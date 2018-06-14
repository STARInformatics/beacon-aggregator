package bio.knowledge.database.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bio.knowledge.model.neo4j.Node;

@Repository
public interface NodeRepository extends Neo4jRepository<Node,Long> {
	
	@Query("MATCH (n {id: {id}}) RETURN n LIMIT 1")
	public Node getNode(@Param("id") String id);

}
