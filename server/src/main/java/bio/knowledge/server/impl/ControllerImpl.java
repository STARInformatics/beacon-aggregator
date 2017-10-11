/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
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
package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.database.repository.ConceptCliqueRepository;
import bio.knowledge.model.ConceptClique;
import bio.knowledge.server.model.ServerAnnotation;
import bio.knowledge.server.model.ServerConcept;
import bio.knowledge.server.model.ServerConceptDetail;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import bio.knowledge.server.model.ServerLogEntry;
import bio.knowledge.server.model.ServerPredicate;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerSubject;
import bio.knowledge.server.model.ServerSummary;

@Service
public class ControllerImpl {
	
	Map<String, HashSet<String>> cache = new HashMap<String, HashSet<String>>();

	public static final long DEFAULT_TIMEOUT = 10;
	public static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;
	
	@Autowired KnowledgeBeaconService kbs;
	
	@Autowired KnowledgeBeaconRegistry registry;
	
	@Autowired ConceptCliqueRepository conceptCliqueRepository;
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;

	private Integer fixInteger(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	private String fixString(String str) {
		return str != null ? str : "";
	}
	
	private List<String> fixString(List<String> l) {
		if (l == null) return new ArrayList<>();
		
		for (int i = 0; i < l.size(); i++) {
			l.set(i, fixString(l.get(i)));
		}
		
		return l;
	}
	
	private <T> List<T> listOfOne(T item) {
		List<T> list = new ArrayList<>();
		list.add(item);
		return list;
	}
	
	/**
	 * 
	 * @param request
	 * @return url used to make the request
	 */
	private String getUrl(HttpServletRequest request) {
		String query = request.getQueryString();
		query = (query == null)? "" : "?" + query;
		return request.getRequestURL() + query;
	}
	
	private void logError(String sessionId, Exception e) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		kbs.logError(sessionId, "aggregator", getUrl(request), e.getMessage());
	}
	
	
	private <T> Map<KnowledgeBeaconImpl, List<T>> waitFor(CompletableFuture<Map<KnowledgeBeaconImpl, List<T>>> future) {
		return waitFor(future,DEFAULT_TIMEOUT) ; 
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
			return future.get(timeout, TIMEUNIT);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}
	
	/*
	 * Dynamically compute adjustment to
	 * query timeouts proportionately to 
	 * the number of beacons and pageSize
	 */
	private long weightedTimeout( List<String> beacons, Integer pageSize) {
		long timescale;
		if(!beacons.isEmpty()) 
			timescale = beacons.size();
		else
			timescale = registry.countAllBeacons();
		
		timescale *= Math.max(1,pageSize/10) ;
		
		return timescale*DEFAULT_TIMEOUT;
	}
	
	public ResponseEntity<List<ServerConcept>> getConcepts(String keywords, String semgroups, Integer pageNumber,
			Integer pageSize, List<String> beacons, String sessionId) {
		try {
			
			pageNumber = fixInteger(pageNumber);
			pageSize = fixInteger(pageSize);
			keywords = fixString(keywords);
			semgroups = fixString(semgroups);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
	
			CompletableFuture<Map<KnowledgeBeaconImpl, List<bio.knowledge.client.model.BeaconConcept>>>
				future = kbs.getConcepts(keywords, semgroups, pageNumber, pageSize, beacons, sessionId);
	
			List<ServerConcept> responses = new ArrayList<ServerConcept>();
			Map<KnowledgeBeaconImpl, List<bio.knowledge.client.model.BeaconConcept>> map = 
					waitFor(
							future,
							weightedTimeout(beacons,pageSize)
					);
			
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				
				for (Object response : map.get(beacon)) {
					
					ServerConcept translation = ModelConverter.convert(response, ServerConcept.class);
					
					/*
					 *  RMB Sept 25, 2017: 
					 *  Slightly different need for resolution of the
					 *  equivalent concept clique here: every keyword
					 *  matched concept may be distinct so each has
					 *  its own clique. This may introduce a significant
					 *  amount of computational overhead... we'll need
					 *  to review options for performance enhancements?
					 */
					ConceptClique ecc = 
							exactMatchesHandler.getExactMatchesSafe(
										listOfOne(translation.getId())
									);
					translation.setClique(ecc.getId());
					translation.setAliases(ecc.getConceptIds());
					
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

	public ResponseEntity<List<ServerConceptDetail>> getConceptDetails(String conceptId, List<String> beacons, String sessionId) {
		try {
		
			conceptId = fixString(conceptId);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
			
			//List<String> c = exactMatchesHandler.getExactMatchesSafe(listOfOne(conceptId), sessionId);
			ConceptClique ecc = exactMatchesHandler.getExactMatchesSafe(listOfOne(conceptId));
			List<String> conceptIds = ecc.getConceptIds();
			
			CompletableFuture<Map<KnowledgeBeaconImpl, List<bio.knowledge.client.model.BeaconConceptWithDetails>>>
				//future = kbs.getConceptDetails(c, beacons, sessionId);
				future = kbs.getConceptDetails(conceptIds, beacons, sessionId);
	
			List<ServerConceptDetail> responses = new ArrayList<ServerConceptDetail>();
			Map<
				KnowledgeBeaconImpl, 
				List<bio.knowledge.client.model.BeaconConceptWithDetails>
			> map = waitFor(
					future,
					weightedTimeout(beacons,1)
			);  // Scale timeout proportionately to the number of beacons only?
					
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					ServerConceptDetail translation = ModelConverter.convert(response, ServerConceptDetail.class);
					translation.setClique(ecc.getId());
					translation.setAliases(ecc.getConceptIds());
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
	
	public ResponseEntity<List<ServerAnnotation>> getEvidence(String statementId, String keywords, Integer pageNumber, Integer pageSize, List<String> beacons, String sessionId) {
		try {
		
			pageNumber = fixInteger(pageNumber);
			pageSize = fixInteger(pageSize);
			keywords = fixString(keywords);
			statementId = fixString(statementId);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
			
			CompletableFuture<Map<KnowledgeBeaconImpl, List<bio.knowledge.client.model.BeaconEvidence>>> future = 
					kbs.getEvidences(statementId, keywords, pageNumber, pageSize, beacons, sessionId);
			
			List<ServerAnnotation> responses = new ArrayList<ServerAnnotation>();
			Map<
				KnowledgeBeaconImpl, 
				List<bio.knowledge.client.model.BeaconEvidence>
			> map = waitFor(future,weightedTimeout(beacons, pageSize));
					
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					ServerAnnotation translation = ModelConverter.convert(response, ServerAnnotation.class);
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
	
	private Boolean matchToList(String target, List<String> identifiers ) {
		String pattern = "(?i:"+target+")";
		for(String id : identifiers) {
			if(id.matches(pattern)) return true;
		}
		return false;
	}
	
	public ResponseEntity<List<ServerStatement>> getStatements(
			List<String> c, Integer pageNumber, Integer pageSize,
			String keywords, String semgroups, List<String> beacons, String sessionId) {
		try {
			
			pageNumber = fixInteger(pageNumber);
			pageSize = fixInteger(pageSize);
			keywords = fixString(keywords);
			semgroups = fixString(semgroups);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
			c = fixString(c);
			
			ConceptClique ecc = exactMatchesHandler.getExactMatchesSafe(c);
			List<String> conceptIds = ecc.getConceptIds();
			
			CompletableFuture<Map<KnowledgeBeaconImpl, List<bio.knowledge.client.model.BeaconStatement>>> future = 
					//kbs.getStatements(c, keywords, semgroups, pageNumber, pageSize, beacons, sessionId);
					kbs.getStatements(conceptIds, keywords, semgroups, pageNumber, pageSize, beacons, sessionId);
			
			List<ServerStatement> responses = new ArrayList<ServerStatement>();
			Map<
				KnowledgeBeaconImpl, 
				List<bio.knowledge.client.model.BeaconStatement>
			> map = waitFor(future,weightedTimeout(beacons, pageSize));

			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					ServerStatement translation = ModelConverter.convert(response, ServerStatement.class);
					translation.setBeacon(beacon.getId());
					
					// Heuristic: need to somehow tag the equivalent concept here?
					ServerSubject subject  = translation.getSubject();
					String subjectId = subject.getId();
					bio.knowledge.server.model.ServerObject object = translation.getObject();
					String objectId = object.getId();

					List<String>  otherIds = new ArrayList<String>();
					ConceptClique otherEcc = null;
					
					if( matchToList(subjectId,conceptIds) ) {
						
						subject.setClique(ecc.getId());
						
						/* 
						 * A bit of extra overhead here but we ideally need 
						 * to chase after the equivalent concept clique 
						 * of the corresponding object concept too; 
						 * perhaps need to get these out of a session cache?
						 */
						otherIds.add(objectId) ;
						otherEcc = exactMatchesHandler.getExactMatchesSafe(otherIds);
						object.setClique(otherEcc.getId());
						
					} else if( matchToList(objectId,conceptIds)) {
						
						object.setClique(ecc.getId()) ; 
						
						/* 
						 * A bit of extra overhead here but we ideally need 
						 * to chase after the equivalent concept clique 
						 * of the corresponding subject concept too; 
						 * perhaps need to get these out of a session cache?
						 */
						otherIds.add(subjectId) ;
						otherEcc = exactMatchesHandler.getExactMatchesSafe(otherIds);
						subject.setClique(otherEcc.getId());
						
					} // else, not sure why nothing hit here? Fail silently, clique not set?
					
					responses.add(translation);
				}
			}
			
			return ResponseEntity.ok(responses);
		} catch (Exception e) {
			logError(sessionId, e);
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	public ResponseEntity<List<ServerSummary>> linkedTypes(List<String> beacons, String sessionId) {
		try {
			
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
		
			CompletableFuture<Map<KnowledgeBeaconImpl, List<bio.knowledge.client.model.BeaconSummary>>>
				future = kbs.linkedTypes(beacons, sessionId);
			
			List<ServerSummary> responses = new ArrayList<ServerSummary>();
			Map<
				KnowledgeBeaconImpl, 
				List<bio.knowledge.client.model.BeaconSummary>
			> map = waitFor(future);
			
			for (KnowledgeBeaconImpl beacon : map.keySet()) {
				for (Object summary : map.get(beacon)) {
					ServerSummary translation = ModelConverter.convert(summary, ServerSummary.class);
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
	
	public ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons() {
		
		List<ServerKnowledgeBeacon> beacons = new ArrayList<>();
		for (Object beacon : registry.getKnowledgeBeacons()) {
			beacons.add(ModelConverter.convert(beacon, ServerKnowledgeBeacon.class));
		}
		
		return ResponseEntity.ok(beacons);
	}

	public ResponseEntity<List<ServerLogEntry>> getErrors(String sessionId) {
		sessionId = fixString(sessionId);

		List<ServerLogEntry> responses = new ArrayList<>();
		for (Object entry : kbs.getErrors(sessionId)) {
			if (entry != null) {
				responses.add(ModelConverter.convert(entry, ServerLogEntry.class));
			}
		}

		return ResponseEntity.ok(responses);
	}

	public ResponseEntity<List<ServerPredicate>> getPredicates() {
		List<ServerPredicate> responses = new ArrayList<>();
		return ResponseEntity.ok(responses);
	}

}
