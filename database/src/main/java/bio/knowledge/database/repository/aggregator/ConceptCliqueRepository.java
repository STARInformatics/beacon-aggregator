/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-18 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
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
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;

@Repository
public interface ConceptCliqueRepository extends Neo4jRepository<Neo4jConceptClique,Long> {
	
	public final String getConceptCliquesQuery = 
			"MATCH (c:ConceptClique) WHERE ANY (x IN {conceptIds} WHERE ANY (y IN c.conceptIds WHERE toUpper(x) = toUpper(y))) "+
			//accessionIdFilter+
			"RETURN DISTINCT c as clique, FILTER (x IN {conceptIds} WHERE x IN c.conceptIds) as matchedConceptIds";
	
	public final String getSingleConceptCliqueQuery = 
			"MATCH (c:ConceptClique) WHERE ANY (x IN {conceptIds} WHERE ANY (y IN c.conceptIds WHERE toUpper(x) = toUpper(y))) "+
			//accessionIdFilter+
			"RETURN c LIMIT 1";
	
	public final String getSingleConceptCliqueQueryFromConceptId = 
			"MATCH (c:ConceptClique) WHERE ANY (y IN c.conceptIds WHERE toUpper({conceptId}) = toUpper(y)) "+
			"RETURN c LIMIT 1";
	
	@Query(
			"MATCH (clique:ConceptClique) "
			+ "WHERE toUpper(clique.accessionId) = toUpper({cliqueId}) "
			+ "RETURN DISTINCT clique LIMIT 1"
	)
	public Neo4jConceptClique getConceptCliqueById(@Param("cliqueId") String cliqueId );
	
	/**
	 * Gets all the cliques that share a conceptId with the given clique
	 * @param clique
	 * @return
	 */
	@Query(	" MATCH (clique:ConceptClique), (other:ConceptClique) WHERE " +
			" ID(clique) = {dbId} AND ID(other) <> ID(clique) AND " +
			" ANY(x IN other.conceptIds WHERE ANY(y IN clique.conceptIds WHERE toUpper(x) = toUpper(y)))" +
			" RETURN other;")
	public List<Neo4jConceptClique> getOverlappingCliques(@Param("dbId") Long sourceCliqueDbId);
	
	@Query(	" MATCH (other:ConceptClique) WHERE " +
			" toUpper(other.accessionId) <> toUpper({cliqueId}) AND " +
			" ANY(x IN other.conceptIds WHERE toUpper(x) = toUpper({conceptId}))" +
			" RETURN other;")
	public List<Neo4jConceptClique> getOverlappingCliques(@Param("conceptId") String conceptId, @Param("cliqueId") String cliqueId);

	/**
	 * Returns a ConceptClique that contains any of {@code conceptIds}
	 * @param conceptIds
	 * @return
	 */
	@Query(getSingleConceptCliqueQuery)
	public Neo4jConceptClique getConceptClique(@Param("conceptIds") List<String> conceptIds);
	
	/**
	 * Returns a ConceptClique that contains any of {@code conceptIds}
	 * @param conceptIds
	 * @return
	 */
	@Query(getSingleConceptCliqueQuery)
	public Neo4jConceptClique getConceptClique(@Param("conceptIds") String[] conceptIds);
	
	/**
	 * Returns the ConceptClique that contains {@code conceptId}
	 * @param conceptId
	 * @return
	 */
	@Query(getSingleConceptCliqueQueryFromConceptId)
	public Neo4jConceptClique getConceptCliqueByConceptId(@Param("conceptId") String conceptId);
	
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
