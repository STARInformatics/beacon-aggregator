package bio.knowledge.server.blackboard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CliqueMap {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private final ConcurrentHashMap<String, Clique> cliqueMap = new ConcurrentHashMap<>();

    public Clique get(String curie) {
        readWriteLock.readLock().lock();
        try {
            return cliqueMap.get(curie);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public boolean contains(String curie) {
        readWriteLock.readLock().lock();
        try {
            return cliqueMap.containsKey(curie);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public Clique merge(Collection<String> curies) {
        return merge(curies.toArray(new String[0]));
    }

    /**
     * Creates a new clique by merging the cliques of the given curies together,
     * and updates the curie-to-clique mapping.
     *
     * @param curies
     * @return
     */
    public Clique merge(String... curies) {
        if (curies.length == 0) {
            return null;
        }

        readWriteLock.writeLock().lock();

        try {
            Clique clique = new Clique();

            for (String curie : curies) {
                clique.inner.add(curie);

                Clique previousClique = cliqueMap.get(curie);

                if (previousClique != null) {
                    clique.inner.addAll(previousClique.inner);
                }
            }

            for (String curie : clique) {
                cliqueMap.put(curie, clique);
            }

            return clique;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * An immutable set of strings that represents a clique.
     */
    public final static class Clique implements Set<String> {
        private Clique() { }

        private Set<String> inner = new HashSet<>();

        private String cliqueLeader;

        public void setCliqueLeader(String cliqueLeader) {
            if (!contains(cliqueLeader)) {
                throw new IllegalStateException("Clique must contain its clique leader [curie="+cliqueLeader+"]");
            }

            this.cliqueLeader = cliqueLeader;
        }

        public String getCliqueLeader() {
            return this.cliqueLeader;
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean isEmpty() {
            return inner.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return inner.contains(o);
        }

        @Override
        public Iterator<String> iterator() {
            return inner.iterator();
        }

        @Override
        public Object[] toArray() {
            return inner.toArray();
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return inner.toArray(ts);
        }

        @Override
        public boolean add(String s) {
            throw new UnsupportedOperationException("Cliques are immutable");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Cliques are immutable");
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return inner.containsAll(collection);
        }

        @Override
        public boolean addAll(Collection<? extends String> collection) {
            throw new UnsupportedOperationException("Cliques are immutable");
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException("Cliques are immutable");
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException("Cliques are immutable");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Cliques are immutable");
        }
    }
}
