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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import bio.knowledge.Util;

/**
 * @author richard
 *
 */
@Component
public class QueryRegistry implements Util {

	private ConcurrentHashMap<String, AbstractQuery> queryMap =
			new ConcurrentHashMap<String, AbstractQuery>();

	/**
	 * 
	 * @param queryId
	 * @return
	 */
	public boolean isActiveQuery(String queryId) {
		if( nullOrEmpty(queryId) || ! queryMap.containsKey(queryId) )
			return false;
		return true; 
	}
	
	public enum QueryType {
		CONCEPTS,
		STATEMENTS
		;
	}
	
	public AbstractQuery createQuery(QueryType type) {
		
		if(type==null)
			throw new RuntimeException("QueryRegistry createQuery(): null type encountered?");
		
		AbstractQuery queryObject = null;
		
		switch(type) {
			case CONCEPTS:
				queryObject = new ConceptsQuery();
				break;
			case STATEMENTS:
				queryObject = new StatementsQuery();
				break;
			default:
				// Can't be reached?
				throw new RuntimeException("Invalid query type?");
		}
		
		// Identify which beaconss still need to be harvested
		// give that list into the ConceptsQuery
		
		// Record the new active query
		queryMap.put(queryObject.getQueryId(), queryObject);
		
		return queryObject;
	}
	
	public AbstractQuery lookupQuery(String queryId) {
		return queryMap.get(queryId);
	}
	
	/*
	 * Code to clear the registry of older queries.
	 * 
	 * TODO: need to test this and figure out how best to automatically invoke it
	 */
	
	// One hour query time to live, in milliseconds
	private final long TIME_TO_LIVE = 3600000;
	
	private final long THOUSAND = 1000; 
	
	private final void isActive( AbstractQuery q, long now ) {
		if(now - q.getTimestamp().getTime() > TIME_TO_LIVE) {
			/*
			 * Not sure if this will work: 
			 * We're dynamically removing an element 
			 * from the map while iterating within a 'forEachValue'
			 * This is not really a functional operation?
			 */
			queryMap.remove(q.getQueryId());
		}
	}

	/**
	 * 
	 */
	synchronized public void purgeStaleQueries() {
		long now = new Date().getTime();
		queryMap.forEachValue( THOUSAND, q-> isActive(q,now) );
	}

}
