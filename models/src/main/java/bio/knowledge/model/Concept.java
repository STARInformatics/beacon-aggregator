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

import java.util.Optional;
import java.util.Set;

import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;

public interface Concept {
	
	/**
	 * 
	 * @param clique
	 */
	public void setClique(Neo4jConceptClique clique);
    
	/**
	 * 
	 * @return
	 */
    public Neo4jConceptClique getClique();
    
    /**
     * 
     * @param name
     */
    public void setName(String name);
    
    /**
     * 
     * @return
     */
    public String getName();
    
    /**
     * @param conceptType
     */
    public void setTypes(Set<Neo4jConceptCategory> conceptType);
    
    /**
     * @param conceptType
     */
    public Set<Neo4jConceptCategory> getTypes();
    
    /**
     * A default concept type (if the Concept is tagged with more than one type)
     * @return
     */
    public Optional<Neo4jConceptCategory> getType();
    
    /**
     * @return Set of Integer index identifiers citing this Concept
     */
	public Set<Integer> getCitingBeacons();
	
	/**
	 * 
	 * @return Set of local concept identifiers from beacons citing this Concept
	 */
	public Set<String> getCitedIds();
    
}
