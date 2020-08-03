package bio.knowledge.server.api;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import bio.knowledge.server.controller.ConceptsController;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryBeaconStatus;
import bio.knowledge.server.model.ServerConceptsQueryStatus;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Make sure that the Aggregator server is running.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConceptsTest {
	@Autowired
	ConceptsApiController controller;
	
//	@Autowired
//	TestRepository repository;
	
	@Test
	public void testWorkflow() {
//		repository.clearDatabase();
//		
//		assertTrue(repository.isEmpty());
		
		ResponseEntity<ServerConceptsQuery> postResponse = controller.postConceptsQuery(
				Arrays.asList("e"), 
				Collections.emptyList(), 
				Collections.emptyList()
		);
		
		assertThat(postResponse.getStatusCode(), Matchers.equalTo(HttpStatus.OK));
		
		String id = postResponse.getBody().getQueryId();
		
		
		ResponseEntity<ServerConceptsQueryStatus> statusResponse = null;
		
		while(!isFinished(statusResponse)) {
			statusResponse = controller.getConceptsQueryStatus(id, Collections.emptyList());
		}
		
		assertThat(statusResponse.getStatusCode(), Matchers.equalTo(HttpStatus.OK));
		
		assertThat(statusResponse.getBody().getQueryId(), Matchers.equalTo(id));
		
		for (ServerConceptsQueryBeaconStatus d : statusResponse.getBody().getStatus()) {
			assertThat(d.getStatus(), Matchers.equalTo(200));
			assertThat(d.getDiscovered(), Matchers.greaterThan(0));
		}
	}
	
	private boolean isFinished(ResponseEntity<ServerConceptsQueryStatus> statusResponse) {
		if (statusResponse == null) {
			return false;
		}
		
		for (ServerConceptsQueryBeaconStatus d : statusResponse.getBody().getStatus()) {
			if (d.getCount() == null) {
				return false;
			}
		}
		
		return true;
	}
}
