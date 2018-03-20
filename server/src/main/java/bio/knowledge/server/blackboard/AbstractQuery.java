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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import bio.knowledge.aggregator.QueryPagingInterface;

/**
 * @author richard
 *
 */
public abstract class AbstractQuery implements QueryPagingInterface {
	
	private final String queryId ;
	private final Date timestamp;
	
	private final BeaconHarvestService beaconHarvestService ;
	
	protected AbstractQuery(BeaconHarvestService beaconHarvestService) {
		this.beaconHarvestService = beaconHarvestService;
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
		return pageNumber;
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
		return pageSize;
	}	
	
	private final static int sanitizeInt(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}
	
	/**
	 * 
	 * @return
	 */
	public int makeThreshold() {
		pageNumber = sanitizeInt(pageNumber);

		pageSize = sanitizeInt(pageSize);

		return ((pageNumber - 1) * pageSize) + pageSize;
	}
	

	private List<Integer> queryBeacons;

	/**
	 * 
	 * @param beacons
	 */
	public void setQueryBeacons(List<Integer> beacons) {
		if(queryBeacons!=null)
			queryBeacons = beacons;
		else
			queryBeacons = new ArrayList<Integer>();
	}

	/**
	 * 
	 */
	public List<Integer> getQueryBeacons() {
		if(queryBeacons==null)
			queryBeacons = new ArrayList<Integer>();
		return queryBeacons;
	}

	private List<Integer> beaconsToHarvest;
	
	/**
	 * 
	 * @param beacons
	 */
	public void setBeaconsToHarvest(List<Integer> beacons) {
		if(beaconsToHarvest!=null)
			beaconsToHarvest = beacons;
		else
			beaconsToHarvest = new ArrayList<Integer>();
	}
	
	/**
	 * Beacons to harvest may be a subset of the total QueryBeacons specified, 
	 * if some beacons were harvested for a given query in the past
	 * 
	 * @return List<Integer> of Knowledge Beacon index identifiers
	 */
	public List<Integer> getBeaconsToHarvest() {
		return beaconsToHarvest;
	}
}
