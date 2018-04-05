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
package bio.knowledge.model.neo4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;

@NodeEntity(label="Statement")
public class Neo4jStatement {
	
	@Id @GeneratedValue
	private Long dbId;
	
	private String statementId;
	
	@Relationship(type="SUBJECT")
    private Neo4jConcept subject;
    
	@Relationship(type="RELATION")
    private Neo4jRelation relation;

	@Relationship(type="OBJECT")
    private Neo4jConcept object;
	
	@Relationship(type="QUERY", direction = Relationship.INCOMING)
	private Set<QueryTracker> queries = new HashSet<QueryTracker>();
	
	@Relationship(type="SOURCE_BEACON", direction = Relationship.OUTGOING)
	private Set<Neo4jKnowledgeBeacon> beacons = new HashSet<Neo4jKnowledgeBeacon>();
	
	public boolean addQuery(QueryTracker query) {
		return this.queries.add(query);
	}
	
	public Set<QueryTracker> getQueries() {
		return Collections.unmodifiableSet(queries);
	}
	
	public boolean addBeacon(Neo4jKnowledgeBeacon beacon) {
		return this.beacons.add(beacon);
	}
	
	public Set<Neo4jKnowledgeBeacon> getBeacons() {
		return Collections.unmodifiableSet(beacons);
	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String id) {
		this.statementId = id;
	}

	public Neo4jConcept getSubject() {
		return subject;
	}

	public void setSubject(Neo4jConcept subject) {
		this.subject = subject;
	}

	public Neo4jRelation getRelation() {
		return relation;
	}

	public void setRelation(Neo4jRelation relation) {
		this.relation = relation;
	}

	public Neo4jConcept getObject() {
		return object;
	}

	public void setObject(Neo4jConcept object) {
		this.object = object;
	}

}
