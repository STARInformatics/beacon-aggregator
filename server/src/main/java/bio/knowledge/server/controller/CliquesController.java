package bio.knowledge.server.controller;

import bio.knowledge.client.ApiException;
import bio.knowledge.database.repository.aggregator.QueryTrackerRepository;
import bio.knowledge.model.aggregator.neo4j.Neo4jQuery;
import bio.knowledge.model.aggregator.neo4j.Neo4jQueryTracker;
import bio.knowledge.server.blackboard.CliqueMap;
import bio.knowledge.server.blackboard.CliqueService;
import bio.knowledge.server.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class CliquesController {
    /**
     * We no longer use beacons to build up cliques, but the clique architecture is intertwined with the beacon index.
     * So until that underlying architecture is cleaned up, we will act as if all cliques come from the first beacon.
     */
    private static final int BEACON_ID = 0;

    /**
     * We are not using the database to track which cliques have resulted from which clique queries. Instead, we will
     * just keep track of this in memory.
     */
    private final ConcurrentHashMap<String, Set<String>> queryIdToCurieMap = new ConcurrentHashMap<>();

    private final QueryTrackerRepository queryTrackerRepository;
    private final TaskProcessor processor;
    private final CliqueService cliqueService;

    @Autowired
    public CliquesController(
            QueryTrackerRepository queryTrackerRepository,
            TaskProcessor processor,
            CliqueService cliqueService
    ) {
        this.queryTrackerRepository = queryTrackerRepository;
        this.processor = processor;
        this.cliqueService = cliqueService;
    }

    public ResponseEntity<ServerCliquesQueryResult> getCliques(String queryId) {
        Neo4jQueryTracker query = queryTrackerRepository.find(queryId);

        if (query == null) {
            return ResponseEntity.notFound().build();
        }

        ServerCliquesQueryResult result = new ServerCliquesQueryResult();

        result.setQueryId(queryId);

        for (String curie : queryIdToCurieMap.get(queryId)) {
            ServerClique serverClique = new ServerClique();

            serverClique.setId(curie);

            try {
                CliqueMap.Clique clique = cliqueService.getClique(curie);
                serverClique.setCliqueId(clique.getCliqueLeader());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

            result.addResultsItem(serverClique);
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<ServerCliquesQueryStatus> getCliquesQueryStatus(String queryId) {
        Neo4jQueryTracker queryTracker = queryTrackerRepository.find(queryId);

        if (queryTracker == null) {
            return ResponseEntity.notFound().build();
        }

        ServerCliquesQueryStatus response = new ServerCliquesQueryStatus();

        response.setQueryId(queryId);

        for (Neo4jQuery query : queryTracker.getQueries()) {
            ServerCliquesQueryBeaconStatus statusItem = new ServerCliquesQueryBeaconStatus();

            statusItem.setStatus(query.getStatus());
            statusItem.setBeacon(query.getBeaconId());
            statusItem.setCount(query.getCount());
            statusItem.setDiscovered(query.getDiscovered());
            statusItem.setProcessed(query.getProcessed());

            response.addStatusItem(statusItem);
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ServerCliquesQuery> postCliquesQuery(List<String> ids) {
        String queryId = RandomStringUtils.randomAlphanumeric(20);

        Neo4jQueryTracker queryTracker = new Neo4jQueryTracker();

        queryTracker.setQueryString(queryId);
        queryTracker.setQueries(new HashSet<>());

        Neo4jQuery q = new Neo4jQuery();

        q.setBeaconId(BEACON_ID);
        q.setStatus(HttpStatus.QUERY_IN_PROGRESS);

        queryTracker.getQueries().add(q);

        queryTrackerRepository.save(queryTracker, 2);

        queryIdToCurieMap.put(queryId, ConcurrentHashMap.newKeySet());

        ThrowingRunnable runnable = () -> {
            int total = 0;

            for (String curie : ids) {
                try {
                    Set<String> clique = cliqueService.getClique(curie);

                    total += clique.size();

                    queryIdToCurieMap.get(queryId).add(curie);

                } catch (ApiException e) {
                    queryTrackerRepository.updateQueryStatusState(
                            queryId,
                            BEACON_ID,
                            HttpStatus.SERVER_ERROR,
                            null,
                            null,
                            null
                    );

                    throw e;
                }

            }

            queryTrackerRepository.updateQueryStatusState(
                    queryId,
                    BEACON_ID,
                    HttpStatus.SUCCESS,
                    total,
                    total,
                    total
            );
        };

        processor.run(queryId, runnable);

        ServerCliquesQuery response = new ServerCliquesQuery();

        response.setIds(ids);
        response.setQueryId(queryId);

        return ResponseEntity.ok(response);
    }
}
