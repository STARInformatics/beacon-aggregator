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

import bio.knowledge.model.core.IdentifiedEntity;

public interface Evidence extends IdentifiedEntity {

	/**
	 * @return
	 */
	void setStatement(Statement statement);

	/**
	 * @return
	 */
	Statement getStatement();

	/**
	 * 
	 * @param annotations
	 */
	void setAnnotations(Set<Annotation> annotations);

	/**
	 * 
	 * @param annotation
	 */
	void addAnnotation(Annotation annotation);

	/**
	 * 
	 * @return
	 */
	Set<Annotation> getAnnotations();

	/**
	 * @param count of number of Annotations in Evidence
	 */
	void setCount(Integer count);

	/**
	 * @param increment count of number of Annotations in Evidence
	 */
	void incrementCount();

	/**
	 * @return the 'count' of the Evidence Annotations (i.e. number of independent pieces of Evidence)
	 */
	Integer getCount();

	/* (non-Javadoc)
	 * @see bio.knowledge.model.core.neo4j.Neo4jIdentifiedEntity#toString()
	 */
	String toString();

	/**
	 * 
	 */
	int compareTo(IdentifiedEntity other);

}