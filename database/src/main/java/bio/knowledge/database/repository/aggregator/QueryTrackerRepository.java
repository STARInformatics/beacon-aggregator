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
package bio.knowledge.database.repository.aggregator;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bio.knowledge.model.aggregator.neo4j.Neo4jQueryTracker;

/**
 * This repository manages Knowledge Beacon Aggregator / Blackboard QueryTracker query audit objects.
 * 
 * @author richard
 *
 */
@Repository
public interface QueryTrackerRepository extends Neo4jRepository<Neo4jQueryTracker, Long> {

	@Query("match (n:QueryTracker {queryString: {queryString}})-[r:QUERY]->(q:Query) return n, r")
	Neo4jQueryTracker find(@Param("queryString") String queryString);

	String FIND_QUERY = "match (n:QueryTracker {queryString:{queryString}})-[r:QUERY]->(q:Query {beaconId:{beaconId}}) ";

	String UPDATE_QUERY_STATUS = FIND_QUERY + "set " +
			"q.status = coalesce({httpCode}, q.status), " +
			"q.discovered = coalesce({discovered}, q.discovered), " +
			"q.processed = coalesce({processed}, q.processed), " +
			"q.count = coalesce({count}, q.count);";

	/**
	 * Updates only if the values passed into this function are not null. And so passing null for an argument of
	 * this method will have no effect on that corresponding database field.
	 */
	@Query(UPDATE_QUERY_STATUS)
	void updateQueryStatusState(
			@Param("queryString") String queryString,
			@Param("beaconId") Integer beaconId,
			@Param("httpCode") Integer httpCode,
			@Param("discovered") Integer discovered,
			@Param("processed") Integer processed,
			@Param("count") Integer count
	);

	@Query(FIND_QUERY + "set q.processed = coalesce(q.processed, 0) + {processed}")
	void incrementProcessed(
			@Param("queryString") String queryString,
			@Param("beaconId") Integer beaconId,
			@Param("processed") Integer processed
	);
}
