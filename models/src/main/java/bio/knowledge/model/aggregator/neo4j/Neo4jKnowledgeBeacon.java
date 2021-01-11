/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
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

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import bio.knowledge.model.aggregator.KnowledgeBeaconEntry;

/**
 * @author Richard
 *
 */
@NodeEntity(label="KnowledgeBeacon")
public class Neo4jKnowledgeBeacon
	implements KnowledgeBeaconEntry {
	
	@Id @GeneratedValue
	private Long dbId;
	
	private Integer beaconId;
	
	public Integer getBeaconId() {
		return this.beaconId;
	}
	
	public void setBeaconId(int beaconId) {
		this.beaconId = beaconId;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Neo4jKnowledgeBeacon) {
			Neo4jKnowledgeBeacon beacon = (Neo4jKnowledgeBeacon) other;
			return this.beaconId.equals(beacon.beaconId);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.beaconId.hashCode();
	}
	
}
