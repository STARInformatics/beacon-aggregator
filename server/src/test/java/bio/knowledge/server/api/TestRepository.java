package bio.knowledge.server.api;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends Neo4jRepository<Object, Long> {
	@Query("match (n) detach delete n")
	void clearDatabase();
	
	@Query("match (n) return count(n) = 0")
	boolean isEmpty();
}