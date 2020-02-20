package bio.knowledge.server.controller;

import bio.knowledge.aggregator.*;
import bio.knowledge.aggregator.ontology.Ontology;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.api.StatementsApi;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconStatementObject;
import bio.knowledge.client.model.BeaconStatementPredicate;
import bio.knowledge.client.model.BeaconStatementSubject;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.PredicateRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.database.repository.TkgNodeRepository;
import bio.knowledge.database.repository.aggregator.BeaconCitationRepository;
import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.database.repository.aggregator.QueryTrackerRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.SimpleConcept;
import bio.knowledge.model.aggregator.neo4j.*;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;
import bio.knowledge.model.neo4j.Neo4jPredicate;
import bio.knowledge.model.neo4j.Neo4jStatement;
import bio.knowledge.server.blackboard.Blackboard;
import bio.knowledge.server.blackboard.CliqueService;
import bio.knowledge.server.blackboard.StatementsDatabaseInterface;
import bio.knowledge.server.model.*;
import bio.knowledge.server.tkg.TKG;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Controller
public class StatementsController {
    private static final int SIZE = 100;

    private final ConcurrentHashMap<String, ServerStatementsQueryStatus> statuses = new ConcurrentHashMap<>();

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

    private final Object mutex = new Object();

    public ResponseEntity<ServerStatementDetails> getStatementDetails(
            String statementId,
            List<String> keywords,
            Integer pageNumber,
            Integer pageSize
    ) {
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
        List<String> sources = source != null ? Collections.singletonList(source) : null;
        List<String> targets = target != null ? Collections.singletonList(target) : null;
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

        ControllerUtil.createQuery(queryTrackerRepository, queryId, knowledgeBeacons);

        // TODO: Is this required?
        setupStatusRecord(knowledgeBeacons, queryId);

        for (KnowledgeBeacon beacon : knowledgeBeacons) {
            ThrowingRunnable task = () -> {
                StatementsApi statementsApi = new StatementsApi();
                statementsApi.setApiClient(((KnowledgeBeaconImpl) beacon).getApiClient());

                List<BeaconStatement> statements;

                try {
                    statements = statementsApi.getStatements(
                            sources,
                            edgeLabel,
                            null,
                            targets,
                            keywords,
                            categories,
                            SIZE
                    );
                } catch (ApiException e) {
                    // TODO: just have a method that directly sets the status?
                    synchronized (mutex) {
                        Neo4jQueryTracker tracker = queryTrackerRepository.find(queryId);
                        Neo4jQuery query = tracker.getQuery(beacon.getId())
                                .orElseThrow(() -> new IllegalStateException("Cannot find query [queryId=" + queryId + "]"));
                        query.setStatus(HttpStatus.SERVER_ERROR);
                        queryTrackerRepository.save(tracker);
                    }

                    ServerStatementsQueryBeaconStatus status = getStatus(queryId, beacon.getId());
                    status.setStatus(HttpStatus.SERVER_ERROR);
                    throw e;
                }

//                loadData(null, statements, beacon.getId());

                for (BeaconStatement statement : statements) {
                    Set<String> objectClique = cliqueService.getClique(statement.getObject().getId());
                    Set<String> subjectClique = cliqueService.getClique(statement.getSubject().getId());
                }

                throw new RuntimeException("LOAD THE DATA WAS COMMENTED OUT!");
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

    private void setupStatusRecord(List<KnowledgeBeacon> knowledgeBeacons, String queryId) {
        ServerStatementsQueryStatus queryStatus = new ServerStatementsQueryStatus();
        queryStatus.setQueryId(queryId);

        knowledgeBeacons.forEach(b -> {
            ServerStatementsQueryBeaconStatus status = new ServerStatementsQueryBeaconStatus();

            status.setBeacon(b.getId());
            status.setStatus(HttpStatus.QUERY_IN_PROGRESS);

            queryStatus.addStatusItem(status);
        });

        statuses.put(queryId, queryStatus);
    }

    private ServerStatementsQueryBeaconStatus getStatus(String queryId, int beaconId) {
        return statuses.get(queryId).getStatus().stream()
                .filter(s -> s.getBeacon() == beaconId)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Cannot find status [queryId=" + queryId + ", beaconId=" + beaconId));
    }
}
