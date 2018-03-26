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
import org.neo4j.ogm.annotation.Transient;

import bio.knowledge.model.core.neo4j.Neo4jAbstractDatabaseEntity;

/**
 * @author Richard
 * December 14, 2017 Revision: move towards external RDF/OWL data typing of concepts
 *
 */
@NodeEntity(label="ConceptType")
public class ConceptTypeEntry extends Neo4jAbstractDatabaseEntity {
	
	private String baseUri ;
	private String prefix ;
	private String identifier ;
	private String name ;
	private String definition ;

	public ConceptTypeEntry(
			String baseUri, 
			String prefix, 
			String identifier, 
			String name, 
			String definition
	) {
		this.baseUri    = baseUri;
		this.prefix     = prefix;
		this.identifier = identifier;
		this.name       = name;
		this.definition = definition;
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
	 * @return the baseline Uniform Resource Identifier (URI or IRI) of the authority for this Concept Type entry
	 */
	public String getBaseUri() {
		return baseUri;
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
	 * @return
	 */
	public String getIdentifier() {
		return identifier ;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name ;
	}

	/**
	 * 
	 * @return
	 */
	public String getDefinition() {
		return definition ;
	}

    @Transient
    private String uri=null;
    
	/** 
	 * @return the Uniform Resource Identifier (also knowns as IRI?) corresponding to this ConceptType
	 */
	public String getUri() {
		if(uri == null) uri = baseUri+identifier;
		return uri;
	}

    @Transient
    private String curie=null;
    
	/**
	 * @return the CURIE corresponding to this ConceptType
	 */
	public String getCurie() {
		if(curie == null) curie = prefix+":"+identifier;
		return curie;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}
