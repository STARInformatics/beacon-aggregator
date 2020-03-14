package bio.knowledge.server.blackboard;

import bio.knowledge.client.ApiClient;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.ApiResponse;
import bio.knowledge.client.Pair;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.*;

@Service
public class CliqueService {
    private static final String PATH = "https://api.monarchinitiative.org/api/search/entity/";
    private static final String ROW = "?rows=1";

    CliqueMap cliqueMap = new CliqueMap();

    public CliqueMap.Clique getClique(@NotNull String curie) throws ApiException {
        curie = Objects.requireNonNull(curie);

        if (cliqueMap.contains(curie)) {
            return cliqueMap.get(curie);
        } else {
            return discoverClique(curie);
        }
    }

    private CliqueMap.Clique discoverClique(String sourceCurie) throws ApiException {
        Map<String, Object> data = queryBiolink(sourceCurie);

        List<Map<String, Object>> docs = (List<Map<String, Object>>) data.get("docs");

        if (docs.isEmpty()) {
            return cliqueMap.merge(sourceCurie);
        }

        Map<String, Object> first = docs.get(0);

        String cliqueLeader = (String) first.get("id");

        List<String> equivalentCuries = (List<String>) first.get("equivalent_curie");

        Set<String> curies = new HashSet<>();

        curies.add(sourceCurie);

        if (cliqueLeader != null) {
            curies.add(cliqueLeader);
        }

        if (equivalentCuries != null) {
            curies.addAll(equivalentCuries);
        }

        CliqueMap.Clique clique = cliqueMap.merge(curies);

        clique.setCliqueLeader(cliqueLeader);

        return clique;
    }

    private Map<String, Object> queryBiolink(String curie) throws ApiException {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(PATH);

        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();

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
    }
}
