package bio.knowledge.server.controller;

import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.api.ConceptsApi;
import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.database.repository.aggregator.QueryTrackerRepository;
import bio.knowledge.model.aggregator.neo4j.Neo4jQuery;
import bio.knowledge.model.aggregator.neo4j.Neo4jQueryTracker;
import bio.knowledge.server.blackboard.Blackboard;
import bio.knowledge.server.blackboard.BlackboardException;
import bio.knowledge.server.blackboard.ConceptsDatabaseInterface;
import bio.knowledge.server.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
public class ConceptsController extends ConceptsApi {
    private static final Logger logger = LoggerFactory.getLogger(ConceptsController.class);

    @Autowired
    TaskProcessor processor;

    @Autowired
    KnowledgeBeaconRegistry registry;

    @Autowired
    QueryTrackerRepository queryTrackerRepository;

    @Autowired
    ConceptsDatabaseInterface conceptsDatabaseInterface;

    @Autowired
    Blackboard blackboard;

    public ResponseEntity<ServerConceptWithDetails> getConceptDetails(String cliqueId, List<Integer> beacons) throws BlackboardException {
        ServerConceptWithDetails result = blackboard.getConceptDetails(cliqueId, beacons);

        return ResponseEntity.ok(result);
    }
    public ResponseEntity<ServerConceptsQueryResult> getConcepts(
            String queryId,
            List<Integer> beacons,
            Integer pageNumber,
            Integer pageSize
    ) {
        List<ServerConcept> serverConcepts = conceptsDatabaseInterface.getDataPage(queryId, beacons, pageNumber, pageSize);

        ServerConceptsQueryResult result = new ServerConceptsQueryResult();
        result.setQueryId(queryId);
        result.setPageNumber(pageNumber);
        result.setPageSize(pageSize);
        result.setBeacons(beacons);
        result.setResults(serverConcepts);

        return ResponseEntity.ok(result);
    }

    private ServerConceptsQueryBeaconStatus convert(Neo4jQuery q) {
        ServerConceptsQueryBeaconStatus status = new ServerConceptsQueryBeaconStatus();

        status.setBeacon(q.getBeaconId());
        status.setCount(q.getCount());
        status.setDiscovered(q.getDiscovered());
        status.setProcessed(q.getProcessed());
        status.setStatus(q.getStatus());

        return status;
    }

    public ResponseEntity<ServerConceptsQueryStatus> getConceptsQueryStatus(String queryId, List<Integer> beacons) {
        Neo4jQueryTracker queryTracker = queryTrackerRepository.find(queryId);

        if (queryTracker == null) {
            return ResponseEntity.notFound().build();
        }

        List<ServerConceptsQueryBeaconStatus> statuses = queryTracker.getQueries().stream()
                .filter(q -> beacons == null || beacons.contains(q.getBeaconId()))
                .map(this::convert)
                .collect(toList());

        ServerConceptsQueryStatus response = new ServerConceptsQueryStatus();

        response.setQueryId(queryId);
        response.setStatus(statuses);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ServerConceptsQuery> postConceptsQuery(
            List<String> keywords,
            List<String> categories,
            List<Integer> beacons
    ) {
        List<KnowledgeBeacon> knowledgeBeacons = ControllerUtil.filter(registry.getKnowledgeBeacons(), beacons);

        String queryId = RandomStringUtils.randomAlphanumeric(20);

        Neo4jQueryTracker queryTracker = new Neo4jQueryTracker();

        queryTracker.setQueryString(queryId);
        queryTracker.setQueries(new HashSet<>());

        for (KnowledgeBeacon beacon : knowledgeBeacons) {
            Neo4jQuery q = new Neo4jQuery();
            q.setBeaconId(beacon.getId());
            q.setStatus(HttpStatus.QUERY_IN_PROGRESS);

            queryTracker.getQueries().add(q);
        }

        queryTrackerRepository.save(queryTracker, 2);

        for (KnowledgeBeacon beacon : registry.getKnowledgeBeacons()) {
            ThrowingRunnable runnable = () -> {
                ConceptsApi conceptsApi = new ConceptsApi();
                conceptsApi.setApiClient(((KnowledgeBeaconImpl) beacon).getApiClient());

                try {
                    List<BeaconConcept> concepts = conceptsApi.getConcepts(keywords, categories, 1000);

                    queryTrackerRepository.updateQueryStatusState(
                            queryId,
                            beacon.getId(),
                            HttpStatus.SUCCESS,
                            concepts.size(),
                            null,
                            null
                    );

                    queryTrackerRepository.updateQueryStatusState(
                            queryId,
                            beacon.getId(),
                            HttpStatus.SUCCESS,
                            concepts.size(),
                            null,
                            null
                    );

                    conceptsDatabaseInterface.loadData(queryId, concepts, beacon.getId());
                } catch (ApiException e) {
                    queryTrackerRepository.updateQueryStatusState(
                            queryId,
                            beacon.getId(),
                            HttpStatus.SERVER_ERROR,
                            null,
                            null,
                            null
                    );

                    throw e;
                }
            };

            processor.run(queryId, runnable);
        }

        ServerConceptsQuery serverConceptsQuery = new ServerConceptsQuery();

        serverConceptsQuery.setCategories(categories);
        serverConceptsQuery.setKeywords(keywords);
        serverConceptsQuery.setQueryId(queryId);

        return ResponseEntity.ok(serverConceptsQuery);
    }
}
