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
package bio.knowledge.database.repository.aggregator;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bio.knowledge.model.aggregator.ConceptClique;

@Repository
public interface ConceptCliqueRepository extends GraphRepository<ConceptClique> {
	
	/*
	 * RMB: Oct 10, 2017
	 * It seems a bit tricky to normalize accession identifiers here in Cypher,
	 * so I will simply content myself here with detecting and capturing a candidate 
	 * concept identifier then properly normalize it in the Java business logic later?
	 * 
	 * RMB: oCT 21, 2017
	 * Delegate the accessionId setting task completely to the main code.
	 * Assume that it is already set otherwise when returned since no query here, 
	 * other than the implicit 'save', creates a ConceptClique
	 */
	@Deprecated
	public final String accessionIdFilter = 
			 " SET c.accessionId = "
			  + "CASE "
			  +   "WHEN c.accessionId IS NOT NULL THEN c.accessionId " // don't change the accessionId if already set?
				+ "WHEN ANY (x in c.conceptIds WHERE toLower(x) STARTS WITH \"ncbigene:\") "
				   + "THEN HEAD(FILTER (x in c.conceptIds WHERE toLower(x) STARTS WITH \"ncbigene:\")) "
				
				+ "WHEN ANY (x in c.conceptIds WHERE toLower(x) STARTS WITH \"wd:\") "
				   + "THEN HEAD(FILTER (x in c.conceptIds WHERE toLower(x) STARTS WITH \"wd:\")) "
				
				+ "WHEN ANY (x in c.conceptIds WHERE toLower(x) STARTS WITH \"chebi:\") "
				   + "THEN HEAD(FILTER (x in c.conceptIds WHERE toLower(x) STARTS WITH \"chebi:\")) "
				
				+ "WHEN ANY (x in c.conceptIds WHERE toLower(x) STARTS WITH \"umls:\") "
				   + "THEN HEAD(FILTER (x in c.conceptIds WHERE toLower(x) STARTS WITH \"umls:\")) "
				
				+ "ELSE HEAD(c.conceptIds) "
			  + "END " ;
	
	public final String getConceptCliquesQuery = 
			"MATCH (c:ConceptClique) WHERE ANY (x IN {conceptIds} WHERE x IN c.conceptIds) " +
			//accessionIdFilter+
			"RETURN DISTINCT c as clique, FILTER (x IN {conceptIds} WHERE x IN c.conceptIds) as matchedConceptIds";
	
	public final String getSingleConceptCliqueQuery = 
			"MATCH (c:ConceptClique) WHERE ANY (x in {conceptIds} WHERE x IN c.conceptIds) "+
			//accessionIdFilter+
			"RETURN c LIMIT 1";
	
	@Query(
			"MATCH (clique:ConceptClique) "
			+ "WHERE toUpper(clique.accessionId) = toUpper({cliqueId}) "
			+ "RETURN DISTINCT clique LIMIT 1"
	)
	public ConceptClique getConceptCliqueById(@Param("cliqueId") String cliqueId );

	/**
	 * Returns a ConceptClique that contains any of {@code conceptIds}
	 * @param conceptIds
	 * @return
	 */
	@Query(getSingleConceptCliqueQuery)
	public ConceptClique getConceptClique(@Param("conceptIds") List<String> conceptIds);
	
	/**
	 * Returns a ConceptClique that contains any of {@code conceptIds}
	 * @param conceptIds
	 * @return
	 */
	@Query(getSingleConceptCliqueQuery)
	public ConceptClique getConceptClique(@Param("conceptIds") String[] conceptIds);
	
	/**
	 * Returns a list of maps, each containing a clique and the subset of {@code conceptIds} that are
	 * found within that clique
	 * @param conceptIds
	 * @return
	 */
	@Query(getConceptCliquesQuery)
	public List<Map<String, Object>> getConceptCliques(@Param("conceptIds") List<String> conceptIds);
	
	/**
	 * Returns a list of maps, each containing a clique and the subset of {@code conceptIds} that are
	 * found within that clique
	 * @param conceptIds
	 * @return
	 */
	@Query(getConceptCliquesQuery)
	public List<Map<String, Object>> getConceptCliques(@Param("conceptIds") String[] conceptIds);
	
}
