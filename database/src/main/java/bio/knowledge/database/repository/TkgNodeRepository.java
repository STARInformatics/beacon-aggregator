package bio.knowledge.database.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bio.knowledge.model.neo4j.TkgNode;

@Repository
public interface TkgNodeRepository extends Neo4jRepository<TkgNode,Long> {
	
	@Query("MATCH (n:Node {id: {id}}) RETURN n LIMIT 1")
	public TkgNode getNode(@Param("id") String id);

}
