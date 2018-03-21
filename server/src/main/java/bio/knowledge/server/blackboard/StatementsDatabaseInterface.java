/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.BeaconItemWrapper;
import bio.knowledge.aggregator.BeaconStatementWrapper;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.DatabaseInterface;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.Query;
import bio.knowledge.aggregator.StatementsQueryInterface;

import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconStatementObject;
import bio.knowledge.client.model.BeaconStatementPredicate;
import bio.knowledge.client.model.BeaconStatementSubject;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.Concept;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.Predicate;
import bio.knowledge.model.Statement;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jGeneralStatement;
import bio.knowledge.model.neo4j.Neo4jPredicate;
import bio.knowledge.server.controller.ExactMatchesHandler;
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
	
	@Autowired private ConceptTypeService   conceptTypeService;
	@Autowired private ConceptRepository    conceptRepository;
	@Autowired private ExactMatchesHandler  exactMatchesHandler;
	@Autowired private StatementRepository  statementRepository;

	private interface SimpleBeaconConceptInterface {
		String getId();
		String getName();
		String getType();
	}
	
	private Neo4jConcept loadConcept(SimpleBeaconConceptInterface concept, String queryString) {
		
		Neo4jConcept neo4jConcept = new Neo4jConcept();
		
		ConceptClique clique = 
				exactMatchesHandler.getConceptClique(new String[] { concept.getId() });
		neo4jConcept.setClique(clique.getId());
		
		neo4jConcept.setName(concept.getName());
		
		ConceptTypeEntry conceptType = conceptTypeService.lookUp(concept.getType());
		if(conceptType!=null) {
			List<ConceptTypeEntry> types = new ArrayList<ConceptTypeEntry>();
			types.add(conceptType);
			neo4jConcept.setTypes(types);
		}

		neo4jConcept.setQueryFoundWith(queryString);
		
		if (!conceptRepository.exists(neo4jConcept.getClique(), queryString)) {
			conceptRepository.save(neo4jConcept);
		}
		return neo4jConcept;
	}
	
	@Override
	public boolean cacheData(
			KnowledgeBeacon kb, 
			BeaconItemWrapper<BeaconStatement> beaconItemWrapper, 
			String queryString
	) {
		BeaconStatementWrapper statementWrapper = (BeaconStatementWrapper) beaconItemWrapper;
		BeaconStatement statement = statementWrapper.getItem();

		String stmtId = statement.getId();
		
		Neo4jGeneralStatement neo4jStatement ;
		if (!statementRepository.exists(stmtId, queryString)) {
			neo4jStatement = statementRepository.findById(stmtId);
		} else {
			neo4jStatement = new Neo4jGeneralStatement(stmtId);
		}
		
		BeaconStatementSubject subject = statement.getSubject();
		Neo4jConcept neo4jSubject = loadConcept((SimpleBeaconConceptInterface)subject, queryString);
		neo4jStatement.getSubjects().add(neo4jSubject);
	
		BeaconStatementPredicate predicate = statement.getPredicate();
		Neo4jPredicate neo4jPredicate = new Neo4jPredicate() ;
		neo4jPredicate.setId(predicate.getId());
		neo4jPredicate.setName(predicate.getName());
		neo4jStatement.setRelation(neo4jPredicate);
		 
		BeaconStatementObject object = statement.getObject();
		Neo4jConcept neo4jObject = loadConcept((SimpleBeaconConceptInterface)object, queryString);
		neo4jStatement.getObjects().add(neo4jObject);
			
		neo4jStatement.setQueryFoundWith(queryString);

		if (!statementRepository.exists(neo4jStatement.getId(), queryString)) {
			statementRepository.save(neo4jStatement);
			return true;
		} else {
			return false;
		}
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

			Concept neo4jSubject = (Concept) result.get("subject");
			ServerStatementSubject serverSubject = new ServerStatementSubject();
			serverSubject.setClique(neo4jSubject.getClique());
//			serverSubject.setId(neo4jSubject.getId());
			serverSubject.setName(neo4jSubject.getName());
			serverSubject.setType(neo4jSubject.getType().getName());
			
			Predicate neo4jPredicate = (Predicate) result.get("relation");
			ServerStatementPredicate serverPredicate = new ServerStatementPredicate();
			serverPredicate.setName(neo4jPredicate.getName());
			serverPredicate.setId(neo4jPredicate.getId());

			Concept neo4jObject = (Concept) result.get("object");
			ServerStatementObject serverObject = new ServerStatementObject();
			serverObject.setClique(neo4jObject.getClique());
//			serverObject.setId(neo4jObject.getId());
			serverObject.setName(neo4jObject.getName());
			serverObject.setType(neo4jObject.getType().getName());

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