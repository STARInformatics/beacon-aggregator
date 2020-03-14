package bio.knowledge.server.controller;

import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.database.repository.aggregator.QueryTrackerRepository;
import bio.knowledge.model.aggregator.neo4j.Neo4jQuery;
import bio.knowledge.model.aggregator.neo4j.Neo4jQueryTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ControllerUtil {
    public static List<KnowledgeBeacon> filter(List<KnowledgeBeacon> beacons, List<Integer> filter) {
        if (filter != null) {
            return beacons.stream().filter(b -> filter.contains(b.getId())).collect(toList());
        } else {
            return beacons;
        }
    }

    public static void createQuery(QueryTrackerRepository repo, String queryId, List<KnowledgeBeacon> beacons) {
        List<Integer> beaconIds = beacons.stream().map(KnowledgeBeacon::getId).collect(toList());

        beaconIds.forEach(Objects::requireNonNull);

        Set<Neo4jQuery> queries = beaconIds.stream()
                .map(i -> new Neo4jQuery() {{ setBeaconId(i); }})
                .collect(Collectors.toSet());

        Neo4jQueryTracker queryTracker = new Neo4jQueryTracker();

        queryTracker.setQueryString(queryId);
        queryTracker.setBeaconsHarvested(beaconIds);
        queryTracker.setQueries(queries);

        repo.save(queryTracker, 5);
    }

    private static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> List<List<T>> partition(List<T> list, int maxPartitionSize) {
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<T>> partitions = new ArrayList<>();

        partitions.add(new ArrayList<>());

        for (T t : list) {
            if (last(partitions).size() >= maxPartitionSize) {
                partitions.add(new ArrayList<>());
            }

            last(partitions).add(t);
        }

        return partitions;
    }
}
