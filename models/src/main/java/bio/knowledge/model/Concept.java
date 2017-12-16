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

import java.util.Set;

import bio.knowledge.model.core.AnnotatedEntity;
import bio.knowledge.model.core.IdentifiedEntity;

public interface Concept extends IdentifiedEntity, AnnotatedEntity {
	
	public static final String SEMANTIC_GROUP_FIELD_START = "[" ;
	public static final String SEMANTIC_GROUP_FIELD_END   = "]" ;

	/**
	 * 
	 * @param the Concept Semantic Group 
	 * Should generally be set at node creation, but sometimes not?
	 */
	void setSemanticGroup(SemanticGroup semanticGroup);

	/**
	 * 
	 * @return the Explicit Concept SemanticGroup 
	 */
	SemanticGroup getSemanticGroup();

	/**
	 * @return the usage of the Concept in Statements
	 */
	Long getUsage();

	/**
	 * @param usage to set counting the number of Concept used in Statements
	 */
	void setUsage(Long usage);

	/**
	 * @param increment to add to count of the number of Concept used in Statements
	 */
	void incrementUsage(Long increment);

	/**
	 * @param increment by one the count of the number of Concept used in Statements
	 */
	void incrementUsage();

	/**
	 * 
	 * @param library of archived ConceptMaps associated with the Concept
	 */
	void setLibrary(Library library);

	/**
	 * @return Library of archived ConceptMaps associated with the Concept
	 */
	Library getLibrary();

	/**
	 * @return the Genetic Home References identifier (for genes and disorders)
	 */
	String getGhr();

	/**
	 * @param ghr the Genetic Home References identifier to set
	 */
	void setGhr(String ghr);

	/**
	 * 
	 * @return the Human Metabolome DataBase identifier (as a string)
	 */
	String getHmdbId();

	/**
	 * @param hmdbId the Human Metabolome DataBase identifier (as a string) to set
	 */
	void setHmdbId(String hmdbId);

	/**
	 * @return any associated identifier for Chemical Entities of Biological Interest (ChEBI)
	 */
	String getChebi();

	/**
	 * @param chebi associated identifier for Chemical Entities of Biological Interest (ChEBI)
	 */
	void setChebi(String chebi);

	/**
	 * 
	 * @return
	 */
	Set<String> getCrossReferences();

	/**
	 * 
	 * @return
	 */
	Set<String> getTerms();

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	String toString();

	String getName();

}