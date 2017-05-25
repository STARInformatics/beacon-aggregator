package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.server.model.InlineResponse200;
import bio.knowledge.server.model.InlineResponse2001;
import bio.knowledge.server.model.InlineResponse2002;
import bio.knowledge.server.model.InlineResponse2003;
import bio.knowledge.server.model.InlineResponse2004;
import bio.knowledge.server.model.Translator;
import io.swagger.annotations.ApiParam;

@Service
public class ControllerImpl {
	
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
}
