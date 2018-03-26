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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.RandomStringUtils;

import bio.knowledge.Util;
import bio.knowledge.aggregator.DatabaseInterface;
import bio.knowledge.aggregator.QueryPagingInterface;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.server.controller.HttpStatus;

/**
 * @author richard
 *
 */
public abstract class AbstractQuery<
										Q, // *sQueryInterface
										B, // Beacon*
										S  // Server*
									>      // where '*' is 'Concept', 'Statement', etc. 

		implements QuerySession<Q>, QueryPagingInterface, Util, HttpStatus {
	
	private final BeaconHarvestService beaconHarvestService ;
	private final DatabaseInterface<Q,B,S> databaseInterface;
	private final String queryId ;
	private final Date timestamp;
	
	private List<Integer> queryBeacons;
	
	/*
	 * Map of CompletableFutures wrapping the API calls made to the Beacons for knowledge harvesting
	 */
	private Map<
				Integer,
				CompletableFuture<Integer>
			> beaconCallMap = new HashMap< Integer, CompletableFuture<Integer>>();
	
	protected AbstractQuery(
			BeaconHarvestService     beaconHarvestService, 
			DatabaseInterface<Q,B,S> databaseInterface
	) {
		this.beaconHarvestService = beaconHarvestService;
		this.databaseInterface = databaseInterface;
		
		queryId = RandomStringUtils.randomAlphanumeric(20);
		timestamp = new Date();
	}

	/**
	 * 
	 * @return
	 */
	public String getQueryId() {
		return queryId;
	}
	
	protected Date getTimestamp() {
		return timestamp;
	}
	
	protected BeaconHarvestService getHarvestService() {
		return beaconHarvestService;
	}
	
	protected DatabaseInterface<Q,B,S> getDatabaseInterface() {
		return databaseInterface;
	}

	/**
	 * 
	 * @return
	 */
	public Map<
		Integer,
		CompletableFuture<Integer>
	> getBeaconCallMap() {
		return beaconCallMap;
	}

	/**
	 * 
	 * @param beacons
	 */
	public void setQueryBeacons(List<Integer> beacons) {
		queryBeacons = beacons;
	}

	/**
	 * 
	 */
	public List<Integer> getQueryBeacons() {
		if(nullOrEmpty(queryBeacons))
			queryBeacons = beaconHarvestService.getAllBeacons();
		return queryBeacons;
	}
	
	private final static int sanitizeInt(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}
	
	private int pageNumber = 1;

	/**
	 * 
	 * @param pageNumber
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	/**
	 * 
	 */
	public int getPageNumber() {
		return sanitizeInt(pageNumber);
	}

	private int pageSize = 1;

	/**
	 * 
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}	
	
	/**
	 * 
	 */
	public int getPageSize() {
		return sanitizeInt(pageSize);
	}	
	
	/**
	 * 
	 * @return
	 */
	public int makeThreshold() {
		return ((getPageNumber() - 1) * getPageSize()) + getPageSize();
	}

	private List<Integer> beaconsToHarvest;
	
	/**
	 * Sets a specific set of beacons to harvest
	 * @param beacons
	 */
	public void setBeaconsToHarvest(List<Integer> beacons) {
		beaconsToHarvest = beacons;
	}
	
	/**
	 * Beacons to harvest may be a subset of the total QueryBeacons specified, 
	 * if some beacons were harvested for a given query in the past
	 * 
	 * TODO: we need to do some intelligent triage for repeat queries 
	 * only harvesting beacons not yet harvested for a given queryString?
	 * 
	 * @return List<Integer> of Knowledge Beacon index identifiers
	 */
	public List<Integer> getBeaconsToHarvest() {
		if(nullOrEmpty(beaconsToHarvest))
			beaconsToHarvest = 
				databaseInterface.getBeaconsToHarvest();
		return beaconsToHarvest;
	}
	
	/**
	 * 
	 * @param beacon
	 * @return
	 */
	abstract protected BeaconStatusInterface createBeaconStatus(Integer beacon);
	
	/**
	 * 
	 * @param beacon
	 * @return
	 */
	protected Optional<BeaconStatusInterface> getBeaconStatus(Integer beacon) {
		
		if(beaconCallMap.containsKey(beacon)) {
			
			BeaconStatusInterface bs = createBeaconStatus(beacon);
			
			bs.setBeacon(beacon);
			
			CompletableFuture<Integer> future = beaconCallMap.get(beacon);
			
			if(future.isCompletedExceptionally()) {
				
				bs.setStatus(SERVER_ERROR);
				
			} else if(future.isDone()) {
				
				bs.setStatus(SUCCESS);
				
				try {
					bs.setCount(future.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
					bs.setStatus(SERVER_ERROR);
				} catch (ExecutionException e) {
					e.printStackTrace();
					bs.setStatus(SERVER_ERROR);
				}
				
			} else {
				// query still active?
				bs.setStatus(QUERY_IN_PROGRESS);
			}
			
			return Optional.of(bs);
			
		} else return Optional.empty();
	}
}
