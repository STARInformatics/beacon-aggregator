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
import java.util.Map;

import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryBeaconStatus;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;

/**
 * @author richard
 *
 */
public class ConceptsQuery extends AbstractQuery implements ConceptsQueryUpdateInterface {
	
	private final ServerConceptsQuery query;
	private final ServerConceptsQueryStatus status;
	private final ServerConceptsQueryResult results;
	
	/**
	 * 
	 */
	public ConceptsQuery() {
		
		super();
		
		query = new ServerConceptsQuery();
		query.setQueryId(getQueryId());
		
		status = new ServerConceptsQueryStatus();
		status.setQueryId(getQueryId());
		
		results = new ServerConceptsQueryResult();
		results.setQueryId(getQueryId());
	}
	
	public ServerConceptsQuery getQuery(String keywords, String conceptTypes, List<Integer> beacons) {
		query.setKeywords(keywords);
		query.setTypes(conceptTypes);
		
		
		concepts = beaconHarvestService.harvestConcepts(
				keywords, conceptTypes,
				pageNumber, pageSize,
				beacons, queryId,
				conceptsDatabaseInterface
			);		
		
		return query;
	}
	
	public ServerConceptsQueryStatus getQueryStatus(List<Integer> beacons) {
		/*
		 *  TODO: also need to check beacons here 
		 *  against recorded default query list of beacons?
		 */
		
		// check status of query
		List<ServerConceptsQueryBeaconStatus> bsList = queryStatus.getStatus();
		for( Integer beacon : beacons ) {
			ServerConceptsQueryBeaconStatus bs = new ServerConceptsQueryBeaconStatus();
			bs.setBeacon(beacon);
			
			// Load beacon status here!
			
			bsList.add(bs);
		}
		return status;
	}
	
	public ServerConceptsQueryResult getQueryResults(Integer pageNumber, Integer pageSize, List<Integer> beacons) {
		

		/*
		 *  TODO: also need to filter beacons here against default query list of beacons?
		 */
		
		// TODO: retrieve and load the results here!
		// Should be a simple database quuey at this point
		// subject only to whether or not the given beacons have data?
		// should the user be warned if they ask for beacons that had error 
		// or are incomplete, or should it silentely fail for such beacons?
		concepts = 
				getConceptsFromDatabase(
						keywords, conceptTypes, 
						pageNumber, pageSize,
						beacons
				);
		return results;
	}
}
