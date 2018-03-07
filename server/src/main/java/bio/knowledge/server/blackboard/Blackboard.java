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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.SystemTimeOut;

import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.ConceptTypeUtil;
import bio.knowledge.aggregator.BeaconKnowledgeMap;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.aggregator.LogEntry;
import bio.knowledge.aggregator.blackboard.BeaconHarvestService;
import bio.knowledge.aggregator.blackboard.Query;

import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptType;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.model.BioNameSpace;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.umls.Category;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerCliqueIdentifier;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptBeaconEntry;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementSubject;

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
public class Blackboard implements SystemTimeOut, ConceptTypeUtil, Query {
	
	private static Logger _logger = LoggerFactory.getLogger(Blackboard.class);
	
	@Autowired private KnowledgeBeaconRegistry registry;
	
	@Autowired private ConceptRepository  conceptRepository;
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	
	@Autowired private BeaconHarvestService beaconHarvestService;
	
	@Autowired private KnowledgeBeaconService kbs;

	@Override
	public int countAllBeacons() {
		return registry.countAllBeacons();
	}

		/*
	 * @param future
	 * @return
	 */
	private <T> Map<KnowledgeBeaconImpl, List<T>> waitFor(CompletableFuture<Map<KnowledgeBeaconImpl, List<T>>> future) {
		return waitFor(
				future,
				// Scale the timeout proportionately to the number of beacons?
				registry.countAllBeacons()*KnowledgeBeaconService.BEACON_TIMEOUT_DURATION
		) ; 
	}
	 
	/*
	 * Waits {@code TIMEOUT} {@code TIMEUNIT} for the future to complete, throwing a runtime exception otherwise.
	 * @param future
	 * @return
	 */
	private <T> Map<KnowledgeBeaconImpl, List<T>> 
		waitFor(
				CompletableFuture<Map<KnowledgeBeaconImpl, List<T>>> future,
				long timeout
		) {
		try {
			return future.get(timeout, KnowledgeBeaconService.BEACON_TIMEOUT_UNIT);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}
	
	/******************************** METADATA Access *************************************/

	/**
	 * 
	 * @param sessionId
	 * @param beacon
	 * @param query
	 * @param message
	 */
	public void logError(String sessionId, String beacon, String query, String message) {
		kbs.logError(sessionId, beacon, query, message);
	}

	/**
	 * 
	 * @param sessionId
	 * @return
	 */
	public List<ServerLogEntry> getErrors(String sessionId) throws BlackboardException {
		
		List<ServerLogEntry> responses = new ArrayList<>();
		
		try {
			List<LogEntry> entries = kbs.getErrors(sessionId);
			
			for (LogEntry entry : entries) {
				if (entry != null) {
					responses.add(ModelConverter.convert(entry, ServerLogEntry.class));
				}
			}
		} catch (Exception e) {
			throw new BlackboardException(e);
		}

		return responses;
	}

	/**
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  Map<
				KnowledgeBeacon, 
				List<BeaconConceptType>
			> getAllConceptTypes() {
		
		CompletableFuture<
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconConceptType>
			>
		> future = kbs.getConceptTypes();
		
		Map<
			KnowledgeBeaconImpl, 
			List<BeaconConceptType>
		> map = waitFor(future);

		return (Map)map;
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<
				KnowledgeBeacon, 
				List<BeaconPredicate>
			> getAllPredicates() 
	{
			
		CompletableFuture<
			Map<KnowledgeBeaconImpl, 
			List<BeaconPredicate>>
		> future = kbs.getAllPredicates();

		Map<
			KnowledgeBeaconImpl, 
			List<BeaconPredicate>
		> map = waitFor( future );

		 return (Map)map;
	}
	

	public Map<
				KnowledgeBeacon, 
				List<BeaconKnowledgeMap>
			>  getKnowledgeMap(List<String> beacons, String sessionId) {
		
		// TODO Implement me!
		//return new ArrayList<BeaconKnowledgeMap>();
		throw new RuntimeException("Implement me!");
	}

/******************************** CONCEPT Data Access *************************************/
	
	/**
	 * Retrieves a List of BeaconConcepts from the database, 
	 * if a keyword match to concept name, etc. is available.
	 * 
	 * @param keywords
	 * @param types
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<BeaconConcept> getConceptsFromDatabase(String keywords, String types, Integer pageNumber, Integer pageSize) {
		
		String queryString = makeQueryString("concept", keywords, types);
		
		String[] keywordArray = keywords != null ? keywords.split(" ") : null;
		String[] typesArray = types != null ? types.split(" ") : new String[0];
		
		pageNumber = pageNumber != null && pageNumber > 0 ? pageNumber : 1;
		pageSize = pageSize != null && pageSize > 0 ? pageSize : 5;
		
		List<Neo4jConcept> neo4jConcepts = conceptRepository.apiGetConcepts(keywordArray, typesArray, queryString, pageNumber, pageSize);
		
		List<BeaconConcept> concepts = new ArrayList<BeaconConcept>();
		
		for (Neo4jConcept neo4jConcept : neo4jConcepts) {
			BeaconConcept concept = new BeaconConcept();
			
			// TODO: fix BeaconConcept to include a proper clique?
			concept.setId(neo4jConcept.getClique());
			
			concept.setName(neo4jConcept.getName());
			
			// TODO: fix BeaconConcept to track data type?
			concept.setSemanticGroup(neo4jConcept.getType().getName());
			
			concepts.add(concept);
		}
		
		return concepts;
	}

	public List<ServerConcept> getConcepts(
			String keywords, 
			String conceptTypes, 
			Integer pageNumber, 
			Integer pageSize,
			List<String> beacons, 
			String sessionId
	) throws BlackboardException {
		
		List<ServerConcept> responses = new ArrayList<ServerConcept>();
		
		try {
			/*
			 * Look for existing concepts cached within 
			 * the blackboard (Neo4j) database
			 */
			List<BeaconConcept> concepts = 
					getConceptsFromDatabase(
							keywords, 
							conceptTypes, 
							pageNumber, 
							pageSize,
							beacons
					);
	    	
			// If none found, harvest concepts from the Beacon network
		    	if (concepts.isEmpty())
		    		
		    		concepts = 
		    				beaconHarvestService.harvestConcepts(
		    	    				keywords,
		    	    				conceptTypes,
		    	    				pageNumber,
		    	    				pageSize,
		    	    				beacons,
		    	    				sessionId
		    	    			);

			for (BeaconConcept concept : concepts) {
				ServerConcept translation = Translator.translate(concept);
				responses.add(translation);
			}
			
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return responses;
	}

	/**
	 * 
	 * @param identifier
	 * @param sessionId
	 * @return
	 */
	public ServerCliqueIdentifier getClique(
			String identifier, 
			String sessionId
	) throws BlackboardException {
		
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  ServerConceptWithDetails getConceptDetails(
			String cliqueId, 
			List<String> 
			beacons, 
			String sessionId
	) throws BlackboardException {
		ServerConceptWithDetails conceptDetails = null;
		
		try {

			ConceptClique clique = exactMatchesHandler.getClique(cliqueId);

			if(clique==null) 
				throw new RuntimeException("getConceptDetails(): '"+cliqueId+"' could not be found?") ;

			conceptDetails = new ServerConceptWithDetails();
			
			conceptDetails.setClique(cliqueId);
			
			/* 
			 * Defer name setting below; 
			 * clique name seems to be the 
			 * same as the cliqueId right now... 
			 * not sure if that is correct?
			 * 
			 * conceptDetails.setName(ecc.getName()); 
			 */
			conceptDetails.setType(clique.getConceptType());
			conceptDetails.setAliases(clique.getConceptIds());
			
			List<ServerConceptBeaconEntry> entries = conceptDetails.getEntries();
			
			CompletableFuture<
				Map<KnowledgeBeaconImpl, 
				List<BeaconConceptWithDetails>>
			> future = kbs.getConceptDetails(clique, beacons, sessionId);
	
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconConceptWithDetails>
			> conceptDetailsByBeacon = waitFor(
						future,
						weightedTimeout(beacons,1)
					);  // Scale timeout proportionately to the number of beacons only?
		
			for (KnowledgeBeacon beacon : conceptDetailsByBeacon.keySet()) {
				
				for (BeaconConceptWithDetails response : conceptDetailsByBeacon.get(beacon)) {
					
					/*
					 * Simple heuristic to set the name to something sensible.
					 * Since beacon-to-beacon names may diverge, may not always
					 * give the "best" name (if such a thing exists...)
					 */
					if( conceptDetails.getName() == null )
						conceptDetails.setName(response.getName());
					
					ServerConceptBeaconEntry entry = Translator.translate(response);
					entry.setBeacon(beacon.getId());
					entries.add(entry);
				}
			}
			
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return conceptDetails;

	}
	
/******************************** STATEMENTS Data Access *************************************/


	/*
	 * @param conceptId
	 * @param conceptName
	 * @param identifiers
	 * @return
	 */
	private Boolean matchToList(String conceptId, String conceptName, List<String> identifiers ) {
		
		String idPattern = "(?i:"+conceptId+")";
		
		/*
		 *  Special test for the presence of 
		 *  Human Gene Nomenclature Consortium (and geneCards) symbols.
		 *  Case insensitive match to non-human species symbols
		 *  which may have difference letter case?
		 */
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		
		List<ServerStatement> responses = new ArrayList<ServerStatement>();
		
		try {
			
			if(source.isEmpty()) {
				throw new RuntimeException("ControllerImpl.getStatements(): empty source clique string encountered?") ;
			}
			
			ConceptClique sourceClique = exactMatchesHandler.getClique(source);
			if(sourceClique==null) {
				throw new RuntimeException("ControllerImpl.getStatements(): source clique '"+source+"' could not be found?") ;
			}

			ConceptClique targetClique = null;
			if(!target.isEmpty()) {
				targetClique = exactMatchesHandler.getClique(target);
				if(targetClique==null) {
					throw new RuntimeException("ControllerImpl.getStatements(): target clique '"+target+"' could not be found?") ;
				}
			}
			
			Map<
				KnowledgeBeacon, 
				List<BeaconStatement>
			> beaconStatements = 
						blackboard.getStatements(
								sourceClique, relations, targetClique, 
								keywords, conceptTypes, 
								pageNumber, pageSize, 
								beacons, sessionId
						) ;

			for (KnowledgeBeacon beacon : beaconStatements.keySet()) {
				
				String beaconId = beacon.getId();
				
				_logger.debug("ctrl.getStatements(): processing beacon '"+beaconId+"'...");
			
				for ( BeaconStatement response : beaconStatements.get(beacon)) {
					
					/*
					 * Sanity check: to get around the fact that some beacons 
					 * (like Biolink) will sometimes send back statements
					 *  with a null *%$@?!?!!! subject or object 
					 */
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
					 */
					String subjectTypeId = subject.getType();
					
					List<ConceptTypeEntry> subjectTypes = 
							conceptTypeService.lookUpByIdentifier(subjectTypeId);
					
					subject.setType(curieList(subjectTypes));
					
					ConceptClique subjectEcc = 
							exactMatchesHandler.getExactMatches(
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
					 */
					String objectTypeId = object.getType();
					
					List<ConceptTypeEntry> objectTypes = 
							conceptTypeService.lookUpByIdentifier(objectTypeId);

					object.setType(curieList(objectTypes));
					
					ConceptClique objectEcc = 
							exactMatchesHandler.getExactMatches(
													beacon,
													objectId,
													objectName,
													objectTypes
												);
					
					/*
					 * Need to refresh the ecc clique in case either 
					 * subject or object id was discovered to belong 
					 * to it during the exact matches operations above?
					 */
					sourceClique = exactMatchesHandler.getClique(source);
					
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
						 */
						String ssg = subject.getType();
						if( ( ssg==null || ssg.isEmpty() || ssg.equals(Category.DEFAULT_SEMANTIC_GROUP)) && sourceClique != null )
							subject.setType(sourceClique.getConceptType());
						
						object.setClique(objectEcc.getId());
						/*
						 * Temporary workaround for beacons not yet 
						 * setting their statement object semantic groups?
						 */
						String osg = object.getType();
						if( ( osg==null || osg.isEmpty() || osg.equals(Category.DEFAULT_SEMANTIC_GROUP)) && objectEcc != null )
							object.setType(objectEcc.getConceptType());
						
					} else if( matchToList( objectId, objectName, conceptIds ) ) {
						
						object.setClique(sourceClique.getId()) ;
						/*
						 * Temporary workaround for beacons not yet 
						 * setting their statement object semantic groups?
						 */
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
						 */
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
					 */
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
					
					responses.add(translation);
				}
			}
			
			if( ! relations.isEmpty() ) {
				final String relationFilter = relations;
				responses = responses.stream()
						.filter(
							s -> s.getPredicate().getId().equals(relationFilter) ? true : false 
				).collect(Collectors.toList());
			}
			
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return responses;

		//////////////////////
		
		/*
		 * Look for existing concept relationship statements 
		 * cached within the blackboard (Neo4j) database
		 */
		List<BeaconStatement> statements = 
				getStatementsFromDatabase( 
						sourceClique,  relations, targetClique, 
						keywords, conceptTypes, 
						pageNumber, pageSize,
						beacons
				);
    	
		// If none found, harvest concepts from the Beacon network
	    	if (statements.isEmpty())
	    		
	    		statements = 
	    				beaconHarvestService.harvestStatements(
	    	    				keywords,
	    	    				conceptTypes,
	    	    				pageNumber,
	    	    				pageSize,
	    	    				beacons,
	    	    				sessionId
	    	    			);
		
	    	return statements;

		
		/*
    			List<ServerStatement> statements = 
    								statementCache.getStatements(
						    			source, relations, target, 
						    			keywords,  conceptTypes, pageNumber, pageSize, 
						    			beacons, sessionId
						        );
		
		*/
		
		CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconStatement>>> future = 
				kbs.getStatements( sourceClique, relations, targetClique, keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId );
		
		Map<
			KnowledgeBeaconImpl, 
			List<BeaconStatement>
		> map = waitFor(future,weightedTimeout(beacons, pageSize));

		return (Map)map;
	}
		
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ServerAnnotation>  getEvidence(
					String statementId,
					String keywords,
					Integer pageNumber,
					Integer pageSize,
					List<String> beacons,
					String sessionId
	) throws BlackboardException {
		
		List<ServerAnnotation> responses = new ArrayList<ServerAnnotation>();
		
		try {
			
			CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconAnnotation>>> future = 
					kbs.getEvidence(statementId, keywords, pageNumber, pageSize, beacons, sessionId);
			
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconAnnotation>
			> evidence = waitFor(future,weightedTimeout(beacons, pageSize));
			
			for (KnowledgeBeacon beacon : evidence.keySet()) {
				for (BeaconAnnotation reference : evidence.get(beacon)) {
					ServerAnnotation translation = ModelConverter.convert(reference, ServerAnnotation.class);
					translation.setBeacon(beacon.getId());
					responses.add(translation);
				}
			}
			
		} catch (Exception e) {
			throw new BlackboardException(e);
		}
		
		return responses;
	}

}
