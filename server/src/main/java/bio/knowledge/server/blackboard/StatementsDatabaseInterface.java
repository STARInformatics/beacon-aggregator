/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.aggregator.StatementsQueryInterface;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconStatementObject;
import bio.knowledge.client.model.BeaconStatementPredicate;
import bio.knowledge.client.model.BeaconStatementSubject;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.EvidenceRepository;
import bio.knowledge.database.repository.PredicateRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jEvidence;
import bio.knowledge.model.neo4j.Neo4jGeneralStatement;
import bio.knowledge.model.neo4j.Neo4jRelation;
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
		extends CoreDatabaseInterface<
					StatementsQueryInterface,
					BeaconStatement,
					ServerStatement
				> 
{
	@Autowired private BeaconRepository beaconRepository;

	@Autowired private ConceptTypeService conceptTypeService;
	@Autowired private ConceptRepository conceptRepository;
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	@Autowired private ConceptCliqueRepository conceptCliqueRepository;
	
	@Autowired private StatementRepository statementRepository;
	@Autowired private PredicateRepository predicateRepository;
	@Autowired private EvidenceRepository evidenceRepository;

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#loadData(java.lang.Object, java.util.List, java.lang.Integer)
	 */
	@Override
	public void loadData(QuerySession<StatementsQueryInterface> query, List<BeaconStatement> results, Integer beaconId) {
		
		Neo4jKnowledgeBeacon beacon = beaconRepository.getBeacon(beaconId);

		for (BeaconStatement beaconStatement : results) {
			
			try {
				
				BeaconStatementSubject beaconSubject    = beaconStatement.getSubject();
				BeaconStatementPredicate beaconRelation = beaconStatement.getPredicate();
				BeaconStatementObject beaconObject      = beaconStatement.getObject();
				
				Map<String,Object> sMap = statementRepository.findById(beaconStatement.getId());
				
				Neo4jGeneralStatement statement;
				
				if (sMap != null && !sMap.isEmpty()) {
					statement = (Neo4jGeneralStatement)sMap.get("statement");
					statement.setSubject((Neo4jConcept)sMap.get("subject"));
					statement.setObject((Neo4jConcept)sMap.get("object"));
				} else {
					// Create a new empty statement
					statement = new Neo4jGeneralStatement( beaconStatement.getId(), "");
				}
				
				// These should be correct casts of the object
				Neo4jConcept neo4jSubject = (Neo4jConcept)statement.getSubject();
				if (neo4jSubject == null) {
					neo4jSubject = getConcept(beaconSubject.getId(), beacon);
				}
				
				neo4jSubject.setName(beaconSubject.getName());
				neo4jSubject.addTypes(conceptTypeService.lookUp(beaconId, beaconSubject.getType()));
				
				// don't forget to persist the new/modified statement subject concept!
				neo4jSubject = conceptRepository.save(neo4jSubject);
				
				Neo4jRelation neo4jRelation = (Neo4jRelation)statement.getRelation();
				if (neo4jRelation == null) {
					neo4jRelation = predicateRepository.findPredicateById(beaconRelation.getId());
					
					if (neo4jRelation == null) {
						neo4jRelation = new Neo4jRelation();
					}
				}
				
				neo4jRelation.setName(beaconRelation.getName());
				neo4jRelation.setId(beaconRelation.getId());
				
				// don't forget to persist the new/modified statement predicate relation!
				neo4jRelation = predicateRepository.save(neo4jRelation);
				
				Neo4jConcept neo4jObject = (Neo4jConcept)statement.getObject();
				if (neo4jObject == null) {
					neo4jObject = getConcept(beaconObject.getId(), beacon);
				}
				
				neo4jObject.setName(beaconObject.getName());
				neo4jObject.addTypes(conceptTypeService.lookUp(beaconId, beaconObject.getType()));

				// don't forget to persist the new/modified statement object concept!
				neo4jObject = conceptRepository.save(neo4jObject);

				Neo4jEvidence neo4jEvidence = 
						evidenceRepository.findByEvidenceId(beaconStatement.getId());
				
				if(neo4jEvidence == null) {
					/*
					 * I can create a new evidence object but it won't 
					 * yet be initialized with associated References.
					 * That may be OK if they are later retrieved on demand
					 * (i.e. lazy loaded...) by the /evidence endpoint
					 */
					neo4jEvidence = evidenceRepository.createByEvidenceId( beaconStatement.getId() );
				}
				
				/*
				 * Now, load and persist the new or updated statement object
				 */
				statement.setId(beaconStatement.getId());
				statement.setSubject(neo4jSubject);
				statement.setRelation(neo4jRelation);
				statement.setObject(neo4jObject);
				statement.setEvidence(neo4jEvidence);
				statement.addBeacon(beacon);
				
				statement.addQuery(query.getQueryTracker());
				
				statementRepository.save(statement);				
				
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Neo4jConcept getConcept(String conceptId, Neo4jKnowledgeBeacon beacon) {
		
		ConceptClique clique = 
				exactMatchesHandler.getConceptCliqueFromDb(new String[] { conceptId });
		
		if (clique == null) {
			clique = exactMatchesHandler.createConceptClique(conceptId, beacon.getBeaconId());
			conceptCliqueRepository.save(clique);
		}
		
		String cliqueId = clique.getId();
		
		Neo4jConcept neo4jConcept = conceptRepository.getByClique(cliqueId);
		
		if (neo4jConcept == null) {
			neo4jConcept = new Neo4jConcept();
			neo4jConcept.setClique(cliqueId);
		}
		
		// Add this beacon to the set of beacons who have seen this concept
		neo4jConcept.addBeacon(beacon);
		
		return neo4jConcept;
	}

	/*
	 *
	 * TODO: method not used but I'll keep it around commented out 
	 * for a short while until I'm sure that I don't still need to look at the code?
	 * 

	private interface SimpleBeaconConceptInterface {
		String getId();
		String getName();
		String getType();
	}
	
	private Neo4jConcept loadConcept(SimpleBeaconConceptInterface concept, String queryString) {
		
		Neo4jConcept neo4jConcept = new Neo4jConcept();
		
		ConceptClique clique = 
				exactMatchesHandler.getConceptCliqueFromDb(new String[] { concept.getId() });
		neo4jConcept.setClique(clique.getId());
		
		neo4jConcept.setName(concept.getName());
		
		ConceptTypeEntry conceptType = conceptTypeService.lookUpByIdentifier(concept.getType());
		if( conceptType != null ) {
			Set<ConceptTypeEntry> types = new HashSet<ConceptTypeEntry>();
			types.add(conceptType);
			neo4jConcept.setTypes(types);
		}

		// March 26th, 2018: New format of QueryTracker management: 
		// TODO: Need to fix this particular test
		//neo4jConcept.setQueryFoundWith(queryString);
		
		if (!conceptRepository.exists(neo4jConcept.getClique(), queryString)) {
			conceptRepository.save(neo4jConcept);
		}
		return neo4jConcept;
	}
	*/

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#getDataPage(bio.knowledge.aggregator.QuerySession, java.util.List)
	 */
	@Override
	public List<ServerStatement> getDataPage(
				QuerySession<StatementsQueryInterface> query, 
				List<Integer> beacons
	) {
		//String queryString = query.makeQueryString();
		
		StatementsQueryInterface statementQuery = query.getQuery();
		
		/*
		 * TODO: review this legacy code to clarify whether or not it is truly needed... or if the 'queryString' mechanism suffices
		 * TODO: review 'queryString' to see if we need to harmonize with the QueryTracker mechanism in /concepts
		 * 
		String[] sources = split(statementQuery.getSource());
		String[] relationIds = statementQuery.getRelations().toArray(new String[0]);
		String[] targets = split(statementQuery.getTarget());
		String[] semanticGroups = statementQuery.getConceptTypes().toArray(new String[0]);
		String[] filter = split(statementQuery.getKeywords());
		*/
		
		beacons = beacons.isEmpty() ? beaconRepository.findAllBeacons().stream().map(b -> b.getBeaconId()).collect(Collectors.toList()) : beacons;
		
		List<Map<String, Object>> results = statementRepository.getQueryResults(
				query.makeQueryString(),
				beacons,
				statementQuery.getPageNumber(),
				statementQuery.getPageSize()
		);

		List<ServerStatement> serverStatements = new ArrayList<ServerStatement>();
		for (Map<String, Object> result : results) {

			Neo4jGeneralStatement statement = (Neo4jGeneralStatement) result.get("statement");

			Neo4jConcept neo4jSubject = (Neo4jConcept) result.get("subject");
			ServerStatementSubject serverSubject = new ServerStatementSubject();
			
			serverSubject.setClique(neo4jSubject.getClique());
//			serverSubject.setId(neo4jSubject.getId());
			serverSubject.setName(neo4jSubject.getName());
			
			Optional<ConceptTypeEntry> subjectTypeOpt = neo4jSubject.getType();
			ConceptTypeEntry subjectType;
			if(subjectTypeOpt.isPresent())
				subjectType = subjectTypeOpt.get();
			else
				subjectType = conceptTypeService.defaultType();
			serverSubject.setType(subjectType.getLabel());
			
			Neo4jRelation neo4jPredicate = (Neo4jRelation) result.get("relation");
			ServerStatementPredicate serverPredicate = new ServerStatementPredicate();
			serverPredicate.setName(neo4jPredicate.getName());
			serverPredicate.setId(neo4jPredicate.getId());

			Neo4jConcept neo4jObject = (Neo4jConcept) result.get("object");
			ServerStatementObject serverObject = new ServerStatementObject();
			
			serverObject.setClique(neo4jObject.getClique());
//			serverObject.setId(neo4jObject.getId());
			serverObject.setName(neo4jObject.getName());
			
			Optional<ConceptTypeEntry> objectTypeOpt = neo4jObject.getType();
			ConceptTypeEntry objectType;
			if(objectTypeOpt.isPresent())
				objectType = objectTypeOpt.get();
			else
				objectType = conceptTypeService.defaultType();
			serverObject.setType(objectType.getLabel());
			
			ServerStatement serverStatement = new ServerStatement();
			serverStatement.setId(statement.getId());

			serverStatement.setObject(serverObject);
			serverStatement.setSubject(serverSubject);
			serverStatement.setPredicate(serverPredicate);
			
			Neo4jKnowledgeBeacon neo4jBeacon = (Neo4jKnowledgeBeacon) result.get("beacon");
			serverStatement.setBeacon(neo4jBeacon.getBeaconId());
			
			serverStatements.add(serverStatement);
		}
		return serverStatements;
	}

	@Override
	public Integer getDataCount(QuerySession<StatementsQueryInterface> query, int beaconId) {
		return statementRepository.countQueryResults(query.makeQueryString(), beaconId);
	}
	
	/*
	 * LEGACY DATABASE ACCESS CODE FROM PREVIOUS CACHING...
	 * 
	 * 
	/**
	 * 
	 * @param source
	 * @param relations
	 * @param target
	 * @param keywords
	 * @param conceptTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param queryId
	 * @return
	 * /
	public List<ServerStatement>  getStatements(
					String source,
					String relations,
					String target,
					String keywords,
					String conceptTypes,
					Integer pageNumber, 
					Integer pageSize, 
					List<Integer> beacons, 
					String queryId
	) throws BlackboardException {
		
		List<ServerStatement> statements = new ArrayList<ServerStatement>();
		
		try {
			
			if(source.isEmpty()) {
				throw new RuntimeException("Blackboard.getStatements(): empty source clique string encountered?") ;
			}

			/*
			 * Look for existing concept relationship statements 
			 * cached within the blackboard (Neo4j) database
			 * /
			
			statements = 
					getStatementsFromDatabase( 
							source,  relations, target, 
							keywords, conceptTypes, 
							pageNumber, pageSize,
							beacons
					);
	    	
			// If none found, harvest concepts from the Beacon network
		    	if (statements.isEmpty()) {
		    		
		    		statements = beaconHarvestService.harvestStatements(
		    				    source,  relations, target, 
							keywords, conceptTypes, 
							pageNumber, pageSize,
							beacons,
							queryId
		    	    			);
		    		
		    		addStatementsToDatabase(statements);
		    	}
				
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		    	
		return statements;
	}
	
	private void addStatementsToDatabase(List<ServerStatement> statements) {
		
		for(ServerStatement statement : statements) {
			
			// Need to more completely populate statements here!
			Neo4jGeneralStatement entry = 
					new Neo4jGeneralStatement(
				    		 statement.getId() //,
				    		 //subject,
				    		 //predicate,
				    		 //object
				    );
			
			statementRepository.save(entry);
		}
	}

	/*
	 * Method to retrieve Statements in the local cache database
	 * /
	private List<ServerStatement> getStatementsFromDatabase(
			String source, String relation, String target, 
			String keywords, String types, 
			Integer pageNumber, Integer pageSize,
			List<Integer> beacons
	) {
		//String queryString = makeQueryString("statement", keywords, types);
		
		String[] sources   = new String[] {source};
		String[] relations = new String[] {relation};
		String[] targets   = new String[] {target};
		
		String[] keywordArray = keywords != null ? keywords.split(" ") : null;
		String[] typesArray = types != null ? types.split(" ") : new String[0];
		
		pageNumber = pageNumber != null && pageNumber > 0 ? pageNumber : 1;
		pageSize = pageSize != null && pageSize > 0 ? pageSize : 5;
		
		List<Map<String, Object>> Neo4jGeneralStatements = 
				statementRepository.findStatements(
						sources, relations, targets,
						keywordArray, typesArray,
						pageNumber, pageSize
				);
		
		List<ServerStatement> statements = new ArrayList<ServerStatement>();
		
		/ *
		for (Neo4jGeneralStatement Neo4jGeneralStatement : Neo4jGeneralStatements) {
			
			ServerStatement statement = new ServerStatement();
			
			statement.setId(Neo4jGeneralStatement.getId());
			
			// process statements more completely here
			
			statements.add(statement);
		}
		* /
		
		return statements;
	}

	 */
	
}