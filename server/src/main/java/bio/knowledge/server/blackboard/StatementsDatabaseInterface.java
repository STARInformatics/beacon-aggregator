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
					neo4jSubject = getConcept((SimpleConcept)beaconSubject, beacon);
				}
				
				Neo4jRelation neo4jRelation = (Neo4jRelation)statement.getRelation();
				if (neo4jRelation == null) {
					
					neo4jRelation = predicateRepository.findPredicateById(beaconRelation.getId());
					
					if (neo4jRelation == null) {
						neo4jRelation = new Neo4jRelation(beaconRelation.getId(),beaconRelation.getName());
						neo4jRelation = predicateRepository.save(neo4jRelation);
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
				statement.setRelation(neo4jRelation);
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
		
		ConceptTypeEntry conceptType = conceptTypeService.lookUp(beacon.getBeaconId(), concept.getType());
		
		if (clique == null) {
			clique = exactMatchesHandler.createConceptClique(concept.getId(), beacon.getBeaconId());
			clique.setConceptType(conceptType.getLabel());
			conceptCliqueRepository.save(clique);
		}
		
		String cliqueId = clique.getId();
		
		Neo4jConcept neo4jConcept = conceptRepository.getByClique(cliqueId);
		
		if (neo4jConcept == null) {
			/*
			 * TODO: This may not be adequate: we may wish to trigger a full beacon harvesting of this concept, if not here.
			 */
			neo4jConcept = new Neo4jConcept(clique.getId(),conceptType,concept.getName());
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
	@Deprecated 

 	keeping this code around in case I need to refer  
 	back to the matchToList() and harvestStatements() code
	
	/*
	 * @param conceptId
	 * @param conceptName
	 * @param identifiers
	 * @return
	 *-/
	private Boolean matchToList(String conceptId, String conceptName, List<String> identifiers ) {

		String idPattern = "(?i:"+conceptId+")";

		/*
		 *  Special test for the presence of 
		 *  Human Gene Nomenclature Consortium (and geneCards) symbols.
		 *  Case insensitive match to non-human species symbols
		 *  which may have difference letter case?
		 *-/
		String hgncSymbolPattern = "HGNC.SYMBOL:(?i:"+conceptName.toUpperCase()+")";
		String genecardsPattern = "GENECARDS:(?i:"+conceptName.toUpperCase()+")";
		String umlsPattern = "UMLS:(?i:"+conceptName.toUpperCase()+")";

		for(String id : identifiers) {

			if(id.matches(idPattern)) 
				return true;

			if(id.matches(hgncSymbolPattern)) 
				return true;

			if(id.matches(genecardsPattern)) 
				return true;

			if(id.matches(umlsPattern)) 
				return true;
		}
		return false;
	}

	public List<ServerStatement> harvestStatements(
			String source, String relations, String target, 
			String keywords, String conceptTypes, 
			Integer pageNumber, Integer pageSize,
			List<Integer> beacons, String queryId
	) {
		List<ServerStatement> statements = new ArrayList<ServerStatement>();
		
		ConceptClique sourceClique = getExactMatchesHandler().getClique(source);
		if(sourceClique==null) {
			_logger.error("getStatements(): source clique '"+source+"' could not be found?") ;
			return statements; // empty result
		}

		ConceptClique targetClique = null;
		if(!target.isEmpty()) {
			targetClique = getExactMatchesHandler().getClique(target);
			if(targetClique==null) {
				_logger.error("getStatements(): target clique '"+target+"' could not be found?") ;
				return statements; // empty result
			}
		}

		CompletableFuture<Map<KnowledgeBeacon, List<BeaconStatement>>> future = 
				kbs.getStatements( sourceClique, relations, targetClique, keywords, conceptTypes, pageNumber, pageSize, beacons, queryId );

		Map<
			KnowledgeBeacon, 
			List<BeaconStatement>
		> beaconStatements = waitFor(
								future,
								weightedTimeout(beacons, pageSize)
							);
		
		for (KnowledgeBeacon beacon : beaconStatements.keySet()) {

			Integer beaconId = beacon.getId();

			_logger.debug("ctrl.getStatements(): processing beacon '"+beaconId+"'...");

			for ( BeaconStatement response : beaconStatements.get(beacon)) {

				/*
				 * Sanity check: to get around the fact that some beacons 
				 * (like Biolink) will sometimes send back statements
				 *  with a null *%$@?!?!!! subject or object 
				 *-/
				if( response.getSubject()==null || response.getObject() == null ) continue;

				ServerStatement translation = Translator.translate(response);
				translation.setBeacon(beaconId);

				// Heuristic: need to somehow tag the equivalent concept here?
				ServerStatementSubject subject  = translation.getSubject();
				String subjectId = subject.getId();
				String subjectName = subject.getName();

				/*
				 * The existing beacons may not send the semantic group 
				 * back as a CURIE, thus coerce it accordingly
				 *-/
				String subjectTypeId = subject.getType();
				ConceptTypeEntry conceptType = conceptTypeService.lookUpByIdentifier(subjectTypeId);
				Set<ConceptTypeEntry> subjectTypes = new HashSet<ConceptTypeEntry>();
				if( conceptType != null ) {
					subjectTypes = new HashSet<ConceptTypeEntry>();
					subjectTypes.add(conceptType);
				}

				ConceptClique subjectEcc = 
						getExactMatchesHandler().getExactMatches(
								beacon,
								subjectId,
								subjectName,
								subjectTypes
								);

				ServerStatementObject object = translation.getObject();
				String objectId = object.getId();
				String objectName = object.getName();

				/*
				 * The existing beacons may not send the semantic group 
				 * back as a CURIE, thus coerce it accordingly
				 *-/
				String objectTypeId = object.getType();
				conceptType = conceptTypeService.lookUpByIdentifier(objectTypeId);
				Set<ConceptTypeEntry> objectTypes = new HashSet<ConceptTypeEntry>();
				if( conceptType != null ) {
					objectTypes = new HashSet<ConceptTypeEntry>();
					objectTypes.add(conceptType);
				}

				ConceptClique objectEcc = 
						getExactMatchesHandler().getExactMatches(
								beacon,
								objectId,
								objectName,
								objectTypes
								);

				/*
				 * Need to refresh the ecc clique in case either 
				 * subject or object id was discovered to belong 
				 * to it during the exact matches operations above?
				 *-/
				sourceClique = getExactMatchesHandler().getClique(source);

				List<String> conceptIds = sourceClique.getConceptIds(beaconId);

				_logger.debug("ctrl.getStatements(): processing statement '"+translation.getId()
							+ " from beacon '"+beaconId + "' "
							+ "with subject id '"+subjectId + "' "
							+ "and object id '"+objectId+"'"
							+ " matched against conceptIds: '"+String.join(",",conceptIds)+"'"
						);

				if( matchToList( subjectId, subjectName, conceptIds ) ) {

					subject.setClique(sourceClique.getId());
					/*
					 * Temporary workaround for beacons not yet 
					 * setting their statement subject semantic groups?
					 *-/
					String ssg = subject.getType();
					if( ( ssg==null || ssg.isEmpty() || ssg.equals(Category.DEFAULT_SEMANTIC_GROUP)) && sourceClique != null )
						subject.setType(sourceClique.getConceptType());

					object.setClique(objectEcc.getId());
					/*
					 * Temporary workaround for beacons not yet 
					 * setting their statement object semantic groups?
					 *-/
					String osg = object.getType();
					if( ( osg==null || osg.isEmpty() || osg.equals(Category.DEFAULT_SEMANTIC_GROUP)) && objectEcc != null )
						object.setType(objectEcc.getConceptType());

				} else if( matchToList( objectId, objectName, conceptIds ) ) {

					object.setClique(sourceClique.getId()) ;
					/*
					 * Temporary workaround for beacons not yet 
					 * setting their statement object semantic groups?
					 *-/
					String objectConceptType = object.getType();
					if( ( objectConceptType==null ||
							objectConceptType.isEmpty() || 
							objectConceptType.equals(Category.DEFAULT_SEMANTIC_GROUP)) && sourceClique != null
							)
						object.setType(sourceClique.getConceptType());

					subject.setClique(subjectEcc.getId());
					/*
					 * Temporary workaround for beacons not yet 
					 * setting their statement subject semantic groups?
					 *-/
					String subjectConceptType = subject.getType();
					if( ( subjectConceptType==null || 
							subjectConceptType.isEmpty() || 
							subjectConceptType.equals(Category.DEFAULT_SEMANTIC_GROUP)) && subjectEcc != null 
							)
						object.setType(subjectEcc.getConceptType());	

				} else {

					_logger.warn("ctrl.getStatements() WARNING: "
							+ "clique is unknown (null) "
							+ "for statement '"+translation.getId()
							+ "from beacon '"+beaconId
							+"' with subject '"+subject.getName()
							+"' ["+subjectId+"]"
							+ " and object '"+object.getName()
							+"' ["+objectId+"]"
							+ " matched against conceptIds: '"+
							String.join(",",conceptIds)+"'"
							);
					continue;
				}

				/*
				 *  Heuristic workaround for beacons which have not yet properly 
				 *  implemented the tagging of semantic groups of concepts,
				 *  to try to set their semantic group type
				 *-/
				if( subject.getClique() != null && 
						subject.getType() == null) {

					subject.setType(
							BioNameSpace.defaultConceptType( subject.getClique() ).getCurie()
							);
				}

				if( object.getClique() != null && 
						object.getType() == null) {

					object.setType(
							BioNameSpace.defaultConceptType( object.getClique() ).getCurie()
							);
				}

				statements.add(translation);
			}
		}

		if( ! relations.isEmpty() ) {
			final String relationFilter = relations;
			statements = statements.stream()
					.filter(
							s -> s.getPredicate().getId().equals(relationFilter) ? true : false 
							).collect(Collectors.toList());
		}
		
		return statements;
	}
	*/

	/*
	 *
	 * TODO: method not used but I'll keep it around commented out 
	 * for a short while until I'm sure that I don't still need to look at the code?
	 * 

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
		
		// TODO: convert 'queryString' into QueryTracker tagging?
		// TODO: fix getQueryResults() Cypher to properly return results!!??!
		List<Map<String, Object>> results = 
				statementRepository.getQueryResults(
						queryString,
						beacons,
						pageNumber,
						pageSize
				);

		List<ServerStatement> serverStatements = new ArrayList<ServerStatement>();
		for (Map<String, Object> result : results) {

			Neo4jGeneralStatement statement = (Neo4jGeneralStatement) result.get("statement");

			Neo4jConcept neo4jSubject = (Neo4jConcept) result.get("subject");
			ServerStatementSubject serverSubject = new ServerStatementSubject();
			
			serverSubject.setClique(neo4jSubject.getClique());
			
			Neo4jBeaconCitation subjCitation = 
					(Neo4jBeaconCitation) result.get("subjCitation");
			serverSubject.setId(subjCitation.getObjectId());
			
			serverSubject.setName(neo4jSubject.getName());
			
			/*
			Optional<ConceptTypeEntry> subjectTypeOpt = neo4jSubject.getType();
			ConceptTypeEntry subjectType;
			if(subjectTypeOpt.isPresent())
				subjectType = subjectTypeOpt.get();
			else
			 */
			ConceptTypeEntry subjectType = (ConceptTypeEntry) result.get("subjectType");
			if(subjectType==null)
				subjectType = conceptTypeService.defaultConceptType();
			serverSubject.setType(subjectType.getLabel());
			
			Neo4jRelation neo4jPredicate = (Neo4jRelation) result.get("relation");
			ServerStatementPredicate serverPredicate = new ServerStatementPredicate();
			serverPredicate.setName(neo4jPredicate.getName());
			serverPredicate.setId(neo4jPredicate.getId());

			Neo4jConcept neo4jObject = (Neo4jConcept) result.get("object");
			ServerStatementObject serverObject = new ServerStatementObject();
			
			serverObject.setClique(neo4jObject.getClique());
			
			Neo4jBeaconCitation objCitation = 
					(Neo4jBeaconCitation) result.get("objCitation");
			serverObject.setId(objCitation.getObjectId());
			
			serverObject.setName(neo4jObject.getName());
			
			/*
			Optional<ConceptTypeEntry> objectTypeOpt = neo4jObject.getType();
			ConceptTypeEntry objectType;
			if(objectTypeOpt.isPresent())
				objectType = objectTypeOpt.get();
			else
			*/
			ConceptTypeEntry objectType = (ConceptTypeEntry) result.get("objectType");
			if(objectType==null)
				objectType = conceptTypeService.defaultConceptType();
			serverObject.setType(objectType.getLabel());
			
			ServerStatement serverStatement = new ServerStatement();
			serverStatement.setId(statement.getId());

			serverStatement.setObject(serverObject);
			serverStatement.setSubject(serverSubject);
			serverStatement.setPredicate(serverPredicate);
			
			Neo4jKnowledgeBeacon neo4jBeacon = 
					(Neo4jKnowledgeBeacon) result.get("beacon");
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