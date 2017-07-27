package bio.knowledge.database.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import bio.knowledge.model.ConceptClique;

public interface ConceptCliqueRepository extends GraphRepository<ConceptClique> {
	@Query(
			" MATCH (c:ConceptClique) WHERE ANY (x IN {conceptIds} WHERE x IN c.conceptIds) " +
			" RETURN c LIMIT 1 "
	)
	public ConceptClique getConceptClique(@Param("conceptIds") List<String> conceptIds);
}
