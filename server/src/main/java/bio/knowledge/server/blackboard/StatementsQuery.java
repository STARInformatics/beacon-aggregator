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

import org.springframework.beans.factory.annotation.Autowired;

import bio.knowledge.aggregator.Query;
import bio.knowledge.aggregator.StatementsQueryInterface;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementsQuery;
import bio.knowledge.server.model.ServerStatementsQueryBeaconStatus;
import bio.knowledge.server.model.ServerStatementsQueryResult;
import bio.knowledge.server.model.ServerStatementsQueryStatus;

/**
 * @author richard
 *
 */
public class StatementsQuery extends AbstractQuery implements Query<StatementsQueryInterface>{
	
	@Autowired private BeaconHarvestService beaconHarvestService;
	@Autowired private StatementsDatabaseInterface statementsDatabaseInterface;

	private final ServerStatementsQuery query;
	private final ServerStatementsQueryStatus status;
	private final ServerStatementsQueryResult results;
	
	public StatementsQuery(BeaconHarvestService beaconHarvestService) {
		super(beaconHarvestService);
		
		query = new ServerStatementsQuery();
		query.setQueryId(getQueryId());
		
		status = new ServerStatementsQueryStatus();
		status.setQueryId(getQueryId());
		
		results = new ServerStatementsQueryResult();
		results.setQueryId(getQueryId());
	}

	@Override
	public String makeQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int makeThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public ServerStatementsQuery getQuery(
			String source, String relations, String target, 
			String keywords, String conceptTypes, 
			List<Integer> beacons
	) {
		
		query.setSource(source);
		query.setRelations(relations);
		query.setTarget(target);
		query.setKeywords(keywords);
		query.setTypes(conceptTypes);
		
		setQueryBeacons(beacons);
		
		beaconHarvestService.initiateStatementsHarvest(this);	
		
		return query;
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
	public String setRelations() {
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
	public String getKeywords() {
		return query.getKeywords();
	}

	/**
	 * 
	 */
	public String getConceptTypes() {
		return query.getTypes();
	}

	public ServerStatementsQueryStatus getQueryStatus( List<Integer> beacons ) {
		
		/*
		 *  TODO: also need to check beacons here against default query list of beacons?
		 */
		
		// check status of query
		List<ServerStatementsQueryBeaconStatus> bsList = status.getStatus();
		for( Integer beacon : beacons ) {
			ServerStatementsQueryBeaconStatus bs = new ServerStatementsQueryBeaconStatus();
			bs.setBeacon(beacon);
			
			// TODO: Retrieve Beacon Statements query status here!
			
			bsList.add(bs);
		}
		
		return status;
	}
	
	public ServerStatementsQueryResult getQueryResults(Integer pageNumber, Integer pageSize, List<Integer> beacons) {
		
		
		// Seems redundant, but...
		setPageNumber(pageNumber);
		setPageSize(pageNumber);
		
		// ...Also need to also set the Server DTO sent back
		results.setPageNumber(pageSize);
		results.setPageSize(pageSize);

		List<ServerStatement> statements = 
				statementsDatabaseInterface.getDataPage(this,beacons);
		
		results.setResults(statements);
		
		return results;
	}
}
