package bio.knowledge.server.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.BeaconItemWrapper;
import bio.knowledge.aggregator.BeaconConceptWrapper;
import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.Harvester;
import bio.knowledge.aggregator.Harvester.BeaconInterface;
import bio.knowledge.aggregator.Harvester.DatabaseInterface;
import bio.knowledge.aggregator.Harvester.RelevanceTester;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.aggregator.QueryTracker;
import bio.knowledge.aggregator.Timer;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.model.ConceptType;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.server.model.ServerConcept;

@Service
public class ConceptHarvestService {
	
	private final String KEYWORD_DELIMINATOR = " ";
	
	@Autowired private KnowledgeBeaconService kbs;
	@Autowired private QueryTracker queryTracker;
	@Autowired private ConceptTypeService conceptTypeService;
	@Autowired private TaskExecutor executor;	
	@Autowired private ConceptRepository  conceptRepository;
	
	public CompletableFuture<List<ServerConcept>> initiateConceptHarvest(
			String keywords,
			String conceptTypes,
			Integer pageNumber,
			Integer pageSize,
			List<String> beacons,
			String sessionId
	) {
		if (beacons == null) {
			beacons = new ArrayList<String>();
		}
		
		Harvester<BeaconConcept, ServerConcept> harvester = new Harvester<BeaconConcept, ServerConcept>(
				buildBeaconInterface(keywords, conceptTypes, beacons, sessionId),
				buildDatabaseInterface(),
				buildRelevanceTester(keywords, conceptTypes),
				executor,
				queryTracker
		);
		
		return harvester.initiateHarvest(keywords, conceptTypes, pageNumber, pageSize);
	}
	
	private RelevanceTester<BeaconConcept> buildRelevanceTester(String keywords, String conceptTypes) {
		return new RelevanceTester<BeaconConcept>() {

			@Override
			public boolean isItemRelevant(BeaconItemWrapper<BeaconConcept> beaconItemWrapper) {
				BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
				BeaconConcept concept = conceptWrapper.getItem();
				
				String[] keywordsArray = keywords.split(KEYWORD_DELIMINATOR);
				
				if (conceptTypes != null && !conceptTypes.toLowerCase().contains(concept.getSemanticGroup().toLowerCase())) {
					return false;
				}
				
				for (String keyword : keywordsArray) {
					if (concept.getName().toLowerCase().contains(keyword.toLowerCase())) {
						return true;
					}
				}
				
				return false;
			}
			
		};
	}

	private BeaconInterface<BeaconConcept> buildBeaconInterface(String keywords, String conceptTypes, List<String> beacons, String sessionId) {
		return new BeaconInterface<BeaconConcept>() {

			@Override
			public Map<KnowledgeBeaconImpl, List<BeaconItemWrapper<BeaconConcept>>> getDataFromBeacons(Integer pageNumber,
					Integer pageSize) throws InterruptedException, ExecutionException, TimeoutException {
				Timer.setTime("Search concept: " + keywords);
				CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconItemWrapper<BeaconConcept>>>>
					future = kbs.getConcepts(keywords, conceptTypes, pageNumber, pageSize, beacons, sessionId);
				return future.get(
						KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,
						KnowledgeBeaconService.BEACON_TIMEOUT_UNIT
				);
			}
		};
	}
	
	private DatabaseInterface<BeaconConcept, ServerConcept> buildDatabaseInterface() {
		return new DatabaseInterface<BeaconConcept, ServerConcept>() {

			@Override
			public boolean cacheData(KnowledgeBeaconImpl kb, BeaconItemWrapper<BeaconConcept> beaconItemWrapper, String queryString) {
				BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
				BeaconConcept concept = conceptWrapper.getItem();
				
				ConceptType conceptType = conceptTypeService.lookUp(concept.getSemanticGroup());
				Neo4jConcept neo4jConcept = new Neo4jConcept();
				
				List<ConceptType> types = new ArrayList<ConceptType>();
				types.add(conceptType);
				
				neo4jConcept.setClique(conceptWrapper.getClique());
				neo4jConcept.setName(concept.getName());
				neo4jConcept.setTypes(types);
				neo4jConcept.setQueryFoundWith(queryString);
				neo4jConcept.setSynonyms(concept.getSynonyms());
				neo4jConcept.setDefinition(concept.getDefinition());
				
				if (!conceptRepository.exists(neo4jConcept.getClique(), queryString)) {
					conceptRepository.save(neo4jConcept);
					return true;
				} else {
					return false;
				}
			}

			@Override
			public List<ServerConcept> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize) {
				return ConceptHarvestService.this.getDataPage(keywords, conceptTypes, pageNumber, pageSize);
			}
		};
	}
	
	public List<ServerConcept> getDataPage(String keywords, String types, Integer pageNumber, Integer pageSize) {
		String queryString = Harvester.makeQueryString("concept", keywords, types);
		String[] keywordArray = keywords != null ? keywords.split(" ") : null;
		String[] typesArray = types != null ? types.split(" ") : new String[0];
		pageNumber = pageNumber != null && pageNumber > 0 ? pageNumber : 1;
		pageSize = pageSize != null && pageSize > 0 ? pageSize : 5;
		
		List<Neo4jConcept> neo4jConcepts = conceptRepository.apiGetConcepts(keywordArray, typesArray, queryString, pageNumber, pageSize);
		
		List<ServerConcept> concepts = new ArrayList<ServerConcept>();
		
		for (Neo4jConcept neo4jConcept : neo4jConcepts) {
			ServerConcept concept = new ServerConcept();
			concept.setClique(neo4jConcept.getClique());
			concept.setName(neo4jConcept.getName());
			concept.setTaxon(neo4jConcept.getTaxon());
			concept.setType(neo4jConcept.getType().getName());
			
			concepts.add(concept);
		}
		
		return concepts;
	}
	
	protected Integer fixInteger(Integer i) {
		return i != null && i >= 1 ? i : 1;
	}

	protected String fixString(String str) {
		return str != null ? str : "";
	}
	
	protected List<String> fixString(List<String> l) {
		if (l == null) return new ArrayList<>();
		
		for (int i = 0; i < l.size(); i++) {
			l.set(i, fixString(l.get(i)));
		}
		
		return l;
	}
}
