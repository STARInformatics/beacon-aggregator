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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;

/**
 * @author Richard Bruskiewich
 * 
 * Concept is a fundamental currency in Knowledge.Bio, 
 * representing the unit of semantic representation of globally distinct ideas.
 * 
 */
@NodeEntity(label="Concept")
public class Neo4jConcept {

	@Id @GeneratedValue
	private Long dbId;

	private String name;
	private String definition;
	private List<String> synonyms = new ArrayList<String>();
	
	@Relationship(type="BEACON_CITATION", direction = Relationship.OUTGOING)
	private Set<Neo4jBeaconCitation> beaconCitations = new HashSet<Neo4jBeaconCitation>();

	@Relationship(type="TYPE", direction = Relationship.OUTGOING)
	private Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();

	@Relationship(type="QUERY", direction = Relationship.INCOMING)
	private Set<QueryTracker> queries = new HashSet<QueryTracker>();
	
	@Relationship(type="HAS_DETAIL", direction = Relationship.OUTGOING)
	private Set<Neo4jConceptDetail> details = new HashSet<Neo4jConceptDetail>();
	
	@Relationship(type="MEMBER_OF", direction = Relationship.OUTGOING)
	private ConceptClique clique;
	
	public Neo4jConcept() { }

	public Neo4jConcept(ConceptClique clique, ConceptTypeEntry type, String name) {
		this.clique = clique;
		this.name = name;
		this.types.add(type);
	}
	
	public boolean addDetail(Neo4jConceptDetail detail) {
		return this.details.add(detail);
	}
	
	public Set<Neo4jConceptDetail> getDetails() {
		return Collections.unmodifiableSet(this.details);
	}
	
	public Iterable<Neo4jConceptDetail> iterDetails() {
		return () -> this.details.iterator();
	}

	public void setClique(ConceptClique clique) {
		this.clique = clique;
	}

	public ConceptClique getClique() {
		return this.clique;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setTypes(Set<ConceptTypeEntry> types) {
		this.types = types;
	}
	
	public void addType(ConceptTypeEntry type) {
		this.types.add(type);
	}
	
	public void addTypes(Set<ConceptTypeEntry> types) {
		this.types.addAll(types);
	}
	
	public void addTypes(ConceptTypeEntry... types) {
		for (ConceptTypeEntry type : types) {
			this.types.add(type);
		}
	}

	public ConceptTypeEntry getType() {
		if (types.isEmpty()) {
			return null;
		} else {
			return types.iterator().next();
		}
	}
	
	public Set<ConceptTypeEntry> getTypes() {
		return types;
	}

	public Set<QueryTracker> getQueries() {
		return queries;
	}

	public void setQueries(Set<QueryTracker> queries) {
		this.queries = queries;
	}

	public void addQuery(QueryTracker query) {
		this.queries.add(query);
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[name=" + getName() + "]";
	}
	
	public void setBeaconCitations(Set<Neo4jBeaconCitation> beaconCitations) {
		this.beaconCitations = beaconCitations;
	}
	
	public Set<Neo4jBeaconCitation> getBeaconCitations() {
		return beaconCitations;
	}
	
	public boolean addBeaconCitation(Neo4jBeaconCitation citation) {
		return beaconCitations.add(citation);
	}

	public Set<Integer> getCitingBeacons() {
		return beaconCitations.stream().map(b->b.getBeacon().getBeaconId()).collect(Collectors.toSet());
	}
	
	public Set<String> getCitedIds() {
		return beaconCitations.stream().map(b->b.getObjectId()).collect(Collectors.toSet());
	}
}
