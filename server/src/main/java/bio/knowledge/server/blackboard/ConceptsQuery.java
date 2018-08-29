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

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import bio.knowledge.aggregator.ConceptsQueryInterface;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.server.blackboard.BeaconCall.ReportableSupplier;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryBeaconStatus;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;

/**
 * @author richard
 *
 */
public class ConceptsQuery extends AbstractQuery<ConceptsQueryInterface, BeaconConcept, ServerConcept> implements  ConceptsQueryInterface {
	
	private final ServerConceptsQuery query;
	private final ServerConceptsQueryStatus status;
	private final ServerConceptsQueryResult results;
	
	/**
	 * 
	 * @param beaconHarvestService
	 */
	public ConceptsQuery(
			BeaconHarvestService beaconHarvestService,
			ConceptsDatabaseInterface conceptsDatabaseInterface
	) {
		super(beaconHarvestService,conceptsDatabaseInterface);

		query = new ServerConceptsQuery();
		query.setQueryId(getQueryId());
		
		status = new ServerConceptsQueryStatus();
		status.setQueryId(getQueryId());
		
		results = new ServerConceptsQueryResult();
		results.setQueryId(getQueryId());
	}
	
	/**
	 * 
	 */
	@Override
	public ConceptsQueryInterface getQuery() {
		return (ConceptsQueryInterface)this;
	}

	/**
	 * 
	 * @param keywords
	 * @param conceptTypes
	 * @param beacons
	 * @return
	 */
	public ServerConceptsQuery getQuery(
			List<String> keywords, 
			List<String> categories,
			List<Integer> beacons
	) {
		
		query.setKeywords(keywords);
		query.setCategories(categories);
		
		/*
		 *  The user has specified what beacons to search with keywords
		 *  matching concepts (names, aliases) of interest to them.
		 *  This specific list of beacons are tagged as the "QueryBeacons"
		 */
		setQueryBeacons(beacons);
		
		getHarvestService().initiateBeaconHarvest(this);		
		
		return query;
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

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.QuerySession#makeQueryString()
	 */
	@Override
	public String makeQueryString() {
		return makeQueryString("concepts",getKeywords(),getConceptCategories());
	}

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.server.blackboard.AbstractQuery#createBeaconStatus(java.lang.Integer)
	 */
	@Override
	protected BeaconStatusInterface createBeaconStatus(Integer beacon) {
		return new ServerConceptsQueryBeaconStatus();
	}

	/**
	 * 
	 * @param beacons
	 * @return
	 */
	public ServerConceptsQueryStatus getQueryStatus(List<Integer> beacons) {

		if(nullOrEmpty(beacons)) {
			beacons = getQueryBeacons(); // retrieve all beacons if not filtered?
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<BeaconStatusInterface> bsList = (List<BeaconStatusInterface>)(List)status.getStatus();
		
		bsList.clear();
		
		/**
		 * Somehow the "beacons" list was being modified, while iterating through it in the for loop.
		 * This was causing a ConcurrentModificationException. I fixed this with a hack, by making
		 * getQueryBeacons return a new list that is a copy of the original one.
		 */
		
		try {		
			for( Integer beacon : beacons ) {
				Optional<BeaconStatusInterface> beaconStatus = getBeaconStatus(beacon);
				
				if(beaconStatus.isPresent()) {
					bsList.add(beaconStatus.get());
				}
			}
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
			throw e;
		}
		
		return status;
	}

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @return
	 */
	public ServerConceptsQueryResult getQueryResults(
			Integer pageNumber, Integer pageSize, List<Integer> beacons
	) {
		
		// Seems redundant, but...
		setPageNumber(pageNumber);
		setPageSize(pageSize);
		
		// ...Also need to also set the Server DTO sent back
		results.setPageNumber(pageNumber);
		results.setPageSize(pageSize);

		if(nullOrEmpty(beacons))
			// retrieve all beacons if not filtered?
			beacons = getQueryBeacons(); 
		
		List<ServerConcept> concepts = 
				getDatabaseInterface().getDataPage(this,beacons);
		
		results.setResults(concepts);
		
		return results;
	}
	
	private List<BeaconConcept> getConcepts(Integer beaconId) {
		BeaconHarvestService bhs = getHarvestService();
		
		List<String> categories = getConceptCategories();
		List<String> keywords   = getKeywords();
		
		categories = categories.isEmpty() ? null : categories;
		
		return bhs.getKnowledgeBeaconService().getConcepts(
				keywords,
				categories,
				DEFAULT_BEACON_QUERY_SIZE,
				beaconId
		);
	}
	
	@Override
	public ConceptsDatabaseInterface getDatabaseInterface() {
		return (ConceptsDatabaseInterface) super.getDatabaseInterface();
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
			
			public final Integer BATCH_SIZE = 10;

			@Override
			public Integer get() {
				List<BeaconConcept> concepts = getConcepts(beaconId);
				
				this.discovered = concepts.size();
				
				List<List<BeaconConcept>> batches = Utilities.buildBatches(concepts, BATCH_SIZE);
				
				for (List<BeaconConcept> batch : batches) {
					getDatabaseInterface().loadData(ConceptsQuery.this, batch, beaconId);
					
					if (processed == null) {
						processed = BATCH_SIZE;
					} else {
						processed += BATCH_SIZE;
					}
				}
				
				return concepts.size();
			}

			@Override
			public Integer reportProcessed() {
				return this.processed;
			}

			@Override
			public Integer reportDiscovered() {
				return this.discovered;
			}
			
		};
	}

}
