package bio.knowledge.server.blackboard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CliqueMap {
    private ConcurrentHashMap<String, Set<String>> cliqueMap = new ConcurrentHashMap<>();

    public Set<String> get(String curie) {
        if (cliqueMap.containsKey(curie)) {
            return Collections.unmodifiableSet(cliqueMap.get(curie));
        } else {
            return Collections.emptySet();
        }
    }

    public boolean contains(String curie) {
        return cliqueMap.containsKey(curie);
    }

    public Set<String> merge(Collection<String> curies) {
        return merge(curies.toArray(new String[0]));
    }

    /**
     * Creates a new clique by merging the cliques of the given curies together,
     * and updates the curie-to-clique mapping.
     *
     * This method should be the singular
     *
     * @param curies
     * @return
     */
    public Set<String> merge(String... curies) {
        if (curies.length == 0) {
            return Collections.emptySet();
        }

        synchronized (cliqueMap) {
            Set<String> clique = new HashSet<>();

            List<String> cliqueLeaders = new ArrayList<>();

            for (String curie : curies) {
                clique.add(curie);

                Set<String> previousClique = cliqueMap.getOrDefault(curie, Collections.emptySet());

                clique.addAll(previousClique);
            }

            for (String curie : clique) {
                cliqueMap.put(curie, clique);
            }

            return Collections.unmodifiableSet(clique);
        }
    }

    // TODO: Use this class to track the clique leader
    private static class Clique extends HashSet<String> {
        private String cliqueLeader;

        public void setCliqueLeader(String cliqueLeader) {
            this.cliqueLeader = Objects.requireNonNull(cliqueLeader);
            this.add(cliqueLeader);
        }

        public String getCliqueLeader() {
            return this.cliqueLeader;
        }
    }
}
