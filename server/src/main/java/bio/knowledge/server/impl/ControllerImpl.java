package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.server.model.KnowledgeBeacon;
import bio.knowledge.server.model.Summary;
import bio.knowledge.server.model.ConceptDetail;
import bio.knowledge.server.model.Concept;
import bio.knowledge.server.model.Statement;
import bio.knowledge.server.model.Annotation;
import bio.knowledge.server.model.LogEntry;
import bio.knowledge.database.repository.ConceptCliqueRepository;
import bio.knowledge.model.ConceptClique;

@Service
public class ControllerImpl {
	
	Map<String, HashSet<String>> cache = new HashMap<String, HashSet<String>>();
	
	public static final long TIMEOUT = 10;
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
	
	private String getUrl(HttpServletRequest request) {
		String query = request.getQueryString();
		query = (query == null)? "" : "?" + query;
		return request.getRequestURL() + query;
	}
	
	private void logError(String sessionId, Exception e) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		kbs.logError(sessionId, "aggregator", getUrl(request), e.getMessage());
	}
	
	private <T> Map<bio.knowledge.aggregator.KnowledgeBeacon, List<T>> waitFor(CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<T>>> future) {
		try {
			return future.get(TIMEOUT, TIMEUNIT);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}
	
	public ResponseEntity<List<Concept>> getConcepts(String keywords, String semgroups, Integer pageNumber,
			Integer pageSize, List<String> beacons, String sessionId) {
		try {
			
			pageNumber = fixInteger(pageNumber);
			pageSize = fixInteger(pageSize);
			keywords = fixString(keywords);
			semgroups = fixString(semgroups);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
	
			CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2002>>>
				future = kbs.getConcepts(keywords, semgroups, pageNumber, pageSize, beacons, sessionId);
	
			List<Concept> responses = new ArrayList<Concept>();
			Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2002>> map = waitFor(future);
			
			for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					Concept translation = ModelConverter.convert(response, Concept.class);
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

	public ResponseEntity<List<ConceptDetail>> getConceptDetails(String conceptId, List<String> beacons, String sessionId) {
		try {
		
			conceptId = fixString(conceptId);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
			
			List<String> c = exactMatchesHandler.getExactMatchesSafe(listOfOne(conceptId), sessionId);
			
			CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2001>>>
				future = kbs.getConceptDetails(c, beacons, sessionId);
	
			List<ConceptDetail> responses = new ArrayList<ConceptDetail>();
			Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2001>> map = waitFor(future);
			
			for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					ConceptDetail translation = ModelConverter.convert(response, ConceptDetail.class);
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
	
	public ResponseEntity<List<Annotation>> getEvidence(String statementId, String keywords, Integer pageNumber, Integer pageSize, List<String> beacons, String sessionId) {
		try {
		
			pageNumber = fixInteger(pageNumber);
			pageSize = fixInteger(pageSize);
			keywords = fixString(keywords);
			statementId = fixString(statementId);
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
			
			CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2004>>> future = 
					kbs.getEvidences(statementId, keywords, pageNumber, pageSize, beacons, sessionId);
			
			List<Annotation> responses = new ArrayList<Annotation>();
			Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2004>> map = waitFor(future);
			
			for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					Annotation translation = ModelConverter.convert(response, Annotation.class);
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
	
	public ResponseEntity<List<Statement>> getStatements(
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
			
			c = exactMatchesHandler.getExactMatchesSafe(c, sessionId);
			
			CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2003>>> future = 
					kbs.getStatements(c, keywords, semgroups, pageNumber, pageSize, beacons, sessionId);
			
			List<Statement> responses = new ArrayList<Statement>();
			Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2003>> map = waitFor(future);
			
			for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
				for (Object response : map.get(beacon)) {
					Statement translation = ModelConverter.convert(response, Statement.class);
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
	
	public ResponseEntity<List<Summary>> linkedTypes(List<String> beacons, String sessionId) {
		try {
			
			beacons = fixString(beacons);
			sessionId = fixString(sessionId);
		
			CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse200>>>
				future = kbs.linkedTypes(beacons, sessionId);
			
			List<Summary> responses = new ArrayList<Summary>();
			Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse200>> map = waitFor(future);
			
			for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
				for (Object summary : map.get(beacon)) {
					Summary translation = ModelConverter.convert(summary, Summary.class);
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
	
	public ResponseEntity<List<KnowledgeBeacon>> getBeacons() {
		
		List<KnowledgeBeacon> beacons = new ArrayList<>();
		for (Object beacon : registry.getKnowledgeBeacons()) {
			beacons.add(ModelConverter.convert(beacon, KnowledgeBeacon.class));
		}
		
		return ResponseEntity.ok(beacons);
	}

	public ResponseEntity<List<LogEntry>> getErrors(String sessionId) {
		sessionId = fixString(sessionId);

		List<LogEntry> responses = new ArrayList<>();
		for (Object entry : kbs.getErrors(sessionId)) {
			if (entry != null) {
				responses.add(ModelConverter.convert(entry, LogEntry.class));
			}
		}

		return ResponseEntity.ok(responses);
	}

}
