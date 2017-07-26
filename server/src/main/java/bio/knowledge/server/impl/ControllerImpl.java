package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.server.model.InlineResponse200;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.InlineResponse2004;
import bio.knowledge.server.model.Translator;

@Service
public class ControllerImpl {
	
	Map<String, HashSet<String>> cache = new HashMap<String, HashSet<String>>();
	
	private static final long TIMEOUT = 1;
	private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;

	@Autowired
	KnowledgeBeaconService kbs;

	private Integer fixInteger(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	private String fixString(String str) {
		return str != null ? str : "";
	}
	
	private List<String> fixString(List<String> l) {
		for (int i = 0; i < l.size(); i++) {
			l.set(i, fixString(l.get(i)));
		}
		
		return l;
	}
	
	private <T> List<T> asList(Collection<T> collection) {
		List<T> list = new ArrayList<>();
		list.addAll(collection);
		return list;
	}
	
	private List<String> getExactMatches(List<String> newMatches) {
		Set<String> knownMatches = new HashSet<>();
		
		for (String match : newMatches) {
			if (cache.containsKey(match)) {
				knownMatches.addAll(cache.get(match));
				knownMatches.add(match);
			}
		}
		
		newMatches.removeAll(knownMatches);
		
		try {
			
			List<String> oldmatches = newMatches;
			CompletableFuture<List<String>> future;
			
			while (!newMatches.isEmpty()) {
				
				knownMatches.addAll(newMatches);
				future = kbs.getExactMatchesToConceptList(newMatches);
				newMatches = future.get(TIMEOUT, TIMEUNIT);
				newMatches.removeAll(knownMatches); // guard against infinite loop due to redundant results
			}
			
			for (String oldmatch : oldmatches) {
				for (String knownmatch : knownMatches) {
					if (cache.containsKey(oldmatch)) {
						cache.get(oldmatch).add(knownmatch);
					} else {
						HashSet<String> set = new HashSet<String>();
						set.add(knownmatch);
						cache.put(oldmatch, set);
					}
				}
			}
			
			return asList(knownMatches);
			
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		} 
	}

	public ResponseEntity<List<InlineResponse2002>> getConcepts(String keywords, String semgroups, Integer pageNumber,
			Integer pageSize) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		semgroups = fixString(semgroups);

		CompletableFuture<List<bio.knowledge.client.model.InlineResponse2002>> future = kbs.getConcepts(keywords,
				semgroups, pageNumber, pageSize);

		List<InlineResponse2002> responses = new ArrayList<InlineResponse2002>();

		try {
			for (bio.knowledge.client.model.InlineResponse2002 r : future.get(TIMEOUT, TIMEUNIT)) {
				responses.add(Translator.translate(r));
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}

		return ResponseEntity.ok(responses);
	}

	public ResponseEntity<List<InlineResponse2001>> getConceptDetails(String conceptId) {
		conceptId = fixString(conceptId);
		
		CompletableFuture<List<bio.knowledge.client.model.InlineResponse2001>> future = kbs
				.getConceptDetails(conceptId);

		List<InlineResponse2001> responses = new ArrayList<InlineResponse2001>();

		try {
			for (bio.knowledge.client.model.InlineResponse2001 r : future.get(TIMEOUT, TIMEUNIT)) {
				responses.add(Translator.translate(r));
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}

		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse2004>> getEvidence(String statementId, String keywords, Integer pageNumber, Integer pageSize) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		statementId = fixString(statementId);
		
		CompletableFuture<List<bio.knowledge.client.model.InlineResponse2004>> future = 
				kbs.getEvidences(statementId, keywords, pageNumber, pageSize);
		List<InlineResponse2004> responses = new ArrayList<InlineResponse2004>();
		
		try {
			for (bio.knowledge.client.model.InlineResponse2004 r : future.get(TIMEOUT, TIMEUNIT)) {
				responses.add(Translator.translate(r));
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse2003>> getStatements(
			List<String> c, Integer pageNumber, Integer pageSize,
			String keywords, String semgroups) {
		pageNumber = fixInteger(pageNumber);
		pageSize = fixInteger(pageSize);
		keywords = fixString(keywords);
		semgroups = fixString(semgroups);
		c = fixString(c);
		
		c = getExactMatches(c);
		
		CompletableFuture<List<bio.knowledge.client.model.InlineResponse2003>> future = 
				kbs.getStatements(c, keywords, semgroups, pageNumber, pageSize);
		
		List<InlineResponse2003> responses = new ArrayList<InlineResponse2003>();
		
		try {
			for (bio.knowledge.client.model.InlineResponse2003 r : future.get(TIMEOUT, TIMEUNIT)) {
				responses.add(Translator.translate(r));
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		
		return ResponseEntity.ok(responses);
	}
	
	public ResponseEntity<List<InlineResponse200>> linkedTypes() {
		CompletableFuture<List<bio.knowledge.client.model.InlineResponse200>> future = kbs.linkedTypes();
		List<InlineResponse200> responses = new ArrayList<InlineResponse200>();
		try {
			for (bio.knowledge.client.model.InlineResponse200 r : future.get(TIMEOUT, TIMEUNIT)) {
				responses.add(Translator.translate(r));
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
		return ResponseEntity.ok(responses);
    }
	
	public ResponseEntity<List<String>> getExactMatchesToConcept(String conceptId) {
		
		conceptId = fixString(conceptId);

		List<String> c = new ArrayList<>();
		c.add(conceptId);
				
		List<String> response = getExactMatches(c);
		
		if (response.size() == 1) {
			// return empty list if concept is completely unrecognized
			
			try {
				CompletableFuture<List<String>> future = kbs.getExactMatchesToConcept(conceptId);
				response = future.get(TIMEOUT, TIMEUNIT);
			
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(), e.getCause());
			} 
		}
		
		return ResponseEntity.ok(response);
	}
	
	public ResponseEntity<List<String>> getExactMatchesToConceptList(List<String> c) {
		
		c = fixString(c);
		
		List<String> response = getExactMatches(c);
		response.removeAll(c);
		
		return ResponseEntity.ok(response);
	}
}
