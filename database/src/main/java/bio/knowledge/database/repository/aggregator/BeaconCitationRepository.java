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

import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;

/**
 * This repository manages Knowledge Beacon Aggregator / Blackboard QueryTracker query audit objects.
 * 
 * @author richard
 *
 */
@Repository
public interface BeaconCitationRepository extends Neo4jRepository<Neo4jBeaconCitation, Long> {

	/**
	 * 
	 * @param beaconId
	 * @param objectId
	 * @return
	 */
	@Query(
			" MATCH (citation:BeaconCitation)-[:SOURCE_BEACON]->(beacon:KnowledgeBeacon) " +
			" WHERE  beacon.beaconId = {beaconId} AND citation.objectId = {objectId}" +
			" RETURN citation " +
			" LIMIT 1 "
	)
	public Neo4jBeaconCitation findByBeaconAndObjectId(@Param("beaconId") Integer beaconId, @Param("objectId") String objectId);
}
