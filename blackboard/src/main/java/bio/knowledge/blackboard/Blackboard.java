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
package bio.knowledge.blackboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.SystemTimeOut;
import bio.knowledge.aggregator.BeaconConceptType;
import bio.knowledge.aggregator.BeaconKnowledgeMap;
import bio.knowledge.aggregator.BeaconPredicateMap;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.aggregator.LogEntry;
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconSummary;
import bio.knowledge.model.BioNameSpace;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.umls.Category;
import bio.knowledge.ontology.ConceptType;
import bio.knowledge.server.impl.Translator;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementSubject;

/**
 * @author richard
 *
 */
@Service
public class Blackboard implements SystemTimeOut {
	
	@Autowired private KnowledgeBeaconRegistry registry;
	
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<KnowledgeBeacon> getKnowledgeBeacons() {
		 return (List<KnowledgeBeacon>)(List)registry.getKnowledgeBeacons();
	}

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
	public List<LogEntry> getErrors(String sessionId) {
		return kbs.getErrors(sessionId);
	}

	/**
	 * 
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public  List<BeaconConceptType> getConceptTypes(
					List<String> beacons,
					String sessionId
	) {
		try {
		
			CompletableFuture<
				Map<
					KnowledgeBeaconImpl, 
					List<BeaconSummary>
				>
			> future = kbs.getConceptTypes(beacons, sessionId);
			
			Map<
				KnowledgeBeaconImpl, 
				List<BeaconSummary>
			> map = waitFor(future);
			
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				
				for (BeaconSummary summary : map.get(beacon)) {
					
					summary.setId(
							conceptCliqueService.fixConceptType(null, summary.getId())
					);
					
					ServerConceptType translation = ModelConverter.convert(summary, ServerConceptType.class);
					
					translation.setId(
							conceptCliqueService.fixConceptType(null, translation.getId())
					);
					
					translation.setBeacon(beacon.getId());	
					responses.add(translation);
				}
			}
	
			return ResponseEntity.ok(responses);
		
		} catch (Exception e) {
			logError(sessionId, e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	@Autowired private PredicatesRegistry predicatesRegistry;

	/**
	 * 
	 * @return
	 */
	public List<BeaconPredicateMap> getPredicates(List<String> beacons, String sessionId) {
		
		try {
			
			CompletableFuture<
				Map<KnowledgeBeaconImpl, 
				List<BeaconPredicate>>
			> future = kbs.getAllPredicates();

			Map<KnowledgeBeaconImpl, List<BeaconPredicate>> map = waitFor( future );

			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (BeaconPredicate response : map.get(beacon)) {
					/*
					 *  No "conversion" here, but response 
					 *  handled by the indexPredicate function
					 */
					predicatesRegistry.indexPredicate(response,beacon.getId());
				}
			}
			
			List<ServerPredicate> responses = 
					new ArrayList<ServerPredicate>(predicatesRegistry.values());
			
			return ResponseEntity.ok(responses);

		} catch (Exception e) {
			logError("Predicates", e);
			return ResponseEntity.ok(new ArrayList<>());
		}
		 return kbs.getAllPredicates();
	}
	

	public List<BeaconKnowledgeMap> getKnowledgeMap(List<String> beacons, String sessionId) {
		// TODO Implement me!
		//return new ArrayList<BeaconKnowledgeMap>();
		throw new RuntimeException("Implement me!");
	}

/******************************** CONCEPT Data Access *************************************/

	public List<BeaconConcept> getConcepts(
			String keywords, 
			String conceptTypes, 
			Integer pageNumber, 
			Integer pageSize,
			List<String> beacons, 
			String sessionId
	) {
		List<ServerConcept> concepts = conceptHarvestService.getDataPage(keywords, conceptTypes, pageNumber, pageSize);
    	
    	pageSize = pageSize != null ? pageSize : 10;
    	
    	if (concepts.size() < pageSize) {
    		
    		CompletableFuture<List<ServerConcept>> f = 
	    			conceptHarvestService.initiateConceptHarvest(
	    				keywords,
	    				conceptTypes,
	    				pageNumber,
	    				pageSize,
	    				beacons,
	    				sessionId
	    			);
		
    		try {
    			
			concepts = f.get(
					KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,
					KnowledgeBeaconService.BEACON_TIMEOUT_UNIT
			);
			
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
    	}
		
    	return ResponseEntity.ok(concepts);

	}

	/**
	 * 
	 * @param clique
	 * @param beacons
	 * @param sessionId
	 * @return
	 */
	public  Map<
				KnowledgeBeaconImpl, 
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
		
		return map;

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
    			List<ServerStatement> statements = 
    								statementCache.getStatements(
						    			source, relations, target, 
						    			keywords,  conceptTypes, pageNumber, pageSize, 
						    			beacons, sessionId
						        );
		
		*/

		/* 
		 * If the beacon aggregator client is attempting relation filtering
		 * then we should ensure that the PredicateRegistry is initialized
		 */
		if(relations!=null && predicatesRegistry.isEmpty()) getPredicates();
		
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
			
		Map<KnowledgeBeacon,BeaconAnnotation> annotationMap = 
								new HashMap<KnowledgeBeacon,BeaconAnnotation>();
		
		CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconAnnotation>>> future = 
				kbs.getEvidence(statementId, keywords, pageNumber, pageSize, beacons, sessionId);
		Map<
			KnowledgeBeaconImpl, 
			List<BeaconAnnotation>
		> evidence = waitFor(future,weightedTimeout(beacons, pageSize));
		
		return (Map)evidence;
	}
}
