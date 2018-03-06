package bio.knowledge.aggregator.blackboard;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.BeaconConceptWrapper;
import bio.knowledge.aggregator.BeaconItemWrapper;
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
import bio.knowledge.model.ConceptTypeEntry;
import bio.knowledge.model.neo4j.Neo4jConcept;

@Service
public class ConceptHarvestService {
	
	private final String KEYWORD_DELIMINATOR = " ";
	
	@Autowired private KnowledgeBeaconService kbs;
	@Autowired private QueryTracker<BeaconConcept> queryTracker;
	@Autowired private ConceptTypeService conceptTypeService;
	@Autowired private ConceptRepository  conceptRepository;
	@Autowired private TaskExecutor executor;

	public List<BeaconConcept> harvestConcepts(
			String keywords,
			String conceptTypes,
			Integer pageNumber,
			Integer pageSize,
			List<String> beacons,
			String sessionId
	) {
		List<BeaconConcept> concepts = null ;
		
		CompletableFuture<List<BeaconConcept>> f = 
    			initiateConceptHarvest(
    				keywords,
    				conceptTypes,
    				pageNumber,
    				pageSize,
    				beacons,
    				sessionId
    			);

		try {
			
		concepts = f.get(
				KnowledgeBeaconService.BEACON_TIMEOUT_DURATION,
				KnowledgeBeaconService.BEACON_TIMEOUT_UNIT
		);
		
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			
			e.printStackTrace();
			
			concepts = new ArrayList<BeaconConcept>();
		}
		
		return concepts;
	}
	
	public CompletableFuture<List<BeaconConcept>> initiateConceptHarvest(
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
		
		Harvester<BeaconConcept, BeaconConcept> harvester = 
				new Harvester<BeaconConcept, BeaconConcept>(
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
	
	// TODO: The purpose and nature of this class needs to be reviewed
	private DatabaseInterface<BeaconConcept, BeaconConcept> buildDatabaseInterface() {
		
		return new DatabaseInterface<BeaconConcept, BeaconConcept>() {

			@Override
			public boolean cacheData(KnowledgeBeaconImpl kb, BeaconItemWrapper<BeaconConcept> beaconItemWrapper, String queryString) {
				BeaconConceptWrapper conceptWrapper = (BeaconConceptWrapper) beaconItemWrapper;
				BeaconConcept concept = conceptWrapper.getItem();
				
				ConceptTypeEntry conceptType = conceptTypeService.lookUp(concept.getSemanticGroup());
				Neo4jConcept neo4jConcept = new Neo4jConcept();
				
				List<ConceptTypeEntry> types = new ArrayList<ConceptTypeEntry>();
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
			public List<BeaconConcept> getDataPage(String keywords, String conceptTypes, Integer pageNumber, Integer pageSize) {
				// TODO: I'm not sure if this action is relevant at this level of the system
				//return ConceptHarvestService.this.getDataPage(keywords, conceptTypes, pageNumber, pageSize);
				return new ArrayList<BeaconConcept>();
			}
		};
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
