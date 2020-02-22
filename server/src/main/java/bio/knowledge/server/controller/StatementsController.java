package bio.knowledge.server.controller;

import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.KnowledgeBeaconImpl;
import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.api.StatementsApi;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.database.repository.aggregator.QueryTrackerRepository;
import bio.knowledge.model.aggregator.neo4j.Neo4jQuery;
import bio.knowledge.model.aggregator.neo4j.Neo4jQueryTracker;
import bio.knowledge.model.neo4j.Neo4jStatement;
import bio.knowledge.server.blackboard.Blackboard;
import bio.knowledge.server.blackboard.CliqueService;
import bio.knowledge.server.blackboard.StatementsDatabaseInterface;
import bio.knowledge.server.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
public class StatementsController {
    private static final int SIZE = 1000;

    @Autowired
    TaskProcessor processor;

    @Autowired
    KnowledgeBeaconRegistry registry;

    @Autowired
    CliqueService cliqueService;

    @Autowired
    QueryTrackerRepository queryTrackerRepository;

    @Autowired
    StatementsDatabaseInterface statementsDatabaseInterface;

    @Autowired
    Blackboard blackboard;

    @Autowired
    StatementRepository statementRepository;

    public ResponseEntity<ServerStatementDetails> getStatementDetails(
            String statementId,
            List<String> keywords,
            Integer pageNumber,
            Integer pageSize
    ) {

        Neo4jStatement statement = statementRepository.findById(statementId);

        ServerStatementDetails result = blackboard.getStatementDetails(statementId, keywords, pageSize, pageNumber);

        if (result == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    public ResponseEntity<ServerStatementsQueryResult> getStatements(
            String queryId,
            List<Integer> beacons,
            Integer pageNumber,
            Integer pageSize
    ) {
        pageNumber = pageNumber != null ? pageNumber : 1;
        pageSize = pageSize != null ? pageSize : 100;

        List<ServerStatement> serverStatements = statementsDatabaseInterface.getDataPage(queryId, beacons, pageNumber, pageSize);

        ServerStatementsQueryResult result = new ServerStatementsQueryResult();

        result.setQueryId(queryId);
        result.setBeacons(beacons);
        result.setPageNumber(pageNumber);
        result.setPageSize(pageSize);
        result.setResults(serverStatements);

        return ResponseEntity.ok(result);
    }

    private ServerStatementsQueryBeaconStatus convert(Neo4jQuery q) {
        ServerStatementsQueryBeaconStatus s = new ServerStatementsQueryBeaconStatus();

        s.setBeacon(q.getBeaconId());
        s.setStatus(q.getStatus());
        s.setDiscovered(q.getDiscovered());
        s.setProcessed(q.getProcessed());
        s.setCount(q.getCount());

        return s;
    }

    public ResponseEntity<ServerStatementsQueryStatus> getStatementsQueryStatus(
            String queryId,
            List<Integer> beacons
    ) {
        Neo4jQueryTracker queryTracker = queryTrackerRepository.find(queryId);

        if (queryTracker == null) {
            return ResponseEntity.notFound().build();
        }

        List<ServerStatementsQueryBeaconStatus> statuses = queryTracker.getQueries().stream()
                .filter(q -> beacons == null || beacons.contains(q.getBeaconId()))
                .map(this::convert)
                .collect(toList());

        ServerStatementsQueryStatus response = new ServerStatementsQueryStatus();

        response.setQueryId(queryId);
        response.setStatus(statuses);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ServerStatementsQuery> postStatementsQuery(
            String source,
            List<String> relations,
            String target,
            List<String> keywords,
            List<String> categories,
            List<Integer> beacons
    ) {
        final String edgeLabel;

        if (relations != null && relations.size() > 1) {
            throw new IllegalStateException("Cannot handle multiple relations at the moment");
        } else if (relations != null && relations.size() == 1) {
            edgeLabel = relations.get(0);
        } else {
            edgeLabel = null;
        }

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

        List<String> sources = source != null ? Collections.singletonList(source) : null;
        List<String> targets = target != null ? Collections.singletonList(target) : null;

        for (KnowledgeBeacon beacon : knowledgeBeacons) {
            ThrowingRunnable task = () -> {
                StatementsApi statementsApi = new StatementsApi();
                statementsApi.setApiClient(((KnowledgeBeaconImpl) beacon).getApiClient());

                try {
                    List<BeaconStatement> statements = statementsApi.getStatements(
                            sources,
                            edgeLabel,
                            null,
                            targets,
                            keywords,
                            categories,
                            SIZE
                    );

                    queryTrackerRepository.updateQueryStatus(
                            queryId,
                            beacon.getId(),
                            HttpStatus.SUCCESS,
                            statements.size(),
                            null,
                            null
                    );

                    statementsDatabaseInterface.loadData(queryId, statements, beacon.getId());
                } catch (ApiException e) {
                    queryTrackerRepository.updateQueryStatus(
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

            processor.add(queryId, task);
        }

        ServerStatementsQuery response = new ServerStatementsQuery();

        response.setQueryId(queryId);
        response.setKeywords(keywords);
        response.setCategories(categories);
        response.setSource(source);
        response.setTarget(target);
        response.setRelations(relations);

        return ResponseEntity.ok(response);
    }
}
