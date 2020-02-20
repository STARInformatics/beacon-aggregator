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
package bio.knowledge.model.aggregator.neo4j;

import java.util.*;

import org.neo4j.ogm.annotation.NodeEntity;

import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.core.neo4j.Neo4jAbstractDatabaseEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author richard
 *
 */
@NodeEntity(label="QueryTracker")
public class Neo4jQueryTracker 
	extends Neo4jAbstractDatabaseEntity
	implements QueryTracker{

	private String queryString;
	private List<Integer> beaconsHarvested ;

	@Relationship(type="QUERY", direction=Relationship.OUTGOING)
	Set<Neo4jQuery> queries = new HashSet<>();
	
	/**
	 * 
	 */
	public Neo4jQueryTracker() { }
	
	/**
	 * 
	 * @param queryString
	 * @param beaconsHarvested
	 */
	public Neo4jQueryTracker(String queryString, List<Integer> beaconsHarvested) {
		super();
		this.queryString = queryString;
		this.beaconsHarvested = beaconsHarvested;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * 
	 * @return
	 */
	public String getQueryString() {
		return queryString;
	}
	
	/**
	 * @param beaconsHarvested the beaconsHarvested to set
	 */
	public void setBeaconsHarvested(List<Integer> beaconsHarvested) {
		this.beaconsHarvested = beaconsHarvested;
	}

	/**
	 * 
	 * @return
	 */
	public List<Integer> getBeaconsHarvested() {
		return beaconsHarvested;
	}

	@Override
	public boolean removeBeaconHarvested(Integer beaconId) {
		return this.beaconsHarvested.remove(beaconId);
	}

	public void setQueries(Set<Neo4jQuery> queries) {
		this.queries = queries;
	}

	public Set<Neo4jQuery> getQueries() {
		return this.queries;
	}

	public Optional<Neo4jQuery> getQuery(int beaconId) {
		return queries.stream().filter(q -> q.getBeaconId() == beaconId).findAny();
	}
}
