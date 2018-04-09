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
import java.util.Optional;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.Concept;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.Neo4jConceptDetail;
import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.umls.Category;
import bio.knowledge.ontology.BiolinkTerm;

/**
 * @author Richard Bruskiewich
 * 
 * Concept is a fundamental currency in Knowledge.Bio, 
 * representing the unit of semantic representation of globally distinct ideas.
 * 
 */
@NodeEntity(label="Concept")
public class Neo4jConcept implements Concept {

	@Id @GeneratedValue
	private Long dbId;

	private String clique;
	private String name;
	private String definition;
	private List<String> synonyms = new ArrayList<String>();
	
	@Relationship(type="SOURCE_BEACON", direction = Relationship.OUTGOING)
	private Set<Neo4jKnowledgeBeacon> beacons = new HashSet<Neo4jKnowledgeBeacon>();

	@Relationship(type="TYPE", direction = Relationship.OUTGOING)
	private Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();

	@Relationship(type="QUERY", direction = Relationship.INCOMING)
	private Set<QueryTracker> queries = new HashSet<QueryTracker>();
	
	@Relationship(type="HAS_DETAIL", direction = Relationship.OUTGOING)
	private Set<Neo4jConceptDetail> details = new HashSet<Neo4jConceptDetail>();

	public Neo4jConcept() { }

	public Neo4jConcept(String clique , ConceptTypeEntry type, String name) {
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
	
	public boolean addBeacon(Neo4jKnowledgeBeacon beacon) {
		return this.beacons.add(beacon);
	}

	public void setClique(String clique) {
		this.clique = clique;
	}

	public String getClique() {
		return this.clique;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setTypes(Set<ConceptTypeEntry> types) {
		this.types = types;
	}
	
	public void addTypes(Set<ConceptTypeEntry> types) {
		this.types.addAll(types);
	}
	
	public void addTypes(ConceptTypeEntry... types) {
		for (ConceptTypeEntry type : types) {
			this.types.add(type);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.model.Concept#getType()
	 */
	@Override
	public Optional<ConceptTypeEntry> getType() {
		if (types.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(types.iterator().next());
		}
	}
	
	public Set<ConceptTypeEntry> getTypes() {
		return types;
	}

	/**
	 * 
	 * @return List of QueryTracker objects
	 */
	public Set<QueryTracker> getQueries() {
		return queries;
	}

	/**
	 * 
	 * @param queries
	 */
	public void setQueries(Set<QueryTracker> queries) {
		this.queries = queries;
	}

	/**
	 * 
	 * @param query
	 */
	public void addQuery(QueryTracker query) {
		this.queries.add(query);
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * 
	 * @param synonyms
	 */
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	/**
	 * 
	 * @return
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * 
	 * @param definition
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/* (non-Javadoc)
	 * @see bio.knowledge.model.neo4j.Concept#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + "[name=" + getName() + "]";
	}

}
