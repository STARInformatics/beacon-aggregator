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
package bio.knowledge.aggregator;

import java.util.List;

/**
 * @author richard
 *
 */
public interface DatabaseInterface<
									Q, // *sQueryInterface
									B, // Beacon*
									S  // Server*
								  >    // where '*' is 'Concept', 'Statement', etc. 
{
	/**
	 * 
	 * @param query
	 * @return
	 */
	public List<Integer> getBeaconsToHarvest(QuerySession<Q> query);
	
	/**
	 * March 24, 2018 - new method to load data into blackboard graph database (replacing 'cacheData')
	 * 
	 * @param query
	 * @param results
	 * @param beacon
	 */
	public void loadData(QuerySession<Q> query, List<B> results, Integer beaconId);
	
	/**
	 * 
	 * @param terms
	 * @param deliminator
	 * @return
	 */
	default public String[] split(String terms, String deliminator) {
		return terms != null && !terms.isEmpty() ? terms.split(deliminator) : null;
	}

	/**
	 * 
	 * @param terms
	 * @return
	 */
	default public String[] split(String terms) {
		return split(terms, " ");
	}
	
	/**
	 * 
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param queryString
	 * @return
	 */
	public List<S> getDataPage(QuerySession<Q> query, List<Integer> beacons);

	/**
	 * Get the number of items in the database from the given QuerySession
	 * that were the result of the given beacon.
	 */
	public Integer getDataCount(QuerySession<Q> query, int beaconId);
}