package bio.knowledge.server.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.Util;
import bio.knowledge.aggregator.ConceptCategoryService;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.api.ConceptsApi;
import bio.knowledge.client.api.StatementsApi;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.aggregator.BeaconCitationRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.ConceptQuery;
import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;
import bio.knowledge.server.blackboard.ConceptsDatabaseInterface;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;

@Controller
public class ConceptsController {
	private static final Logger logger = LoggerFactory.getLogger(ConceptsController.class);

	@Autowired
	TaskProcessor processor;
	@Autowired
	KnowledgeBeaconRegistry registry;

	public ResponseEntity<ServerConceptWithDetails> getConceptDetails(String cliqueId, List<Integer> beacons) {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		int size = threadSet.size();
		ServerConceptWithDetails s = new ServerConceptWithDetails();
		s.setName("Thread count: " + String.valueOf(size));

		return ResponseEntity.ok(s);
	}

	public ResponseEntity<ServerConceptsQueryResult> getConcepts(String queryId, List<Integer> beacons,
			Integer pageNumber, Integer pageSize) {
		ServerConceptsQueryResult result = new ServerConceptsQueryResult();

		return ResponseEntity.ok(result);
	}

	public ResponseEntity<ServerConceptsQueryStatus> getConceptsQueryStatus(String queryId, List<Integer> beacons) {
		return null;
	}

	public ResponseEntity<ServerConceptsQuery> postConceptsQuery(
			List<String> keywords, 
			List<String> categories,
			List<Integer> beacons
	) {
		String queryId = RandomStringUtils.randomAlphanumeric(20);
		
		ConceptQuery query = new ConceptQuery(queryId);
		
		StatementsApi statementsApi;
//		statementsApi.getStatements(s, edgeLabel, relation, t, keywords, categories, size)

		query.getKeywords().addAll(keywords != null ? keywords : Collections.emptySet());
		query.getCategories().addAll(categories != null ? categories : Collections.emptySet());
		query.getBeacons().addAll(beacons != null ? beacons : Collections.emptySet());

		for (KnowledgeBeacon beacon : registry.getKnowledgeBeacons()) {
			if (shouldSkip(beacon, beacons)) {
				continue;
			}

			Runnable runnable = () -> {
				ConceptsApi api = new ConceptsApi();
				api.setApiClient(((KnowledgeBeaconImpl) beacon).getApiClient());

				List<BeaconConcept> concepts;

				try {
					concepts = api.getConcepts(keywords, categories, 1000);
				} catch (ApiException e) {
					throw new RuntimeException(e);
				}

				saveConcepts(beacon.getId(), concepts);
			};

			processor.add(queryId, runnable);
		}

		ServerConceptsQuery serverConceptsQuery = new ServerConceptsQuery();
		
		serverConceptsQuery.setCategories(categories);
		serverConceptsQuery.setKeywords(keywords);
		serverConceptsQuery.setQueryId(queryId);

		return ResponseEntity.ok(serverConceptsQuery);
	}

	/**
	 * The given beacon should be skipped if the beacons filter is not empty,
	 * but doesn't contain it.
	 */
	private boolean shouldSkip(KnowledgeBeacon beacon, List<Integer> beacons) {
		return beacons != null && !beacons.isEmpty() && !beacons.contains(beacon.getId());
	}

	@Autowired
	private ConceptCategoryService conceptTypeService;
	@Autowired
	private ConceptRepository conceptRepository;
	@Autowired
	private BeaconRepository beaconRepository;
	@Autowired
	private ExactMatchesHandler exactMatchesHandler;
	@Autowired
	private BeaconCitationRepository beaconCitationRepository;

	private void saveConcepts(int beaconId, List<BeaconConcept> concepts) {
		List<Neo4jConcept> neo4jConcepts = new ArrayList<>();
		
		for (BeaconConcept concept : concepts) {
			// Resolve concept type(s)
			List<String> categoryLabels = concept.getCategories();
			Set<Neo4jConceptCategory> categories = new HashSet<Neo4jConceptCategory>();
			if (categoryLabels != null && !categoryLabels.isEmpty()) {
				for (String categoryLabel : categoryLabels) {
					Neo4jConceptCategory category = conceptTypeService.lookUp(beaconId, categoryLabel);
					categories.add(category);
				}
			}

			// Retrieve or create associated ConceptClique
			Neo4jConceptClique conceptClique = exactMatchesHandler.getConceptCliqueFromDb(concept.getId());

			Neo4jConcept neo4jConcept = null;
			
			if (conceptClique != null) {
				// Retrieve Neo4jConcept by clique if exists, or create new
				// Neo4jConcept
				String cliqueId = conceptClique.getId();
				neo4jConcept = conceptRepository.getByClique(cliqueId);
			} else {
				conceptClique = exactMatchesHandler.createConceptClique(concept.getId(), beaconId, "");
				logger.error("clique for id: " + concept.getId() + "doesn't exist, but it should have been created earlier");
			}

			// Enrich the list perhaps?
			// categories.addAll(conceptTypeService.getNonDefaultConceptCategoriesByClique(beaconId,
			// conceptClique));

			if (neo4jConcept == null) {
				neo4jConcept = new Neo4jConcept();
				neo4jConcept.setClique(conceptClique);
			}

			neo4jConcept.setName(concept.getName());
			neo4jConcept.setTypes(categories);
			neo4jConcept.setSynonyms(new ArrayList<>());
			neo4jConcept.setDefinition(concept.getDescription());

			/*
			 * Keep track of this concept entry with the current QueryTracker.
			 * Unfortunately, we don't yet track beacon-specific data
			 * associations
			 */
//			neo4jConcept.addQuery(query.getQueryTracker());

			/*
			 * Add this beacon to the set of beacons which have cited this
			 * concept
			 */
			Neo4jKnowledgeBeacon beacon = beaconRepository.getBeacon(beaconId);
			
			Neo4jBeaconCitation citation = beaconCitationRepository.findByBeaconAndObjectId(
					beacon.getBeaconId(),
					concept.getId()
			);
			
			if (citation == null) {
				citation = new Neo4jBeaconCitation(beacon, concept.getId());
				citation = beaconCitationRepository.save(citation);
			}
			
			neo4jConcept.addBeaconCitation(citation);

			// Save the new or updated Concept object
			neo4jConcepts.add(neo4jConcept);
		}
		
		conceptRepository.saveAll(neo4jConcepts);
	}
}
