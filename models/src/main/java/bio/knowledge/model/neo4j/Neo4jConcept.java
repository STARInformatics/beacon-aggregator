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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.Concept;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.umls.Category;

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
	private Long id;

	private String clique;
	private String name;
	private String queryFoundWith;
	private String definition;
	private List<String> synonyms = new ArrayList<String>();

	@Relationship(type="TYPE", direction = Relationship.OUTGOING)
	private Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();

	public Neo4jConcept() { }

	public Neo4jConcept(String clique , ConceptTypeEntry type, String name) {
		this.clique = clique;
		this.name = name;
		this.types.add(type);
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

	@Override
	public ConceptTypeEntry getType() {
		if (types.isEmpty()) {
			return Category.OBJC;
		} else {
			return types.iterator().next();
		}
	}
	
	public Set<ConceptTypeEntry> getTypes() {
		return types;
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

	/**
	 * 
	 * @return
	 */
	public String getQueryFoundWith() {
		return queryFoundWith;
	}

	/**
	 * 
	 * @param queryString
	 */
	public void setQueryFoundWith(String queryString) {
		this.queryFoundWith = queryString;
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
}
