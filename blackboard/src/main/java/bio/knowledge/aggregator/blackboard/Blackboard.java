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
package bio.knowledge.aggregator.blackboard;

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
import bio.knowledge.client.model.BeaconAnnotation;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptType;
import bio.knowledge.client.model.BeaconConceptWithDetails;
import bio.knowledge.client.model.BeaconPredicate;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.model.aggregator.ConceptClique;

/**
 * @author richard
 *
 */
@Service
public class Blackboard implements SystemTimeOut {
	
	@Autowired private KnowledgeBeaconRegistry registry;
	
	@Autowired private ConceptHarvestService conceptHarvestService;
	
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
	public List<LogEntry> getErrors(String sessionId) {
		return kbs.getErrors(sessionId);
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

	public List<BeaconConcept> getConcepts(
			String keywords, 
			String conceptTypes, 
			Integer pageNumber, 
			Integer pageSize,
			List<String> beacons, 
			String sessionId
	) {
		List<BeaconConcept> concepts = conceptHarvestService.getDataPage(keywords, conceptTypes, pageNumber, pageSize);
    	
    	pageSize = pageSize != null ? pageSize : 10;
    	
    	if (concepts.size() < pageSize) {
    		
    		CompletableFuture<List<BeaconConcept>> f = 
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
		
    	return concepts;

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
