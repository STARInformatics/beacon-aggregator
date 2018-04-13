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

import java.util.List;
import java.util.Set;

import bio.knowledge.model.core.IdentifiedEntity;

public interface Statement extends IdentifiedEntity {

	/**
	 * 
	 * @param subject to be added to the Statement
	 */
	void addSubject(Concept subject);

	/**
	 * @param subjects set to be added with the Statement
	 */
	void setSubjects(List<Concept> subjects);

	/**
	 * @return subjects associated with the Statement
	 */
	List<Concept> getSubjects();

	/**
	 * 
	 * @param subject
	 */
	void setSubject(Concept subject);

	/**
	 * 
	 * @return
	 */
	Concept getSubject();

	/**
	 * @param predicate the predicate to set
	 */
	void setRelation(Predicate relation);

	/**
	 * @return the predicate
	 */
	Predicate getRelation();

	/**
	 * 
	 * @param subject to be added to the Statement
	 */
	void addObject(Concept object);

	/**
	 * @param objects set to be added with the Statement
	 */
	void setObjects(List<Concept> objects);

	/**
	 * @return objects associated with the Statement
	 */
	List<Concept> getObjects();

	/**
	 * 
	 * @param object
	 */
	void setObject(Concept object);

	/**
	 * 
	 * @return
	 */
	Concept getObject();

	/**
	 * 
	 * @param evidence to be associated with the Statement
	 */
	void setEvidence(Evidence evidence);

	/**
	 * @return associated Evidence (e.g. References) supporting the Statement
	 */
	Evidence getEvidence();
    
    /**
     * @return Set of Integer index identifiers citing this Statement
     */
	public Set<Integer> getCitingBeacons();
	
	/**
	 * 
	 * @return Set of local statement identifiers from beacons citing this Statement
	 */
	public Set<String> getCitedIds();

}