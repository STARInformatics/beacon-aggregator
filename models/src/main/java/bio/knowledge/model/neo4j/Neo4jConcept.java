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

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import bio.knowledge.model.Concept;
import bio.knowledge.model.ConceptType;
import bio.knowledge.model.Library;
import bio.knowledge.model.core.neo4j.Neo4jAbstractAnnotatedEntity;
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
	
	@GraphId private Long id;
	
    private String clique;
    private String name;
    private String taxon;
    private ConceptType type;
    
    public Neo4jConcept() {
    	
    }
    
    public Neo4jConcept(String clique , ConceptType type, String name) {
    	this.clique = clique;
    	this.type = type;
    	this.name = name;
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
	public void setType(ConceptType conceptType) {
		this.type = conceptType;
	}

	@Override
	public ConceptType getType() {
		if(type==null) {
    		return Category.OBJC;
    	}
    	return type;
	}
    
    public void setTaxon(String taxon) {
    	this.taxon = taxon;
    }
    
    public String getTaxon() {
    	return this.taxon;
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
