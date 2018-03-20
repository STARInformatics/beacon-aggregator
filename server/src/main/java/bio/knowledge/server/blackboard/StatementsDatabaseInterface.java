/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.BeaconStatementWrapper;
import bio.knowledge.aggregator.BeaconItemWrapper;
import bio.knowledge.aggregator.DatabaseInterface;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.Query;
//import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.StatementsQueryInterface;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.Concept;
import bio.knowledge.model.Predicate;
import bio.knowledge.model.Statement;
import bio.knowledge.model.neo4j.Neo4jGeneralStatement;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementPredicate;
import bio.knowledge.server.model.ServerStatementSubject;

/**
 * @author richard
 *
 */
@Component
public class StatementsDatabaseInterface 
		implements DatabaseInterface<
						BeaconStatement,
						ServerStatement,
						StatementsQueryInterface
					> {
	
	@Autowired private StatementRepository  statementRepository;

	@Override
	public boolean cacheData(
			KnowledgeBeacon kb, 
			BeaconItemWrapper<BeaconStatement> beaconItemWrapper, 
			String queryString
	) {

		return false;
	}

	@Override
	public List<ServerStatement> getDataPage(
				Query<StatementsQueryInterface> query, 
				List<Integer> beacons
	) {
		//String queryString = query.makeQueryString();
		
		StatementsQueryInterface statementQuery = query.getQuery();
		
		String[] sources = split(statementQuery.getSource());
		String[] relationIds = split(statementQuery.getRelations());
		String[] targets = split(statementQuery.getTarget());
		String[] semanticGroups = split(statementQuery.getConceptTypes());
		String[] filter = split(statementQuery.getKeywords());

		List<Map<String, Object>> results = statementRepository.findStatements(
				sources, relationIds, targets, filter, semanticGroups, 
				statementQuery.getPageNumber(), statementQuery.getPageSize()
		);

		List<ServerStatement> serverStatements = new ArrayList<ServerStatement>();
		for (Map<String, Object> result : results) {

			Statement neo4jStatement = (Statement) result.get("statement");

			Concept neo4jObject = (Concept) result.get("object");
			Concept neo4jSubject = (Concept) result.get("subject");

			Predicate neo4jPredicate = (Predicate) result.get("relation");

			ServerStatementObject serverObject = new ServerStatementObject();
			ServerStatementSubject serverSubject = new ServerStatementSubject();
			ServerStatementPredicate serverPredicate = new ServerStatementPredicate();
			serverObject.setClique(neo4jObject.getClique());

//			serverObject.setId(neo4jObject.getId());
			serverObject.setName(neo4jObject.getName());
			serverObject.setType(neo4jObject.getType().getName());
			
			serverSubject.setClique(neo4jSubject.getClique());
//			serverSubject.setId(neo4jSubject.getId());
			serverSubject.setName(neo4jSubject.getName());

			serverSubject.setType(neo4jSubject.getType().getName());
			serverPredicate.setName(neo4jPredicate.getName());
			serverPredicate.setId(neo4jPredicate.getId());

			ServerStatement serverStatement = new ServerStatement();
			serverStatement.setId(neo4jStatement.getId());
			serverStatement.setObject(serverObject);
			serverStatement.setSubject(serverSubject);
			serverStatement.setPredicate(serverPredicate);
			serverStatements.add(serverStatement);
		}
		return serverStatements;
	}
}