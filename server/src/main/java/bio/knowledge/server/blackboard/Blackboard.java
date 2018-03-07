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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bio.knowledge.SystemTimeOut;

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
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerCliqueIdentifier;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerLogEntry;

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
public class Blackboard implements SystemTimeOut, Query {
	
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
	public ServerCliqueIdentifier getClique(String identifier, String sessionId) throws BlackboardException {
		
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
	public  Map<
				KnowledgeBeacon, 
				List<BeaconConceptWithDetails>
			> getConceptDetails(
							ConceptClique clique, 
							List<String> beacons, 
							String sessionId
	) {
		
		CompletableFuture<
			Map<KnowledgeBeaconImpl, 
			List<BeaconConceptWithDetails>>
		> future = kbs.getConceptDetails(clique, beacons, sessionId);

		Map<
			KnowledgeBeaconImpl, 
			List<BeaconConceptWithDetails>
		> map = waitFor(
					future,
					weightedTimeout(beacons,1)
				);  // Scale timeout proportionately to the number of beacons only?
		
		return (Map)map;

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<
				KnowledgeBeacon, 
				List<BeaconStatement>
			>  getStatements(
					ConceptClique sourceClique,
					String relations,
					ConceptClique targetClique,
					String keywords,
					String conceptTypes,
					Integer pageNumber, 
					Integer pageSize, 
					List<String> beacons, 
					String sessionId
	) {
		
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
	public Map<
				KnowledgeBeacon, 
				List<BeaconAnnotation>
	
			> getEvidence(
					String statementId,
					String keywords,
					Integer pageNumber,
					Integer pageSize,
					List<String> beacons,
					String sessionId
	) {
		
		CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconAnnotation>>> future = 
				kbs.getEvidence(statementId, keywords, pageNumber, pageSize, beacons, sessionId);
		Map<
			KnowledgeBeaconImpl, 
			List<BeaconAnnotation>
		> evidence = waitFor(future,weightedTimeout(beacons, pageSize));
		
		return (Map)evidence;
	}

}
