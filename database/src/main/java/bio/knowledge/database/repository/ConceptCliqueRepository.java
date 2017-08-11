package bio.knowledge.database.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import bio.knowledge.model.ConceptClique;

public interface ConceptCliqueRepository extends GraphRepository<ConceptClique> {
	public final String getConceptCliquesQuery = 
			"MATCH (c:ConceptClique) WHERE ANY (x IN {conceptIds} WHERE x IN c.conceptIds) " +
			"RETURN DISTINCT c as clique, FILTER (x IN {conceptIds} WHERE x IN c.conceptIds) as matchedConceptIds";
	public final String getSingleConceptCliqueQuery = "MATCH (c:ConceptClique) WHERE ANY (x in {conceptIds} WHERE x IN c.conceptIds) RETURN c LIMIT 1";
	
	/**
	 * Creates a new ConceptClique with the union of the conceptIds of the two
	 * ConceptCliques specified by {@code id1} and {@code id2}. Make sure to
	 * use the <b>database ID's</b> and not the accessionId.
	 * 
	 * @param id1
	 * @param id2
	 */
	@Query(
			" MATCH (c:ConceptClique) WHERE ID(c) = {id1} WITH c as a " +
			" MATCH (c:ConceptClique) WHERE ID(c) = {id2} WITH a.conceptIds + filter(x IN c.conceptIds WHERE NOT x in a.conceptIds) as union, a as a, c as b " +
			" CREATE (c:ConceptClique {conceptIds : union}) " +
			" DELETE a, b " +
			" RETURN c "
	)
	public ConceptClique mergeConceptCliques(@Param("id1") long id1, @Param("id2") long id2);
	
	/**
	 * Returns a ConceptClique that contains {@code conceptId}
	 * @param conceptIds
	 * @return
	 */
	default ConceptClique getConceptClique(String conceptId) {
		return getConceptClique(new String[] {conceptId});
	}
	
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
