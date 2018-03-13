/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-18 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.BeaconConceptWrapper;
import bio.knowledge.aggregator.BeaconItemWrapper;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.Curie;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.Harvester.DatabaseInterface;
import bio.knowledge.aggregator.harvest.Query;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.AnnotationRepository;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.EvidenceRepository;
import bio.knowledge.database.repository.ReferenceRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.model.Annotation;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.neo4j.Neo4jAnnotation;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jEvidence;
import bio.knowledge.model.neo4j.Neo4jGeneralStatement;
import bio.knowledge.model.neo4j.Neo4jReference;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerCliqueIdentifier;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerStatement;

/**
 * This class manages the KBA Blackboard which is, in essence, 
 * a graph database of cached retrieved concepts and relationships.
 * 
 * If requested concepts and relationship statements are not yet detected
 * in the graph database, then a query is triggered to harvest such data 
 * from onto the Knowledge Beacon network.
 * 
 * @author richard
 *
 */
@Service
public class Blackboard implements Curie, Query {
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	
	@Autowired private BeaconHarvestService beaconHarvestService;
	
	@Autowired private ConceptTypeService conceptTypeService;

	@Autowired private ConceptRepository    conceptRepository;
	@Autowired private StatementRepository  statementRepository;
	@Autowired private EvidenceRepository   evidenceRepository;
	@Autowired private AnnotationRepository annotationRepository;
	@Autowired private ReferenceRepository  referenceRepository;

/******************************** CONCEPT Data Access *************************************/
	
	public List<ServerConcept> getConcepts(
			String keywords, 
			String conceptTypes, 
			Integer pageNumber, 
			Integer pageSize,
			List<String> beacons, 
			String sessionId
	) throws BlackboardException {
		
		List<ServerConcept> concepts = null;
		
		try {
			
			/*
			 * TODO: if a previous query triggers population 
			 * of the database with data fitting a particular 
			 * profile, with another query  just seee that data 
			 * and not try to harvest additional data from the 
			 * beacons which is releveant to their  needs?
			 * 
			 */
			/*
			 * Look for existing concepts cached within 
			 * the blackboard (Neo4j) database
			 */
			concepts = 
					getConceptsFromDatabase(
							keywords, conceptTypes, 
							pageNumber, pageSize,
							beacons
					);
			/*
			 *  If none found, harvest concepts 
			 *  from the Beacon network
			 */
		    	if (concepts.size() < pageSize) {
		    		
		    		DatabaseInterface databaseInterface = new DatabaseInterface<BeaconConcept, ServerConcept>() {

		    			@Override
		    			public boolean cacheData(KnowledgeBeaconImpl kb, BeaconItemWrapper<BeaconConcept> beaconItemWrapper, String queryString) {
		    				BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
		    				BeaconConcept concept = conceptWrapper.getItem();

		    				ConceptTypeEntry conceptType = conceptTypeService.lookUp(concept.getType());
		    				Neo4jConcept neo4jConcept = new Neo4jConcept();
		    				
		    				neo4jConcept.setClique(conceptWrapper.getClique());
		    				neo4jConcept.setName(concept.getName());
		    				if(conceptType!=null) {
		    					List<ConceptTypeEntry> types = new ArrayList<ConceptTypeEntry>();
		    					types.add(conceptType);
		    					neo4jConcept.setTypes(types);
		    				}

		    				neo4jConcept.setQueryFoundWith(queryString);
		    				neo4jConcept.setSynonyms(concept.getSynonyms());
		    				neo4jConcept.setDefinition(concept.getDefinition());

		    				if (!conceptRepository.exists(neo4jConcept.getClique(), queryString)) {
		    					conceptRepository.save(neo4jConcept);
		    					return true;
		    				} else {
		    					return false;
		    				}
		    			}

		    			@Override
		    			public List<ServerConcept> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize, String queryString) {
		    				return getConceptsFromDatabase(
									keywords, conceptTypes, 
									pageNumber, pageSize,
									beacons, queryString
							);
		    			}
		    		};
		    		
		    		concepts = beaconHarvestService.harvestConcepts(
		    	    				keywords, conceptTypes,
		    	    				pageNumber, pageSize,
		    	    				beacons, sessionId,
		    	    				databaseInterface
		    	    			);

		    	} 		
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return concepts;
	}
	
	/**
	 * Saving to the database only happens within Harvester.DatabaseInterface.
	 * See {@link Blackboard.getConcepts} method.
	 * 
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void addConceptsToDatabase(List<ServerConcept> concepts) {
		
		for(ServerConcept concept : concepts) {
			
			Neo4jConcept entry = new Neo4jConcept();
			
			entry.setClique(concept.getClique());
			entry.setName(concept.getName());
			
			// TODO: Fix concept type setting
			//String typeName = concept.getType();
			//entry.setTypes(types);
			
			conceptRepository.save(entry);
		}
	}

	/*
	 * Retrieves a List of BeaconConcepts from the database, 
	 * if a keyword match to concept name, etc. is available.
	 */
	private List<ServerConcept> getConceptsFromDatabase(
			String keywords, 
			String types, 
			Integer pageNumber,
			Integer pageSize,
			List<String> beacons
	){
		String queryString = makeQueryString("concept", keywords, types);
		return getConceptsFromDatabase(keywords, types, pageNumber, pageSize, beacons, queryString);
	}
	
	private List<ServerConcept> getConceptsFromDatabase(
						String keywords, 
						String types, 
						Integer pageNumber,
						Integer pageSize,
						List<String> beacons,
						String queryString
		) {
		
		String[] keywordArray = keywords != null && !keywords.isEmpty() ? keywords.split(" ") : null;
		String[] typesArray = types != null && !types.isEmpty() ? types.split(" ") : null;
		
		pageNumber = pageNumber != null && pageNumber > 0 ? pageNumber : 1;
		pageSize = pageSize != null && pageSize > 0 ? pageSize : 5;
		
		List<Neo4jConcept> neo4jConcepts = 
				conceptRepository.getConceptsByKeywordsAndType(
						keywordArray, 
						typesArray, 
						queryString, 
						pageNumber, 
						pageSize
				);
		
		List<ServerConcept> concepts = new ArrayList<ServerConcept>();
		
		for (Neo4jConcept neo4jConcept : neo4jConcepts) {
			
			ServerConcept concept = new ServerConcept();
			
			// TODO: fix BeaconConcept to include a proper clique?
			concept.setClique(neo4jConcept.getClique());
			
			concept.setName(neo4jConcept.getName());
			
			// TODO: fix BeaconConcept to track data type?
			concept.setType(neo4jConcept.getType().getName());
			
			concepts.add(concept);
		}
		
		return concepts;
	}

	/**
	 * 
	 * @param identifier
	 * @param sessionId
	 * @return
	 */
	public ServerCliqueIdentifier getClique( String identifier ) throws BlackboardException {
		
		ServerCliqueIdentifier cliqueId = null;
		
		try {
			
			ConceptClique clique = 
					exactMatchesHandler.getConceptClique(new String[] { identifier });
			
			if(clique!=null) {
				cliqueId = new ServerCliqueIdentifier();
				cliqueId.setCliqueId(clique.getId());
			}
		
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return cliqueId;
	}
	
	/**
	 * 
	 * @param clique
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public  ServerConceptWithDetails getConceptDetails(
			String cliqueId, 
			List<Integer> beacons
	) throws BlackboardException {
	
		ServerConceptWithDetails concept = null;
		
		try {
			/*
			 * Look for existing concepts cached within 
			 * the blackboard (Neo4j) database
			 */
			concept = getConceptsWithDetailsFromDatabase(
							cliqueId,
							beacons
					);
			/*
			 *  If none found, harvest concepts 
			 *  from the Beacon network
			 */
		    	if (concept==null) {
		    		
		    		concept = beaconHarvestService.harvestConceptsWithDetails(
		    					cliqueId,
		    	    				beacons,
		    	    				sessionId
		    	    			);

		    		addConceptsWithDetailsToDatabase(concept);

		    	} 		
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return concept;
	}

	private void addConceptsWithDetailsToDatabase(ServerConceptWithDetails concept) {
			
		Neo4jConcept entry = new Neo4jConcept();
		
		entry.setClique(concept.getClique());
		entry.setName(concept.getName());
		
		/*  TODO: Fix concept type setting
		String type = concept.getType();
		if(type!=null) {
			String[] typenames = type.split("\\s");
			List<String> types = new ArrayList<String>();
			// TODO lookup and load types by typenames here?
			entry.setTypes(types);
		}
		*/
		
		conceptRepository.save(entry);
	}

	private ServerConceptWithDetails getConceptsWithDetailsFromDatabase(String cliqueId, List<String> beacons) {
		
		/*
		 * TODO: the 'getByClique()' needs to be fleshed out out to return a fully detailed object
		 */
		Neo4jConcept neo4jConcept = 
				conceptRepository.getByClique(cliqueId);
		
		if(neo4jConcept == null) return  null;
		
		ServerConceptWithDetails concept = new ServerConceptWithDetails();
		
		// TODO: fix BeaconConcept to include a proper clique?
		concept.setClique(neo4jConcept.getClique());
		
		concept.setName(neo4jConcept.getName());
		
		// TODO: fix BeaconConcept to track data type?
		concept.setType(neo4jConcept.getType().getName());
		
		return concept;
	}

/******************************** STATEMENTS Data Access *************************************/

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
	 * @param sessionId
	 * @return
	 */
	public List<ServerStatement>  getStatements(
					String source,
					String relations,
					String target,
					String keywords,
					String conceptTypes,
					Integer pageNumber, 
					Integer pageSize, 
					List<String> beacons, 
					String sessionId
	) throws BlackboardException {
		
		List<ServerStatement> statements = new ArrayList<ServerStatement>();
		
		try {
			
			if(source.isEmpty()) {
				throw new RuntimeException("Blackboard.getStatements(): empty source clique string encountered?") ;
			}

			/*
			 * Look for existing concept relationship statements 
			 * cached within the blackboard (Neo4j) database
			 */
			
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
							sessionId
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
	 */
	private List<ServerStatement> getStatementsFromDatabase(
			String source, String relation, String target, 
			String keywords, String types, 
			Integer pageNumber, Integer pageSize,
			List<String> beacons
	) {
		String queryString = makeQueryString("statement", keywords, types);
		
		String[] sources   = new String[] {source};
		String[] relations = new String[] {relation};
		String[] targets   = new String[] {target};
		
		String[] keywordArray = keywords != null ? keywords.split(" ") : null;
		String[] typesArray = types != null ? types.split(" ") : new String[0];
		
		pageNumber = pageNumber != null && pageNumber > 0 ? pageNumber : 1;
		pageSize = pageSize != null && pageSize > 0 ? pageSize : 5;
		
		List<Map<String, Object>> neo4jStatements = 
				statementRepository.findStatements(
						sources, relations, targets,
						keywordArray, typesArray,
						pageNumber, pageSize
				);
		
		List<ServerStatement> statements = new ArrayList<ServerStatement>();
		
		/*
		for (Neo4jGeneralStatement neo4jStatement : neo4jStatements) {
			
			ServerStatement statement = new ServerStatement();
			
			statement.setId(neo4jStatement.getId());
			
			// process statements more completely here
			
			statements.add(statement);
		}
		*/
		
		return statements;
	}
	
/******************************** EVIDENCE Data Access *************************************/

	/**
	 * 
	 * @param statementId
	 * @param keywords
	 * @param pageNumber
	 * @param pageSize
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public List<ServerAnnotation>  getEvidence(
					String statementId,
					String keywords,
					Integer pageNumber,
					Integer pageSize,
					List<String> beacons,
					String sessionId
	) throws BlackboardException {
		
		List<ServerAnnotation> annotations = new ArrayList<ServerAnnotation>();
		
		try {
			/*
			 * Look for existing concepts cached within 
			 * the blackboard (Neo4j) database
			 */
			annotations = 
					getEvidenceFromDatabase(
										statementId, keywords,
										pageNumber, pageSize,
										beacons
					);
			/*
			 *  If none found, harvest evidence from the Beacon network
			 */
		    	if (annotations.isEmpty()) {
		    		
		    		annotations = 
		    				beaconHarvestService.harvestEvidence(
					    					statementId, keywords,
					    	    				pageNumber, pageSize,
					    	    				beacons, sessionId
		    	    			);

		    		addEvidenceToDatabase(annotations);
		    	}

		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return annotations;
	}

	/*
	 * This method saves Evidence to the local Neo4j cache database
	 * TODO: we need to carefully review the current data models for Evidence
	 */
	private void addEvidenceToDatabase(List<ServerAnnotation> serverAnnotations) {
		
		Neo4jEvidence entry = new Neo4jEvidence();
		Set<Annotation> annotations = entry.getAnnotations();
		
		for(ServerAnnotation serverAnnotation : serverAnnotations) {
			
			Neo4jReference reference = new Neo4jReference() ;
			// populate reference here...
			referenceRepository.save(reference);
			
			Neo4jAnnotation annotation = new Neo4jAnnotation( 
					serverAnnotation.getId(), 
					serverAnnotation.getLabel(),
					reference 
		    );
			
			// How do we set 'type' and 'beacon' here?
			//entry.setType(serverAnnotation.getType());
			//entry.setBeacon(serverAnnotation.getBeacon());
			
			annotationRepository.save(annotation);
			annotations.add(annotation);
			
		}
		
		evidenceRepository.save(entry);
	}

	private List<ServerAnnotation> getEvidenceFromDatabase(
			String statementId, 
			String keywords, 
			Integer pageNumber, Integer pageSize, 
			List<String> beacons
	) {
		String queryString = makeQueryString("evidence", statementId, keywords);
		
		String[] keywordArray = keywords != null ? keywords.split(" ") : null;
		
		pageNumber = pageNumber != null && pageNumber > 0 ? pageNumber : 1;
		pageSize = pageSize != null && pageSize > 0 ? pageSize : 5;
		
		List<Map<String, Object>> evidence = 
				evidenceRepository.getEvidenceByIdAndKeywords(
						statementId, keywordArray,
						pageNumber,pageSize
				);
		
		List<ServerAnnotation> annotations = new ArrayList<ServerAnnotation>();
		
		// TODO: Process evidence here!
		
		return annotations;
	}
}
