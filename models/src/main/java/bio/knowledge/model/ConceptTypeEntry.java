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
package bio.knowledge.model;

import org.neo4j.ogm.annotation.NodeEntity;

import bio.knowledge.model.core.neo4j.Neo4jAbstractDatabaseEntity;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.ontology.mapping.NameSpace;

/**
 * @author Richard
 * December 14, 2017 Revision: move towards external RDF/OWL data typing of concepts
 *
 */
@NodeEntity(label="ConceptType")
public class ConceptTypeEntry extends Neo4jAbstractDatabaseEntity {
	
	private String objectId ;
	private String baseUri ;
	private String uri;
	private String prefix ;
    private String curie;
	private String label ;
	private String definition ;

	/**
	 * 
	 */
	public ConceptTypeEntry() {
		super();
	}
	
	/**
	 * 
	 * @param baseUri
	 * @param prefix
	 * @param identifier
	 * @param label
	 * @param definition
	 */
	public ConceptTypeEntry(
			String baseUri, 
			String prefix, 
			String identifier, 
			String label, 
			String definition
	) {
		this.objectId = identifier;
		this.baseUri    = baseUri;
		this.uri        = baseUri+identifier;
		this.prefix     = prefix;
		this.curie      = prefix+":"+identifier;
		this.label      = label;
		this.definition = definition;
	}

	/**
	 * 
	 * @param biolinkTerm
	 */
	public ConceptTypeEntry(BiolinkTerm biolinkTerm) {
		this(
			NameSpace.BIOLINK.getBaseIri(),
			NameSpace.BIOLINK.getPrefix(),
			biolinkTerm.getObjectId(),
			biolinkTerm.getLabel(),
			biolinkTerm.getDefinition()
		);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		ConceptTypeEntry type2 = (ConceptTypeEntry)other;
		// ConceptTypeEntry identity based on CURIE
		return this.getCurie().equals(type2.getCurie());
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getCurie().hashCode();
	}

	/**
	 * 
	 * @param baseUri
	 */
	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}
	
	/**
	 * 
	 * @return the baseline Uniform Resource Identifier (URI or IRI) of the authority for this Concept Type entry
	 */
	public String getBaseUri() {
		return baseUri;
	}
	
	/**
	 * 
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * 
	 * @return the prefix of the ConceptName authority ("base URI") name space used for CURIE
	 */
	public String getPrefix() {
		return prefix ;
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
	 * @return
	 */
	public String getObjectId() {
		return objectId ;
	}

	/**
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label ;
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() {
		return label ;
	}

	/**
	 * 
	 * @param definition
	 */
	public void setDefinition(String definition) {
		this.definition = definition ;
	}
    
	/**
	 * 
	 * @return
	 */
	public String getDefinition() {
		return definition ;
	}
    
	/**
	 * 
	 * @param uri Uniform Resource Identifier (also knowns as IRI?) corresponding to this ConceptType
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/** 
	 * @return the Uniform Resource Identifier (also knowns as IRI?) corresponding to this ConceptType
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * 
	 * @param curie corresponding to this ConceptType
	 */
	public void setCurie(String curie) {
		this.curie = curie;
	}
	
	/**
	 * @return the CURIE corresponding to this ConceptType
	 */
	public String getCurie() {
		return curie;
	}
	
	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.model.core.neo4j.Neo4jAbstractDatabaseEntity#toString()
	 */
	@Override
	public String toString() {
		return label;
	}
}
