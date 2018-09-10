/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-18 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bio.knowledge.aggregator.StatementsQueryInterface;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.server.blackboard.BeaconCall.ReportableSupplier;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementsQuery;
import bio.knowledge.server.model.ServerStatementsQueryBeaconStatus;
import bio.knowledge.server.model.ServerStatementsQueryResult;
import bio.knowledge.server.model.ServerStatementsQueryStatus;

/**
 * @author richard
 *
 */
public class StatementsQuery 
		extends AbstractQuery<
					StatementsQueryInterface,
					BeaconStatement,
					ServerStatement
				> 
		implements StatementsQueryInterface
{

	private final ServerStatementsQuery query;
	private final ServerStatementsQueryStatus status;
	private final ServerStatementsQueryResult results;
	
	public StatementsQuery(
			BeaconHarvestService beaconHarvestService, 
			StatementsDatabaseInterface statementsDatabaseInterface
	) {
		super(beaconHarvestService,statementsDatabaseInterface);
		
		query = new ServerStatementsQuery();
		query.setQueryId(getQueryId());
		
		status = new ServerStatementsQueryStatus();
		status.setQueryId(getQueryId());
		
		results = new ServerStatementsQueryResult();
		results.setQueryId(getQueryId());
	}
	
	public ServerStatementsQuery getQuery(
			String source, List<String> relations, String target, 
			List<String> keywords, List<String> categories,
			List<Integer> beacons
	) {
		
		query.setSource(source);
		query.setRelations(relations);
		query.setTarget(target);
		query.setKeywords(keywords);
		query.setCategories(categories);
		
		setQueryBeacons(beacons);
		
		getHarvestService().initiateBeaconHarvest(this);	
		
		return query;
	}
	
	/**
	 * 
	 */
	@Override
	public StatementsQueryInterface getQuery() {
		return (StatementsQueryInterface)this;
	}

	/**
	 * 
	 */
	public String getSource() {
		return query.getSource();
	}

	/**
	 * 
	 */
	public List<String> getRelations() {
		return query.getRelations();
	}

	/**
	 * 
	 */
	public String getTarget() {
		return query.getTarget();
	}

	/**
	 * 
	 */
	public List<String> getKeywords() {
		return query.getKeywords();
	}

	/**
	 * 
	 */
	public List<String> getConceptCategories() {
		return query.getCategories();
	}


	/**
	 * Returns a query string for a 'Statements' Query harvesting
	 */
	@Override
	public String makeQueryString() {
		return makeQueryString("concepts",getSource(),getRelations(),getTarget(),getKeywords(),getConceptCategories());
	}
	
	/**
	 * 
	 */
	@Override
	protected BeaconStatusInterface createBeaconStatus(Integer beacon) {
		return new ServerStatementsQueryBeaconStatus();
	}

	public ServerStatementsQueryStatus getQueryStatus( List<Integer> beacons ) {

		if(nullOrEmpty(beacons))
			beacons = getQueryBeacons(); // retrieve all beacons if not filtered?
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<BeaconStatusInterface> bsList = 
				(List<BeaconStatusInterface>)(List)status.getStatus();
		bsList.clear();
		
		for( Integer beacon : beacons ) {
			Optional<BeaconStatusInterface> beaconStatus = getBeaconStatus(beacon);
			if(beaconStatus.isPresent())
				bsList.add(beaconStatus.get());
		}
		
		return status;
	}
	
	public ServerStatementsQueryResult getQueryResults(Integer pageNumber, Integer pageSize, List<Integer> beacons) {
		
		// Seems redundant, but...
		setPageNumber(pageNumber);
		setPageSize(pageSize);
		
		// ...Also need to also set the Server DTO sent back
		results.setPageNumber(pageNumber);
		results.setPageSize(pageSize);

		List<ServerStatement> statements = 
				getDatabaseInterface().getDataPage(this,beacons);
		
		results.setResults(statements);
		
		return results;
	}
	
	private List<BeaconStatement> getStatements(Integer beaconId) {
		BeaconHarvestService bhs = getHarvestService() ;
		ExactMatchesHandler emh = bhs.getExactMatchesHandler();
		
		String source = getSource();
		Neo4jConceptClique sourceClique = emh.getClique(source);
		if(sourceClique==null) {
			severeError("queryBeaconForStatements(): source clique '"+source+"' could not be found?") ;
		}

		String target = getTarget();
		Neo4jConceptClique targetClique = null;
		if(!target.isEmpty()) {
			targetClique = emh.getClique(target);
			if(targetClique==null) {
				severeError("queryBeaconForStatements(): target clique '"+target+"' could not be found?") ;
			}
		} else {
			target = null;
		}

		List<String> relations = getRelations();
		String relation = relations.isEmpty() ? null : relations.get(0); 

		List<String> keywords;
		if (getKeywords() == null || getKeywords().isEmpty()) {
			keywords = null;
		} else {
			keywords = getKeywords();
		}
		
		List<String> categories = getConceptCategories();
		categories = categories.isEmpty() ? null : categories;
				
		return bhs.getKnowledgeBeaconService().
					getStatements(
						sourceClique,
						null,
						relation,
						targetClique,
						keywords,
						categories,
						DEFAULT_BEACON_QUERY_SIZE,
						beaconId
					);
	}

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.server.blackboard.AbstractQuery#getQueryResultSupplier(java.lang.Integer)
	 */
	@Override
	public ReportableSupplier<Integer> getQueryResultSupplier(Integer beaconId) {
		return new ReportableSupplier<Integer>() {
			private Integer processed = null;
			private Integer discovered = null;
			
			private final Integer BATCH_SIZE = 10;

			@Override
			public Integer get() {
				List<BeaconStatement> statements = getStatements(beaconId);
				
				this.discovered = statements.size();
				
				List<List<BeaconStatement>> batches = Utilities.buildBatches(statements, BATCH_SIZE);

				for (List<BeaconStatement> batch : batches) {
					getDatabaseInterface().loadData(StatementsQuery.this, batch, beaconId);

					if (processed == null) {
						this.processed = batch.size();
					} else {
						this.processed += batch.size();
					}
				}

				return statements.size();
			}

			@Override
			public Integer reportProcessed() {
				return processed;
			}

			@Override
			public Integer reportDiscovered() {
				return discovered;
			}
			
		};
	}
	
	protected StatementsDatabaseInterface getDatabaseInterface() {
		return (StatementsDatabaseInterface) super.getDatabaseInterface();
	}

}
