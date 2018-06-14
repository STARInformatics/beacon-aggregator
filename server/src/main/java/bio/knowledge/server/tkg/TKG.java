package bio.knowledge.server.tkg;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 * This class allows us to dynamically set the type of an edge. It contains a
 * method for building (merging) an edge, and setting its properties.
 */
@Controller
public class TKG {
	
	@Value("${tkg.uri}") private String uri;
	@Value("${tkg.username}") private String username;
	@Value("${tkg.password}") private String password;

	private Driver driver() {
		return GraphDatabase.driver(uri, AuthTokens.basic(username, password)); 
	}

	/**
	 * Runs a cypher query
	 * <h2>Example</h2>
     * <pre class="doctest:StatementRunnerDocIT#parameterTest">
     * {@code
     * Map<String, Object> parameters = new HashMap<String, Object>();
     * parameters.put("myNameParam", "Bob");
     *
     * StatementResult cursor = session.run( "MATCH (n) WHERE n.name = {myNameParam} RETURN (n)", parameters );
	 * @param queryTemplate
	 * @param properties
	 * @return
	 */
	public StatementResult run(String queryTemplate, Map<String, Object> parameters) {
		try (Driver driver = driver(); Session session = driver.session();) {
			return session.run(queryTemplate, parameters);
		}
	}

	public void mergeEdge(String subjectId, String objectId, String edgeLabel, Property... edge_properties) {
		String query =	"UNWIND {properties} AS row " + 
						"MERGE (s:Node {id: {subjectId}}) MERGE (o:Node {id: {objectId}}) MERGE (s)-[r:" + edgeLabel + "]->(o) " +
						"SET r += row;";
		
		Map<String, Object> properties = new HashMap<String, Object>();
		for (Property property : edge_properties) {
			properties.put(property.name, property.value);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("subjectId", subjectId);
		params.put("objectId", objectId);
		params.put("properties", properties);
		
		run(query, params);
	}

}
