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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import bio.knowledge.aggregator.DatabaseInterface;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.database.repository.aggregator.QueryTrackerRepository;
import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.aggregator.neo4j.Neo4jQueryTracker;

/**
 * @author richard
 *
 */
public abstract class CoreDatabaseInterface<Q,B,S> implements DatabaseInterface<Q,B,S> {

	// Child classes of this abstract class should be Spring @Components which set this Autowired field (?)
	@Autowired private QueryTrackerRepository  trackerRepository;
	
	/**
	 * @return List of index identifiers of Beacons to harvest in a given Query.
	 * 
	 * This list depends on the audit trail of previous queries 
	 * which is tracked in the database (hence, this method call)
	 * 
	 * @param query session associated with the query of interest
	 * @return List of beacon index identifiers (of beacons to target for harvesting)
	 */
	 public List<Integer> getBeaconsToHarvest(QuerySession<Q> query) {
		
		String queryString = query.makeQueryString();
		
		List<Integer> queryBeacons = query.getQueryBeacons();
		
		// Initialize BeaconCallMap catalog with all QueryBeacons as keys
		Map< Integer, CompletableFuture<Integer>> beaconCallMap = query.getBeaconCallMap();
		for(Integer beacon: queryBeacons) {
			beaconCallMap.put(beacon, null);
		}
		
		List<Integer> beaconsToHarvest = new ArrayList<Integer>();
		
		Neo4jQueryTracker tracker = trackerRepository.findByQueryString(queryString);
		
		if( tracker != null ) {
			
			List<Integer> beaconsHarvested = tracker.getBeaconsHarvested();
			
			// Take Beacon subtraction Set here?
			Set<Integer> beaconSet = new HashSet<Integer>(queryBeacons);
			beaconSet.removeAll(beaconsHarvested);
			beaconsToHarvest = new ArrayList<Integer>(beaconSet);
			
			// reset beaconsHarvested to all QueryBeacons
			tracker.setBeaconsHarvested(queryBeacons);

		} else {
			tracker = new Neo4jQueryTracker(queryString,queryBeacons);
			// We assume that all Beacons need to be harvested now
			beaconsToHarvest = queryBeacons;
		}
		
		/*
		 * Save updated tracker
		 * Not otherwise persisted in memory (for now)
		 */
		tracker = trackerRepository.save(tracker);
		
		query.setQueryTracker((QueryTracker)tracker);
		
		return new ArrayList<Integer>(beaconsToHarvest);
	}
}
