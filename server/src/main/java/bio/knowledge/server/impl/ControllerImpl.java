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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.server.model.InlineResponse200;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.InlineResponse2004;
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
	
	private <T> Map<KnowledgeBeacon, List<T>> get(CompletableFuture<Map<KnowledgeBeacon, List<T>>> future) {
		try {
			return future.get(TIMEOUT, TIMEUNIT);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}
	
	public ResponseEntity<List<InlineResponse2002>> getConcepts(String keywords, String semgroups, Integer pageNumber,
			Integer pageSize, List<String> beacons, String sessionId) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		semgroups = fixString(semgroups);
		beacons = fixString(beacons);

		CompletableFuture<Map<KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2002>>>
			future = kbs.getConcepts(keywords, semgroups, pageNumber, pageSize, beacons);

		List<InlineResponse2002> responses = new ArrayList<InlineResponse2002>();
		Map<KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2002>> map = get(future);
		
		for (KnowledgeBeacon beacon : map.keySet()) {
			for (Object response : map.get(beacon)) {
				InlineResponse2002 translation = ModelConverter.convert(response, InlineResponse2002.class);
				translation.setBeacon(beacon.getId());
				responses.add(translation);
			}
		}

		return ResponseEntity.ok(responses);
	}

	public ResponseEntity<List<InlineResponse2001>> getConceptDetails(String conceptId, List<String> beacons, String sessionId) {
		conceptId = fixString(conceptId);
		beacons = fixString(beacons);
		
		CompletableFuture<Map<KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2001>>>
			future = kbs.getConceptDetails(conceptId, beacons);

		List<InlineResponse2001> responses = new ArrayList<InlineResponse2001>();
		Map<KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2001>> map = get(future);
		
		for (KnowledgeBeacon beacon : map.keySet()) {
			for (Object response : map.get(beacon)) {
				InlineResponse2001 translation = ModelConverter.convert(response, InlineResponse2001.class);
//				InlineResponse2001 translation = Translator.translate(response);
				translation.setBeacon(beacon.getId());
				responses.add(translation);
			}
		}

		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse2004>> getEvidence(String statementId, String keywords, Integer pageNumber, Integer pageSize, List<String> beacons, String sessionId) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		statementId = fixString(statementId);
		beacons = fixString(beacons);
		
		CompletableFuture<Map<KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2004>>> future = 
				kbs.getEvidences(statementId, keywords, pageNumber, pageSize, beacons);
		
		List<InlineResponse2004> responses = new ArrayList<InlineResponse2004>();
		Map<KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2004>> map = get(future);
		
		for (KnowledgeBeacon beacon : map.keySet()) {
			for (Object response : map.get(beacon)) {
				InlineResponse2004 translation = ModelConverter.convert(response, InlineResponse2004.class);
				translation.setBeacon(beacon.getId());
				responses.add(translation);
			}
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse2003>> getStatements(
			List<String> c, Integer pageNumber, Integer pageSize,
			String keywords, String semgroups, List<String> beacons, String sessionId) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		semgroups = fixString(semgroups);
		beacons = fixString(beacons);
		c = fixString(c);
		
		CompletableFuture<Map<KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2003>>> future = 
				kbs.getStatements(c, keywords, semgroups, pageNumber, pageSize, beacons);
		
		List<InlineResponse2003> responses = new ArrayList<InlineResponse2003>();
		Map<KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2003>> map = get(future);
		
		for (KnowledgeBeacon beacon : map.keySet()) {
			for (Object response : map.get(beacon)) {
				InlineResponse2003 translation = ModelConverter.convert(response, InlineResponse2003.class);
				translation.setBeacon(beacon.getId());
				responses.add(translation);
			}
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse200>> linkedTypes() {
		CompletableFuture<List<bio.knowledge.client.model.InlineResponse200>> future = kbs.linkedTypes();
		List<InlineResponse200> responses = new ArrayList<InlineResponse200>();
		try {
			for (Object r : future.get(TIMEOUT, TIMEUNIT)) {
				responses.add(ModelConverter.convert(r, InlineResponse200.class));
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		return ResponseEntity.ok(responses);
    }
	
	public ResponseEntity<List<Beacon>> getBeacons() {
		
		List<Beacon> beacons = new ArrayList<>();
		for (KnowledgeBeacon beacon : registry.getKnowledgeBeacons()) {
			beacons.add(Translator.translate(beacon));
		}
		
		return ResponseEntity.ok(beacons);
	}

	public ResponseEntity<List<LogEntry>> getErrors(String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

}
