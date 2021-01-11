/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.lang.UnsupportedOperationException;
import java.util.*;
import java.util.stream.Collectors;

import bio.knowledge.client.ApiException;
import bio.knowledge.database.repository.aggregator.QueryTrackerRepository;
import bio.knowledge.model.aggregator.neo4j.*;
import bio.knowledge.server.controller.ControllerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.Util;
import bio.knowledge.aggregator.ConceptCategoryService;
import bio.knowledge.aggregator.ConceptsQueryInterface;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.aggregator.BeaconCitationRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerConcept;

/**
 * @author richard
 *
 */
@Component
public class ConceptsDatabaseInterface extends CoreDatabaseInterface<ConceptsQueryInterface,BeaconConcept,ServerConcept> implements Util {
	private static Logger _logger = LoggerFactory.getLogger(ConceptsDatabaseInterface.class);
	
	@Autowired private ConceptCategoryService conceptTypeService;
	@Autowired private ConceptRepository conceptRepository;
	@Autowired private BeaconRepository beaconRepository;
	@Autowired private ExactMatchesHandler exactMatchesHandler;
	@Autowired private BeaconCitationRepository beaconCitationRepository;
	@Autowired private QueryTrackerRepository queryTrackerRepository;
	
	@Autowired CliqueService cliqueService;

	/*
	 * MINOR ANXIETY ABOUT THIS PARTICULAR DATA ACCESS: 
	 * IS THERE ANY POSSIBILITY OF TWO THREADS 
	 * ACCESSING THE DATABASE IN INCONSISTENTLY? 
	 */

	public void loadData(String queryId, List<BeaconConcept> results, Integer beaconId) {
		List<List<BeaconConcept>> partitions = ControllerUtil.partition(results, 50);

		for (List<BeaconConcept> concepts : partitions) {
			loadDataBatch(queryId, concepts, beaconId);
		}

		queryTrackerRepository.updateQueryStatusState(
				queryId,
				beaconId,
				null,
				null,
				results.size(),
				results.size()
		);
	}
	 
	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#loadData(java.lang.Object, java.util.List, java.lang.Integer)
	 * 
	 * Loads data into blackboard database
	 * For each concept found:
	 * - searches for its type and adds to set (of 1?) - defaults to BiolinkTerm.NAMED_THING if can't find Biolink type
	 * - retrieve or create associated ConceptClique
	 * - create Concept node on the database and fill with data (name, synonym, definition, types)
	 * 
	 */
	public void loadDataBatch(String queryId, List<BeaconConcept> results, Integer beaconId) {
		List<String> conceptIds = new ArrayList<>();
		for (BeaconConcept concept : results) {
			conceptIds.add(concept.getId());
		}

		try {
			exactMatchesHandler.createAndGetConceptCliques(conceptIds);
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}

		Neo4jKnowledgeBeacon beacon = beaconRepository.getBeacon(beaconId);

		List<Neo4jConcept> neo4jConcepts = new ArrayList<>();

		for(BeaconConcept concept : results) {

				// Resolve concept type(s)
				List<String> categoryLabels = concept.getCategories();
				Set<Neo4jConceptCategory> categories = new HashSet<Neo4jConceptCategory>();
				if(!nullOrEmpty(categoryLabels)) {
					for (String categoryLabel : categoryLabels) {
						Neo4jConceptCategory category = conceptTypeService.lookUp(beaconId,categoryLabel);
						categories.add(category);
					}
				}
				
				// Retrieve or create associated ConceptClique
				Neo4jConceptClique conceptClique = exactMatchesHandler.getConceptCliqueFromDb(concept.getId());
				
				Neo4jConcept neo4jConcept = null;
				if (conceptClique != null) {
					// Retrieve Neo4jConcept by clique if exists, or create new Neo4jConcept
					String cliqueId = conceptClique.getId();
					neo4jConcept = conceptRepository.getByClique(cliqueId);
				} else {
					try {
						conceptClique = exactMatchesHandler.createConceptClique(concept.getId(), beaconId, "");
					} catch (ApiException e) {
						throw new RuntimeException(e);
					}
					_logger.error("clique for id: " + concept.getId() + "doesn't exist, but it should have been created earlier"); 
				}
				
				// Enrich the list perhaps?
				// categories.addAll(conceptTypeService.getNonDefaultConceptCategoriesByClique(beaconId, conceptClique)); 
				
				if(neo4jConcept == null) {
					neo4jConcept = new Neo4jConcept();
					neo4jConcept.setClique(conceptClique);
				}
				
				neo4jConcept.setName(concept.getName());
				neo4jConcept.setTypes(categories);
				neo4jConcept.setSynonyms(new ArrayList<>());
				neo4jConcept.setDefinition(concept.getDescription());

				/*
				 *  Keep track of this concept entry 
				 *  with the current QueryTracker.
				 *  Unfortunately, we don't yet track 
				 *  beacon-specific data associations
				 */
				Neo4jQueryTracker queryTracker = queryTrackerRepository.find(queryId);

				if (queryTracker != null) {
				    Neo4jQuery query = queryTracker.getQueries().stream().filter(q -> Objects.equals(q.getBeaconId(), beaconId))
                            .findAny()
                            .orElseThrow(() -> new RuntimeException("Cannot find query [queryId="+queryId+", beaconId="+beaconId+"]"));

				    neo4jConcept.addQuery(query);
				}
				
				/*
				 * Add this beacon to the set of beacons 
				 * which have cited this concept
				 */
				Neo4jBeaconCitation citation = 
						beaconCitationRepository.findByBeaconAndObjectId(
													beacon.getBeaconId(),
													concept.getId()
												);
				if(citation==null) {
					citation = new Neo4jBeaconCitation(beacon,concept.getId());
					citation = beaconCitationRepository.save(citation);
				}

				neo4jConcept.addBeaconCitation(citation);

				neo4jConcepts.add(neo4jConcept);
		}
		// Save the new or updated Concept object
		conceptRepository.saveAll(neo4jConcepts);

		queryTrackerRepository.incrementProcessed(queryId, beaconId, results.size());
	}

	@Override
	public void loadData(QuerySession<ConceptsQueryInterface> query, List<BeaconConcept> results, Integer beaconId) {
		throw new UnsupportedOperationException("loadData not yet implemented?");
	}

	@Override
	public List<ServerConcept> getDataPage(QuerySession<ConceptsQueryInterface> query, List<Integer> beacons) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#getDataPage(bio.knowledge.aggregator.QuerySession, java.util.List)
	 */
	public List<ServerConcept> getDataPage(String queryId, List<Integer> beacons, Integer pageNumber, Integer pageSize) {
		List<ServerConcept> serverConcepts = new ArrayList<>();

		/*
		 *  TODO: also need to filter beacons here against harvested list of beacons?
		 *
		 *  QueryTracker queryTracker = query.getQueryTracker();
		 *  // check which beacon data is wanted here?
		 */

		// TODO: retrieve and load the results here!
		// Should be a simple database query at this point
		// subject only to whether or not the given beacons have data?
		// should the user be warned if they ask for beacons that had error
		// or are incomplete, or should it silently fail for such beacons?

		//String queryString = query.makeQueryString();

//			ConceptsQueryInterface conceptQuery = query.getQuery();

		/*
		 * TODO: Fix this database retrieval call to reflect actual database contents
		 * Maybe ignore queryString (and beacons) for now(?)
		 */
		pageNumber = pageNumber != null ? pageNumber : 1;
		pageSize = pageSize != null ? pageSize : 100;

		List<LinkedHashMap<String, Object>> dbConceptList = conceptRepository.getConceptsByQueryId(queryId, beacons, pageNumber, pageSize);

		for (LinkedHashMap<String, Object> record : dbConceptList) {
			Neo4jConcept dbConcept = (Neo4jConcept) record.get("concept");

			ServerConcept serverConcept = new ServerConcept();
			serverConcept.setName(dbConcept.getName());

			Neo4jConceptClique clique = (Neo4jConceptClique) record.get("clique");

			String cliqueId = clique.getId();
			serverConcept.setClique(cliqueId);

			Set<String> categories = dbConcept.getTypes();

			if (categories == null) {
				categories = conceptTypeService.getConceptCategoriesByClique(cliqueId).stream().map(c -> c.getName()).collect(Collectors.toSet());
			}
			serverConcept.setCategories(new ArrayList<>(categories));
			serverConcepts.add(serverConcept);
		}

		return serverConcepts;
	}

	@Override
	public Integer getDataCount(QuerySession<ConceptsQueryInterface> query, int beaconId) {
		return conceptRepository.countQueryResults(query.makeQueryString(), beaconId);
	}
}
