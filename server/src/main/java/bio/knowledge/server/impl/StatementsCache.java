package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.BaseCache;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.QueryTracker;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.PredicateRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.Concept;
import bio.knowledge.model.ConceptType;
import bio.knowledge.model.Predicate;
import bio.knowledge.model.Statement;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jGeneralStatement;
import bio.knowledge.model.neo4j.Neo4jPredicate;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementPredicate;
import bio.knowledge.server.model.ServerStatementSubject;

@Service
public class StatementsCache extends BaseCache {
	
	@Autowired private ControllerImpl      impl;
	@Autowired private StatementRepository statementRepository;
	@Autowired private ConceptRepository   conceptRepository;
	@Autowired private PredicateRepository predicateRepository;
	@Autowired private ConceptTypeService  conceptTypeService;

	@Autowired private QueryTracker queryTracker;
	protected QueryTracker getQueryTracker() {
		return queryTracker;
	}
	
	@Autowired private TaskExecutor executor;
	protected TaskExecutor getExecutor() {
		return executor;
	}

	public CompletableFuture<List<ServerStatement>> inititateStatementHarvest(
			String source,
			String relations,
			String target,
			String keywords,
			String conceptTypes,
			Integer requestPageNumber, 
			Integer requestPageSize, 
			List<String> beacons, 
			String sessionId
	) {
		
		final int pageNumber = sanitizeInt(requestPageNumber);
		final int pageSize = sanitizeInt(requestPageSize);
		
		BeaconInterface<ServerStatement> beaconInterface = makeBeaconInterface(
				source, relations, target, keywords, conceptTypes, beacons, sessionId
		);
		
		DatabaseInterface<ServerStatement> databaseInterface = makeDatabaseInterface(
				source, relations, target, keywords, conceptTypes, pageNumber, pageSize
		);
		
		RelevanceTester<ServerStatement> relevanceTester = makeRelevanceTester(keywords);
		
		String queryString = makeQueryString("statement", source, relations, target, keywords, conceptTypes);
		int threashold = makeThreshold(pageNumber, pageSize);
		
		return this.initiateHarvest(queryString, threashold, beaconInterface, databaseInterface, relevanceTester);
	}

	private RelevanceTester<ServerStatement> makeRelevanceTester(String keywords) {
		return new RelevanceTester<ServerStatement>() {

			@Override
			public boolean isPageRelevant(Collection<ServerStatement> data) {
				if (keywords == null) {
					return true;
				}
				
				for (String keyword : split(keywords)) {
					for (ServerStatement statement : data) {
						if (statement.getObject().getName().toLowerCase().contains(keyword)) {
							return true;
						}
						if (statement.getSubject().getName().toLowerCase().contains(keyword)) {
							return true;
						}
					}
				}
				return false;
			}
			
		};
	}

	private DatabaseInterface<ServerStatement> makeDatabaseInterface(String source, String relations, String target,
			String keywords, String conceptTypes, final int pageNumber, final int pageSize) {
		return new DatabaseInterface<ServerStatement>() {
			@Override
			public boolean cacheData(ServerStatement serverStatement, String queryString) {
				if (!statementRepository.exists(serverStatement.getId())) {
					String id = serverStatement.getId();
					
					ServerStatementObject serverObject = serverStatement.getObject();
					ServerStatementSubject serverSubject = serverStatement.getSubject();
					ServerStatementPredicate serverPredicate = serverStatement.getPredicate();
					
					
					Neo4jConcept neo4jObject = conceptRepository.getByClique(serverObject.getClique());
					Neo4jConcept neo4jSubject = conceptRepository.getByClique(serverSubject.getClique());
					Neo4jPredicate neo4jPredicate = predicateRepository.findPredicateById(serverPredicate.getId());
					
					if (neo4jObject == null) {
						ConceptType objectType = conceptTypeService.lookUp(serverObject.getType());
						neo4jObject = new Neo4jConcept(serverObject.getId(), objectType, serverObject.getName());
						neo4jObject.setClique(serverObject.getClique());
					}
					
					if (neo4jSubject == null) {
						ConceptType subjectType = conceptTypeService.lookUp(serverSubject.getType());
						neo4jSubject = new Neo4jConcept(serverSubject.getId(), subjectType, serverSubject.getName());
						neo4jSubject.setClique(serverSubject.getClique());
					}
					
					if (neo4jPredicate == null) {
						neo4jPredicate = new Neo4jPredicate();
						neo4jPredicate.setName(serverPredicate.getName());
						neo4jPredicate.setId(serverPredicate.getId());
					}
					
					Neo4jGeneralStatement neo4jStatement = new Neo4jGeneralStatement(id, neo4jSubject, neo4jPredicate, neo4jObject);
					
					statementRepository.save(neo4jStatement);
					
					return true;
				}
				
				return false;
			}
			@Override
			public List<ServerStatement> getDataPage() {
				return getStatementsFromDb(source, relations, target, conceptTypes, keywords, pageNumber, pageSize);
			}
		};
	}

	private BeaconInterface<ServerStatement> makeBeaconInterface(String source, String relations, String target,
			String keywords, String conceptTypes, List<String> beacons, String sessionId) {
		return new BeaconInterface<ServerStatement>() {
			@Override
			public ResponseEntity<List<ServerStatement>> getData(Integer pageNumber, Integer pageSize)
					throws InterruptedException, ExecutionException, TimeoutException {
				return impl.getStatements(source, relations, target, keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId);
			}
		};
	}
	
	public List<ServerStatement> getStatements(
			String source,
			String relations,
			String target,
			String keywords,
			String conceptTypes,
			Integer pageNumber, 
			Integer pageSize, 
			List<String> beacons, 
			String sessionId
	) {
		pageNumber = sanitizeInt(pageNumber);
		pageSize = sanitizeInt(pageSize);
		
		List<ServerStatement> statements = getStatementsFromDb(source, relations, target, keywords, conceptTypes, pageNumber, pageSize);
		
		if (statements.size() < pageSize) {
			CompletableFuture<List<ServerStatement>> future = inititateStatementHarvest(
					source, relations, target, keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId
			);
			
			try {
				statements = future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		return statements;
	}
	
	private List<ServerStatement> getStatementsFromDb(
			String source, String relations, String target, String conceptTypes, String keywords, Integer pageNumber, Integer pageSize
	) {
		String[] sources = split(source);
		String[] relationIds = split(relations);
		String[] targets = split(target);
		String[] semanticGroups = split(conceptTypes);
		String[] filter = split(keywords);
		
		List<Map<String, Object>> results = statementRepository.findStatements(
				sources, relationIds, targets, filter, semanticGroups, pageNumber, pageSize
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
