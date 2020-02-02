package bio.knowledge.server.blackboard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class CliqueService {
	private ConcurrentHashMap<String, Set<String>> cliqueMap = new ConcurrentHashMap<>();

	public Set<String> getClique(String curie) {
		if (cliqueMap.contains(curie)) {
			return Collections.unmodifiableSet(cliqueMap.get(curie));
		} else {
			return Collections.emptySet();
		}
	}

	public boolean hasClique(String curie) {
		return cliqueMap.contains(curie);
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

			for (String curie : curies) {
				clique.add(curie);
				clique.addAll(cliqueMap.getOrDefault(curie, Collections.emptySet()));
			}

			for (String curie : clique) {
				cliqueMap.put(curie, clique);
			}

			return Collections.unmodifiableSet(clique);
		}
	}

	/**
	 * Creates a clique of one if no clique yet exists, otherwise returns that
	 * clique.
	 * 
	 * @param curie
	 * @return
	 */
	public Set<String> createClique(String curie) {
		return merge(curie);
	}

	/**
	 * Chooses the CURIE to use as cliqueId.
	 * 
	 * TODO: In the future implement some more complicated comparator to better
	 * select the best CURIE to represent the clique.
	 * 
	 * @param clique
	 * @return
	 */
	public static String getCliqueId(Set<String> clique) {
		return clique.stream().sorted().findFirst()
				.orElseThrow(() -> new IllegalStateException("Cannot extract clique leader from an empty clique"));
	}
}
