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

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.aggregator.BeaconCitation;
import bio.knowledge.model.core.neo4j.Neo4jAbstractDatabaseEntity;

/**
 * @author Richard
 *
 */
@NodeEntity(label="BeaconCitation")
public class Neo4jBeaconCitation  
	extends Neo4jAbstractDatabaseEntity
	implements BeaconCitation {

	@Relationship(type="SOURCE_BEACON", direction = Relationship.OUTGOING)
	private Neo4jKnowledgeBeacon beacon;

	private String objectId;
	
	/**
	 * 
	 */
	public Neo4jBeaconCitation() {
		super();
	}
	
	/**
	 * 
	 * @param beacon
	 * @param objectId
	 */
	public Neo4jBeaconCitation(Neo4jKnowledgeBeacon beacon, String objectId) {
		this.beacon = beacon;
		this.objectId = objectId;
	}
	
	/**
	 * 
	 * @param beacon
	 */
	public void setBeacon(Neo4jKnowledgeBeacon beacon) {
		this.beacon = beacon;
	}

	/**
	 * 
	 */
	public Neo4jKnowledgeBeacon getBeacon() {
		return beacon;
	}

	/**
	 * 
	 * @param objectId
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * 
	 */
	public String getObjectId() {
		return objectId;
	}

}
