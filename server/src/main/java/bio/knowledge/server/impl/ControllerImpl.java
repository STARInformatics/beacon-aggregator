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
	
	private <T> Map<bio.knowledge.aggregator.KnowledgeBeacon, List<T>> get(CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<T>>> future) {
		try {
			return future.get(TIMEOUT, TIMEUNIT);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}
	
	public ResponseEntity<List<Concept>> getConcepts(String keywords, String semgroups, Integer pageNumber,
			Integer pageSize, List<String> beacons, String sessionId) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		semgroups = fixString(semgroups);
		beacons = fixString(beacons);

		CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2002>>>
			future = kbs.getConcepts(keywords, semgroups, pageNumber, pageSize, beacons);

		List<Concept> responses = new ArrayList<Concept>();
		Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2002>> map = get(future);
		
		for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
			for (Object response : map.get(beacon)) {
				Concept translation = ModelConverter.convert(response, Concept.class);
				translation.setBeacon(beacon.getId());
				responses.add(translation);
			}
		}

		return ResponseEntity.ok(responses);
	}

	public ResponseEntity<List<ConceptDetail>> getConceptDetails(String conceptId, List<String> beacons, String sessionId) {
		conceptId = fixString(conceptId);
		beacons = fixString(beacons);
		
		CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2001>>>
			future = kbs.getConceptDetails(conceptId, beacons);

		List<ConceptDetail> responses = new ArrayList<ConceptDetail>();
		Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2001>> map = get(future);
		
		for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
			for (Object response : map.get(beacon)) {
				ConceptDetail translation = ModelConverter.convert(response, ConceptDetail.class);
//				InlineResponse2001 translation = Translator.translate(response);
				translation.setBeacon(beacon.getId());
				responses.add(translation);
			}
		}

		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<Annotation>> getEvidence(String statementId, String keywords, Integer pageNumber, Integer pageSize, List<String> beacons, String sessionId) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		statementId = fixString(statementId);
		beacons = fixString(beacons);
		
		CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2004>>> future = 
				kbs.getEvidences(statementId, keywords, pageNumber, pageSize, beacons);
		
		List<Annotation> responses = new ArrayList<Annotation>();
		Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2004>> map = get(future);
		
		for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
			for (Object response : map.get(beacon)) {
				Annotation translation = ModelConverter.convert(response, Annotation.class);
				translation.setBeacon(beacon.getId());
				responses.add(translation);
			}
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<Statement>> getStatements(
			List<String> c, Integer pageNumber, Integer pageSize,
			String keywords, String semgroups, List<String> beacons, String sessionId) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		semgroups = fixString(semgroups);
		beacons = fixString(beacons);
		c = fixString(c);
		
		CompletableFuture<Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2003>>> future = 
				kbs.getStatements(c, keywords, semgroups, pageNumber, pageSize, beacons);
		
		List<Statement> responses = new ArrayList<Statement>();
		Map<bio.knowledge.aggregator.KnowledgeBeacon, List<bio.knowledge.client.model.InlineResponse2003>> map = get(future);
		
		for (bio.knowledge.aggregator.KnowledgeBeacon beacon : map.keySet()) {
			for (Object response : map.get(beacon)) {
				Statement translation = ModelConverter.convert(response, Statement.class);
				translation.setBeacon(beacon.getId());
				responses.add(translation);
			}
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<Summary>> linkedTypes() {
		CompletableFuture<List<bio.knowledge.client.model.InlineResponse200>> future = kbs.linkedTypes();
		List<Summary> responses = new ArrayList<Summary>();
		try {
			for (Object r : future.get(TIMEOUT, TIMEUNIT)) {
				responses.add(ModelConverter.convert(r, Summary.class));
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		return ResponseEntity.ok(responses);
    }
	
	public ResponseEntity<List<KnowledgeBeacon>> getBeacons() {
		
		List<KnowledgeBeacon> beacons = new ArrayList<>();
		for (bio.knowledge.aggregator.KnowledgeBeacon beacon : registry.getKnowledgeBeacons()) {
			beacons.add(Translator.translate(beacon));
		}
		
		return ResponseEntity.ok(beacons);
	}

	public ResponseEntity<List<LogEntry>> getErrors(String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

}
