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
			public boolean isItemRelevant(BeaconConcept dataItem) {
				String[] keywordsArray = keywords.split(KEYWORD_DELIMINATOR);
				
				if (!conceptTypes.toLowerCase().contains(dataItem.getSemanticGroup().toLowerCase())) {
					return false;
				}
				
				for (String keyword : keywordsArray) {
					if (dataItem.getName().toLowerCase().contains(keyword.toLowerCase())) {
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
			public Map<KnowledgeBeaconImpl, List<BeaconConcept>> getDataFromBeacons(Integer pageNumber,
					Integer pageSize) throws InterruptedException, ExecutionException, TimeoutException {
				Timer.setTime("Search concept: " + keywords);
				CompletableFuture<Map<KnowledgeBeaconImpl, List<BeaconConcept>>>
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
			public boolean cacheData(KnowledgeBeaconImpl kb, BeaconConcept data, String queryString) {
				ConceptType conceptType = conceptTypeService.lookUp(data.getSemanticGroup());
				Neo4jConcept neo4jConcept = new Neo4jConcept();
				
				neo4jConcept.setClique(data.cliqueId);
				neo4jConcept.setName(data.getName());
				neo4jConcept.setType(conceptType);
				neo4jConcept.setQueryFoundWith(queryString);
				neo4jConcept.setSynonyms(data.getSynonyms());
				neo4jConcept.setDefinition(data.getDefinition());
				
				if (!conceptRepository.exists(neo4jConcept.getClique(), queryString)) {
					conceptRepository.save(neo4jConcept);
					return true;
				} else {
					return false;
				}
			}

			@Override
			public List<ServerConcept> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize) {
				return getDataPage(keywords, conceptTypes, pageNumber, pageSize);
			}
		};
	}
	
	public List<ServerConcept> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize) {
		return null;
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
