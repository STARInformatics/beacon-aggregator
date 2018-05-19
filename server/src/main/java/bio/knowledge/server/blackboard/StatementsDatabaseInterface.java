/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import bio.knowledge.database.repository.aggregator.BeaconCitationRepository;
import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.SimpleConcept;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jEvidence;
import bio.knowledge.model.neo4j.Neo4jPredicate;
import bio.knowledge.model.neo4j.Neo4jStatement;
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
	@Autowired private ConceptRepository  conceptRepository;
	
	@Autowired private ExactMatchesHandler     exactMatchesHandler;
	@Autowired private ConceptCliqueRepository conceptCliqueRepository;
	
	@Autowired private StatementRepository statementRepository;
	@Autowired private PredicateRepository predicateRepository;
	@Autowired private EvidenceRepository  evidenceRepository;
	@Autowired private BeaconCitationRepository beaconCitationRepository;

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
				
				Neo4jStatement statement;
				
				if (sMap != null && !sMap.isEmpty()) {
					statement = (Neo4jStatement)sMap.get("statement");
					statement.setSubject((Neo4jConcept)sMap.get("subject"));
					statement.setObject((Neo4jConcept)sMap.get("object"));
				} else {
					// Create a new empty statement
					statement = new Neo4jStatement();
					statement.setId(beaconStatement.getId());
				}
				
				Neo4jConcept neo4jSubject = statement.getSubject();
				if (neo4jSubject == null) {
					neo4jSubject = getConcept((SimpleConcept)beaconSubject, beacon);
				}
				
				Neo4jPredicate neo4jPredicate = statement.getRelation();
				if (neo4jPredicate == null) {
					
					neo4jPredicate = predicateRepository.findPredicateById(beaconRelation.getRelation());
					
					if (neo4jPredicate == null) {
						neo4jPredicate = new Neo4jPredicate();
						neo4jPredicate.setName(beaconRelation.getEdgeLabel());
						neo4jPredicate.setId(neo4jPredicate.getId());
						neo4jPredicate = predicateRepository.save(neo4jPredicate);
					}
				}
				
				Neo4jConcept neo4jObject = (Neo4jConcept)statement.getObject();
				if (neo4jObject == null) {
					neo4jObject = getConcept((SimpleConcept)beaconObject, beacon);
				}

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
				statement.setRelation(neo4jPredicate);
				statement.setObject(neo4jObject);
				statement.setEvidence(neo4jEvidence);
				
				// Reuse existing BeaconCitation, else create...
				Neo4jBeaconCitation citation = 
						beaconCitationRepository.findByBeaconAndObjectId(
													beacon.getBeaconId(),
													beaconStatement.getId()
												);
				if(citation==null) {
					citation = new Neo4jBeaconCitation(beacon,beaconStatement.getId());
					citation = beaconCitationRepository.save(citation);
				}
				statement.setBeaconCitation(citation);
				
				statement.addQuery(query.getQueryTracker());
				
				statementRepository.save(statement);				
				
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Neo4jConcept getConcept(SimpleConcept concept, Neo4jKnowledgeBeacon beacon) {
		
		ConceptClique clique = exactMatchesHandler.getConceptCliqueFromDb(new String[] { concept.getId() });
		
		ConceptTypeEntry category = 
				conceptTypeService.lookUp(beacon.getBeaconId(), concept.getCategory());
		
		if (clique == null) {
			clique = exactMatchesHandler.createConceptClique(
					concept.getId(), 
					beacon.getBeaconId(),
					category.getLabel()
			);
			clique.setConceptType(category.getLabel());
			conceptCliqueRepository.save(clique);
		}
		
		String cliqueId = clique.getId();
		
		Neo4jConcept neo4jConcept = conceptRepository.getByClique(cliqueId);
		
		if (neo4jConcept == null) {
			/*
			 * TODO: This may not be adequate: we may wish to trigger a full beacon harvesting of this concept, if not here.
			 */
			neo4jConcept = new Neo4jConcept(clique, category, concept.getName());
		}
		
		/*
		 * Add this beacon to the set of beacons 
		 * which have cited this concept
		 */
		Neo4jBeaconCitation citation = 
				beaconCitationRepository.findByBeaconAndObjectId(
											beacon.getBeaconId(),
											concept.getId()
										);
		if(citation==null) {
			citation = new Neo4jBeaconCitation(beacon,concept.getId());
			citation = beaconCitationRepository.save(citation);
		}
		neo4jConcept.addBeaconCitation(citation);
		
		// (re)save the (re)constituted Neo4jConcept node
		neo4jConcept = conceptRepository.save(neo4jConcept);
		
		return neo4jConcept;
	}

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#getDataPage(bio.knowledge.aggregator.QuerySession, java.util.List)
	 */
	@Override
	public List<ServerStatement> getDataPage(
				QuerySession<StatementsQueryInterface> query, 
				List<Integer> beacons
	) {
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
		
		String queryString = query.makeQueryString();
		
		beacons = beacons.isEmpty() ? 
				  beaconRepository.
				  	findAllBeacons().
				  		stream().
				  			map(b -> b.getBeaconId()).
				  				collect(Collectors.toList()) : 
				  beacons;
				  				
		int pageNumber = statementQuery.getPageNumber();
		int pageSize   = statementQuery.getPageSize();
		
		List<Neo4jStatement> results = statementRepository.getQueryResults(
				queryString,
				beacons,
				pageNumber,
				pageSize
		);

		List<ServerStatement> serverStatements = new ArrayList<ServerStatement>();
		for (Neo4jStatement statement : results) {

//			Neo4jGeneralStatement statement = (Neo4jGeneralStatement) result.get("statement");

			Neo4jConcept neo4jSubject = statement.getSubject();
			ServerStatementSubject serverSubject = new ServerStatementSubject();
			
			serverSubject.setClique(neo4jSubject.getClique().getId());
			
			Neo4jBeaconCitation subjCitation = statement.getBeaconCitation();
			serverSubject.setId(subjCitation.getObjectId());
			
			serverSubject.setName(neo4jSubject.getName());
			
			/*
			Optional<ConceptTypeEntry> subjectTypeOpt = neo4jSubject.getType();
			ConceptTypeEntry subjectType;
			if(subjectTypeOpt.isPresent())
				subjectType = subjectTypeOpt.get();
			else
			 */
			ConceptTypeEntry subjectType = neo4jSubject.getType();
			if(subjectType==null)
				subjectType = conceptTypeService.defaultConceptType();
			serverSubject.setType(subjectType.getLabel());
			
			Neo4jPredicate neo4jPredicate = statement.getRelation();
			ServerStatementPredicate serverPredicate = new ServerStatementPredicate();
			serverPredicate.setName(neo4jPredicate.getName());
			serverPredicate.setId(neo4jPredicate.getId());

			Neo4jConcept neo4jObject = statement.getObject();
			ServerStatementObject serverObject = new ServerStatementObject();
			
			serverObject.setClique(neo4jObject.getClique().getId());
			
			Neo4jBeaconCitation objCitation = statement.getBeaconCitation();
			serverObject.setId(objCitation.getObjectId());
			
			serverObject.setName(neo4jObject.getName());
			
			/*
			Optional<ConceptTypeEntry> objectTypeOpt = neo4jObject.getType();
			ConceptTypeEntry objectType;
			if(objectTypeOpt.isPresent())
				objectType = objectTypeOpt.get();
			else
			*/
			ConceptTypeEntry objectType = neo4jObject.getType();
			if(objectType==null)
				objectType = conceptTypeService.defaultConceptType();
			serverObject.setType(objectType.getLabel());
			
			ServerStatement serverStatement = new ServerStatement();
			serverStatement.setId(statement.getId());

			serverStatement.setObject(serverObject);
			serverStatement.setSubject(serverSubject);
			serverStatement.setPredicate(serverPredicate);
			
			Neo4jKnowledgeBeacon neo4jBeacon = statement.getBeaconCitation().getBeacon();
			serverStatement.setBeacon(neo4jBeacon.getBeaconId());
			
			serverStatements.add(serverStatement);
		}
		return serverStatements;
	}

	@Override
	public Integer getDataCount(QuerySession<StatementsQueryInterface> query, int beaconId) {
		return statementRepository.countQueryResults(query.makeQueryString(), beaconId);
	}
}

