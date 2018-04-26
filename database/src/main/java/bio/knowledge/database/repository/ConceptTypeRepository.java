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
package bio.knowledge.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bio.knowledge.model.ConceptTypeEntry;

/**
 * @author Richard
 *
 */
@Repository
public interface ConceptTypeRepository extends Neo4jRepository<ConceptTypeEntry,Long> {

	/* *
	 * 
	 * @param clique
	 * @return
	 */
	//@Query( "MATCH (concept:Concept)-[:TYPE]->(conceptType:ConceptType) "+
	//		"WHERE concept.clique = {clique} RETURN ID(conceptType)")
	//public Long getConceptTypeByClique(@Param("clique") String clique);
	
	/**
	 * 
	 * @param clique
	 * @return
	 */
	@Query( "MATCH (concept:Concept)-[:TYPE]->(conceptType:ConceptType) "+
			"WHERE concept.clique = {clique} RETURN conceptType")
	public Optional<List<ConceptTypeEntry>> getConceptTypeByClique(@Param("clique") String clique);

	/**
	 * 
	 * @param curie
	 * @return
	 */
	@Query( "MATCH (conceptType:ConceptType) "+
			"WHERE conceptType.curie = {curie} RETURN conceptType")
	public Optional<ConceptTypeEntry> getConceptTypeByCurie(@Param("curie") String curie);
	
	/* *
	 * 
	 * @param dbid
	 * @return
	 */
	//@Query( "MATCH (conceptType:ConceptType) "+
	//		"WHERE ID(conceptType) = {id} RETURN conceptType")
	//public Optional<Map<String,Object>> retrieveByDbId(@Param("id") Long dbid);
}