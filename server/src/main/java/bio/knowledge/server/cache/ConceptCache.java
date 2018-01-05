package bio.knowledge.server.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.ConceptCliqueService;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.model.ConceptType;
import bio.knowledge.model.aggregator.ConceptClique;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.impl.ExactMatchesHandler;
import bio.knowledge.server.impl.Translator;
import bio.knowledge.server.model.ServerConcept;

@Service
public class ConceptCache {

	private static final int PAGE_SIZE = 2;
	private static final int TIMEOUT = 15;
	private static final int EXTRA_TIME_INCREMENT_AMOUNT = 5;
	private static final int MAX_TIMEOUT = 60;
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	@Autowired
	ConceptTypeService conceptTypeService;
	@Autowired
	ConceptRepository conceptRepository;
	@Autowired
	KnowledgeBeaconService kbs;
	@Autowired
	QueryTracker queryTracker;
	@Autowired
	TaskExecutor executor;
	
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	@Autowired private ConceptCliqueService conceptCliqueService;

//	@Async
//	public CompletableFuture<List<BeaconConcept>> initiateConceptHarvest(String keywords, String conceptTypes,
//			int pageNumber, int pageSize, List<String> beacons, String sessionId) {
//		final String query = keywords + ";" + (conceptTypes != null ? conceptTypes : "");
//
//		CompletableFuture<List<BeaconConcept>> future = new CompletableFuture();
//
//		if (!queryTracker.isWorking(query)) {
//			queryTracker.addQuery(query, future);
//			
//			executor.execute(() -> {
//				int extraTime = 0;
//				int dataCount = 0;
//
//				while (queryTracker.isWorking(query)) {
//					CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconConcept>>> beaconFuture = kbs.getConcepts(
//							keywords, conceptTypes, queryTracker.getPageNumber(query), PAGE_SIZE, beacons, sessionId);
//
//					try {
//						Map<KnowledgeBeaconImpl, List<BeaconConcept>> results = beaconFuture.get(TIMEOUT + extraTime,
//								TIME_UNIT);
//
//						List<BeaconConcept> concepts = new ArrayList<BeaconConcept>();
//
//						for (KnowledgeBeaconImpl beacon : results.keySet()) {
//							concepts.addAll(results.get(beacon));
//						}
//
//						dataCount += concepts.size();
//
//						if (dataCount >= (pageNumber + 1) * pageSize) {
////							future.complete(getConcepts(keywords, conceptTypes, pageNumber, pageSize));
//						}
//
//						if (concepts.isEmpty()) {
//							break;
//						}
//
////						cacheConcepts(concepts);
//
//						queryTracker.incrementPageNumber(query);
//
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//						break;
//					} catch (ExecutionException e) {
//						e.printStackTrace();
//						break;
//					} catch (TimeoutException e) {
//						e.printStackTrace();
//						extraTime += EXTRA_TIME_INCREMENT_AMOUNT;
//
//						if (TIMEOUT + extraTime > MAX_TIMEOUT) {
//							break;
//						}
//					}
//				}
//			});
//		}
//
//		return future;
//	}
	
	@SuppressWarnings("unchecked")
	public CompletableFuture<List<ServerConcept>> getConceptFuture(String queryString) {
		return queryTracker.getFuture(queryString);
	}
	
	@Autowired ControllerImpl impl;
	
	public CompletableFuture<List<ServerConcept>> initateConceptHarvest3(
			String keywords, String conceptTypes,
			Integer requestPageNumber, Integer requestPageSize, List<String> beacons, String sessionId
	) {
		final int pageNumber = sanitizeInt(requestPageNumber);
		final int pageSize = sanitizeInt(requestPageSize);
		final String queryString = keywords + ";" + (conceptTypes != null ? conceptTypes : "");
		
		if (!queryTracker.isWorking(queryString)) {
			CompletableFuture<List<ServerConcept>> future = new CompletableFuture<List<ServerConcept>>();
			queryTracker.addQuery(queryString, future);
			
			executor.execute(() -> {
				System.out.println("Beginning executor");
				try {
					int N = 1;
					int dataCount = 0;
					final int threashold = ((pageNumber - 1) * pageSize) + pageSize;
					
					while (queryTracker.isWorking(queryString)) {
						try {
							ControllerImpl.setTime("total cache loop " + Integer.toString(N));
							ResponseEntity<List<ServerConcept>> r = impl.getConcepts(
									keywords, conceptTypes, N, PAGE_SIZE, beacons, sessionId
							);
							
							List<ServerConcept> concepts = r.getBody();
							
							for (ServerConcept concept : concepts) {
								if (cacheConcept(concept)) {
									dataCount += 1;
								}
							}
							
							System.out.println("Data found: " + Integer.toString(dataCount));
							
							if (dataCount >= threashold && !future.isDone()) {
								future.complete(getConcepts(keywords, conceptTypes, pageNumber, pageSize));
							}
							
							if (!isPageRelevant(keywords, concepts)) {
								break;
							}
							
							ControllerImpl.printTime("total cache loop " + Integer.toString(N));
							
							N += 1;
							
							
						} catch (TimeoutException e) {
							impl.increaseExtraTime(EXTRA_TIME_INCREMENT_AMOUNT);
							
							if (impl.isExtraTimeGreaterThan(MAX_TIMEOUT)) {
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
					
				} finally {
					impl.resetExtraTime();
					queryTracker.removeQuery(queryString);
					
					if (!future.isDone()) {
						future.complete(getConcepts(keywords, conceptTypes, pageNumber, pageSize));
					}
					
					System.out.println("WE'RE DONE!");
				}
			});
		}
		
		return getConceptFuture(queryString);
		
	}

//	public void initiateConceptHarvest2(
//			String keywords, String conceptTypes,
//			int pageNumber, int pageSize, List<String> beacons, String sessionId
//	) {
//		final String query = keywords + ";" + (conceptTypes != null ? conceptTypes : "");
//		
//		if (!queryTracker.isWorking(query)) {
//			final CompletableFuture<List<ServerConcept>> future = new CompletableFuture<List<ServerConcept>>();
//			
//			executor.execute(() -> {
//				int extraTime = 0;
//				int dataCount = 0;
//				
//				try {
//					queryTracker.addQuery(query, future);
//	
//					while (queryTracker.isWorking(query)) {
//						
//						try {
//							CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconConcept>>> beaconFuture = kbs.getConcepts(
//								keywords, conceptTypes, queryTracker.getPageNumber(query), PAGE_SIZE, beacons, sessionId);
//	
//							Map<KnowledgeBeaconImpl, List<BeaconConcept>> map = beaconFuture.get(TIMEOUT + extraTime, TIME_UNIT);
//	
//							Map<String, ServerConcept> responses = new HashMap<String, ServerConcept>();
//	
//							for (KnowledgeBeaconImpl beacon : map.keySet()) {
//	
//								for (BeaconConcept response : map.get(beacon)) {
//	
//									ServerConcept translation = Translator.translate(response);
//	
//									// First iteration, from beacons, is the Concept
//									// semantic type?
//									String conceptType = translation.getType();
//	
//									List<ConceptType> types = conceptTypeService.lookUpByIdentifier(conceptType);
//	
//									ConceptClique ecc = exactMatchesHandler.getExactMatches(beacon, response.getId(),
//											translation.getName(), types);
//	
//									String cliqueId = ecc.getId();
//									if (!responses.containsKey(cliqueId)) {
//	
//										translation.setClique(cliqueId);
//	
//										// fix the concept type if necessary
//										translation
//												.setType(conceptCliqueService.fixConceptType(ecc, translation.getType()));
//	
//										responses.put(cliqueId, translation);
//									}
//								}
//							}
//							
//							Collection<ServerConcept> concepts = responses.values();
//							
//							for (ServerConcept concept : concepts) {
//								if (cacheConcept(concept)) {
//									dataCount += 1;
//									System.out.println("Threashold: " + Integer.toString((pageNumber + 1) * pageSize));
//									System.out.println("Data Count: " + Integer.toString(dataCount));
//								}
//							}
//	
//							if (dataCount >= (pageNumber - 1) * pageSize) {
//								future.complete(getConcepts(keywords, conceptTypes, pageNumber, pageSize));
//							}
//	
//							if (concepts.isEmpty()) {
//								future.complete(new ArrayList<ServerConcept>());
//								break;
//							}
//							
//							if (!isPageRelevant(keywords, concepts)) {
//								future.complete(new ArrayList<ServerConcept>());
//								break;
//							}
//	
//							
//	
//							queryTracker.incrementPageNumber(query);
//	
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//							future.complete(new ArrayList<ServerConcept>());
//							break;
//						} catch (ExecutionException e) {
//							e.printStackTrace();
//							future.complete(new ArrayList<ServerConcept>());
//							break;
//						} catch (TimeoutException e) {
//							e.printStackTrace();
//							extraTime += EXTRA_TIME_INCREMENT_AMOUNT;
//	
//							if (TIMEOUT + extraTime > MAX_TIMEOUT) {
//								future.complete(new ArrayList<ServerConcept>());
//								break;
//							}
//						}
//					}
//				
//				} finally {
//					queryTracker.removeQuery(query);
//				}
//			});
//		}
//	}

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

	@Async private boolean cacheConcept(ServerConcept concept) {
		ConceptType conceptType = conceptTypeService.lookUp(concept.getType());
		Neo4jConcept neo4jConcept = new Neo4jConcept(concept.getClique(), conceptType, concept.getName());
		neo4jConcept.setClique(concept.getClique());
		neo4jConcept.setId(concept.getClique());
		neo4jConcept.setName(concept.getName());
		neo4jConcept.setConceptType(conceptType);
		neo4jConcept.setTaxon(concept.getTaxon());
//		neo4jConcept.setSynonyms(String.join(" ", concept.getSynonyms()));
//		neo4jConcept.setConceptType(conceptTypeService.lookUp(concept.getSemanticGroup()));

		if (!conceptRepository.exists(neo4jConcept.getId())) {
			conceptRepository.save(neo4jConcept);
			return true;
		} else {
			return false;
		}
	}

	public void cacheConcepts(Collection<ServerConcept> collection) {
//		executor.execute(() -> {
			for (ServerConcept concept : collection) {
				cacheConcept(concept);
			}
//		});
	}

	public List<ServerConcept> getConcepts(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize) {
		pageNumber = sanitizeInt(pageNumber);
		pageSize = sanitizeInt(pageSize);
		String[] keywordsArray = split(keywords);
		String[] conceptTypesArray = split(conceptTypes);

		List<Neo4jConcept> neo4jConcepts = conceptRepository.apiGetConcepts(keywordsArray, conceptTypesArray,
				pageNumber, pageSize);
		List<ServerConcept> serverConcepts = new ArrayList<ServerConcept>();
		for (Neo4jConcept neo4jConcept : neo4jConcepts) {
			ServerConcept serverConcept = new ServerConcept();
			serverConcept.setName(neo4jConcept.getName());
			serverConcept.setClique(neo4jConcept.getClique());
//			serverConcept.setDefinition(neo4jConcept.getDescription());
			serverConcept.setType(neo4jConcept.getConceptType().toString());
//			serverConcept.setSynonyms(Arrays.asList(neo4jConcept.getSynonyms().split(" ")));

			serverConcepts.add(serverConcept);
		}

		return serverConcepts;
	}
	
	private int sanitizeInt(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	private String[] split(String terms, String deliminator) {
		return terms != null && !terms.isEmpty() ? terms.split(deliminator) : null;
	}

	private String[] split(String terms) {
		return split(terms, " ");
	}

}