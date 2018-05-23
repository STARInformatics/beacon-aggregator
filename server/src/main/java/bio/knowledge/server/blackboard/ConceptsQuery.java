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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import bio.knowledge.aggregator.ConceptsQueryInterface;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryBeaconStatus;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;

/**
 * @author richard
 *
 */
public class ConceptsQuery 
			extends AbstractQuery<
						ConceptsQueryInterface,
						BeaconConcept,
						ServerConcept
					> 
			implements  ConceptsQueryInterface
{
	
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

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.server.blackboard.AbstractQuery#getQueryResultSupplier(java.lang.Integer)
	 */
	@Override
	public Supplier<Integer> getQueryResultSupplier(Integer beaconId) {
		return () -> {
			try {
				return queryBeaconForConcepts(beaconId);
				
			} catch (Exception e) {
				getQueryTracker().removeBeaconHarvested(beaconId);
				
				throw e;
			}
		};
	}
	
	/*
	 * This method will access the given beacon, 
	 * in a blocking fashion, within the above 
	 * asynchronoous ComputableFuture. Once the
	 * beacon returns its data, this method also 
	 * loads it into the database, then returns 
	 * size of result list
	 * 
	 * @param conceptsQuery
	 * @param beacon
	 * @return
	 */
	private Integer queryBeaconForConcepts(Integer beacon) {

		BeaconHarvestService bhs = getHarvestService();
		
		List<String> categories = getConceptCategories();
		List<String> keywords   = getKeywords();
		
		categories = categories.isEmpty() ? null : categories;
		
		// Call Beacon
		List<BeaconConcept> results = bhs.getKnowledgeBeaconService().getConcepts(
				keywords,
				categories,
				DEFAULT_BEACON_QUERY_SIZE,
				beacon
		);
		
		// Load BeaconConcept results into the blackboard database
		ConceptsDatabaseInterface dbi = 
				(ConceptsDatabaseInterface)getDatabaseInterface();
		
		dbi.loadData(this,results,beacon);
		
		return results.size();
	}

}
