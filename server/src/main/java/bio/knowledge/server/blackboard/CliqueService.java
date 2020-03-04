package bio.knowledge.server.blackboard;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import bio.knowledge.client.ApiClient;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.ApiResponse;
import bio.knowledge.client.Pair;
import bio.knowledge.client.model.BeaconConcept;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import org.springframework.stereotype.Service;

@Service
public class CliqueService {
	private static final String PATH = "https://api.monarchinitiative.org/api/search/entity/";
	private static final String ROW = "?rows=1";

	CliqueMap cliqueMap = new CliqueMap();

	public Set<String> getClique(String curie) {
		if (cliqueMap.contains(curie)) {
			return cliqueMap.get(curie);
		} else {
			Map<String, Object> data = queryBiolink(curie);

			Set<String> clique = parseBiolinkResults(data);

			clique.add(curie);

			return cliqueMap.merge(clique);
		}
	}

	private Set<String> parseBiolinkResults(Map<String, Object> results) {
		List<Map<String, Object>> docs = (List<Map<String, Object>>) results.get("docs");

		if (docs.isEmpty()) {
			return Collections.emptySet();
		}

		Map<String, Object> first = docs.get(0);

		String cliqueLeader = (String) first.get("id");

		List<String> equivalentCuries = (List<String>) first.get("equivalent_curie");

		Set<String> clique = new HashSet<>(equivalentCuries);

		clique.add(cliqueLeader);

		clique.addAll(equivalentCuries);

		return clique;
	}

	private Map<String, Object> queryBiolink(String curie) {
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(PATH);

		Type type = new TypeToken<HashMap<String, Object>>(){}.getType();

		try {
			Call call = apiClient.buildCall(
					curie,
					"GET",
					Collections.singletonList(new Pair("rows", "1")),
					null,
					new HashMap<>(),
					new HashMap<>(),
					new String[0],
					null
			);

			ApiResponse<HashMap<String, Object>> response = apiClient.execute(call, type);

			if (response.getStatusCode() != 200) {
				throw new RuntimeException("Failure for exact matches call: " + call);
			}

			return response.getData();
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
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
