package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.BaseCache;
import bio.knowledge.aggregator.ConceptCliqueService;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.QueryTracker;
import bio.knowledge.aggregator.Timer;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.umls.Category;
import bio.knowledge.server.model.ServerConcept;

@Service
public class ConceptCache extends BaseCache<ServerConcept> {
	
	@Autowired ControllerImpl     ctrl;
	@Autowired ConceptRepository  conceptRepository;
	@Autowired ConceptTypeService conceptTypeService;
	@Autowired private ConceptCliqueService conceptCliqueService;
	@Autowired private ExactMatchesHandler exactMatchesHandler;

	@Autowired private QueryTracker<ServerConcept> queryTracker;
	protected QueryTracker<ServerConcept> getQueryTracker() {
		return queryTracker;
	}
	
	@Autowired private TaskExecutor executor;
	protected TaskExecutor getExecutor() {
		return executor;
	}

	public CompletableFuture<List<ServerConcept>> initiateConceptHarvest(
			String keywords,
			String conceptTypes,
			Integer requestPageNumber,
			Integer requestPageSize,
			List<String> beacons,
			String sessionId
	) {
		final int pageNumber = sanitizeInt(requestPageNumber);
		final int pageSize = sanitizeInt(requestPageSize);
		
		BeaconInterface<ServerConcept> beaconInterface = 
				new BeaconInterface<ServerConcept>() {

			@Override
			public ResponseEntity<List<ServerConcept>> getData(Integer pageNumber, Integer pageSize) throws InterruptedException, ExecutionException, TimeoutException {
				return ctrl.getConcepts(keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId);
			}

		};
		
		DatabaseInterface<ServerConcept> databaseInterface = 
				new DatabaseInterface<ServerConcept>() {

			@Override
			public boolean cacheData(ServerConcept data, String queryString) {
				return cacheConcept(data, queryString);
			}

			@Override
			public List<ServerConcept> getDataPage() {
				return getConceptsFromDb(keywords, conceptTypes, pageNumber, pageSize);
			}
			
		};
		
		RelevanceTester<ServerConcept> relevanceTester = new RelevanceTester<ServerConcept>() {

			@Override
			public boolean isPageRelevant(Collection<ServerConcept> data) {
				return ConceptCache.this.isPageRelevant(keywords, data);
			}
			
		};
		
		String queryString = makeQueryString("concept", keywords, conceptTypes);
		int threashold = makeThreshold(pageNumber, pageSize);
		
		return initiateHarvest(queryString, threashold, beaconInterface, databaseInterface, relevanceTester);
	}
	
	@Async private boolean cacheConcept(ServerConcept concept, String queryString) {
		ConceptTypeEntry conceptType = conceptTypeService.lookUp(concept.getType());
		Neo4jConcept neo4jConcept = new Neo4jConcept();
		
		neo4jConcept.setClique(concept.getClique());
		neo4jConcept.setName(concept.getName());
		List<ConceptTypeEntry> types = new ArrayList<ConceptTypeEntry>();
		types.add(conceptType);
		neo4jConcept.setTypes(types);
		neo4jConcept.setQueryFoundWith(queryString);
		
		if (!conceptRepository.exists(neo4jConcept.getClique(), queryString)) {
			conceptRepository.save(neo4jConcept);
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isPageRelevant(String keywords, Collection<ServerConcept> dataPage) {
		for (String keyword : split(keywords)) {
			for (ServerConcept concept : dataPage) {
				if (concept.getName().toLowerCase().contains(keyword.toLowerCase())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private List<ServerConcept> getConceptsFromDb(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize) {
		String queryString = makeQueryString("concept", keywords, conceptTypes);
		pageNumber = sanitizeInt(pageNumber);
		pageSize = sanitizeInt(pageSize);
		String[] keywordsArray = split(keywords);
		String[] conceptTypesArray = split(conceptTypes);

		List<Neo4jConcept> neo4jConcepts = conceptRepository.apiGetConcepts(keywordsArray, conceptTypesArray, queryString,
				pageNumber, pageSize);
		List<ServerConcept> serverConcepts = new ArrayList<ServerConcept>();
		for (Neo4jConcept neo4jConcept : neo4jConcepts) {
			ServerConcept serverConcept = new ServerConcept();
			
			serverConcept.setName(neo4jConcept.getName());
			serverConcept.setClique(neo4jConcept.getClique());
			serverConcept.setType(neo4jConcept.getType().getName());
//			serverConcept.setDefinition(neo4jConcept.getDescription());
//			serverConcept.setType(neo4jConcept.getConceptType().toString());
//			serverConcept.setSynonyms(Arrays.asList(neo4jConcept.getSynonyms().split(" ")));
			
			ConceptClique ecc = 
					exactMatchesHandler.getClique2(neo4jConcept.getClique());
			
			
			String str = Category.OBJC.toString();
			if (neo4jConcept.getType() == Category.OBJC) {
				str = "";
			}
			
			String type = conceptCliqueService.fixConceptType(ecc, str);
			serverConcept.setType(type);

			serverConcepts.add(serverConcept);
		}

		return serverConcepts;
	}
	
	public List<ServerConcept> getConcepts(
			String keywords,
			String conceptTypes,
			Integer pageNumber,
			Integer pageSize,
			List<String> beacons,
			String sessionId
			) {
		
		pageNumber = BaseCache.sanitizeInt(pageNumber);
		pageSize = BaseCache.sanitizeInt(pageSize);

		List<ServerConcept> concepts = getConceptsFromDb(keywords, conceptTypes, pageNumber, pageSize);

		if (concepts.size() < pageSize) {
			CompletableFuture<List<ServerConcept>> future = initiateConceptHarvest(
					keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId
					);

			try {
				Timer.setTime("ApiController future.get()");
				concepts = future.get();
				Timer.printTime("ApiController future.get()");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		return concepts;
	}

}
