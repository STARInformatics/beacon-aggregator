package bio.knowledge.server.controller;

import bio.knowledge.server.api.CliquesApi;
import bio.knowledge.server.model.ServerCliquesQuery;
import bio.knowledge.server.model.ServerCliquesQueryResult;
import bio.knowledge.server.model.ServerCliquesQueryStatus;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class CliquesController implements CliquesApi {
    public ResponseEntity<ServerCliquesQueryResult> getCliques(String queryId) {
        return null;
    }

    public ResponseEntity<ServerCliquesQueryStatus> getCliquesQueryStatus(String queryId) {
        return null;
    }

    public ResponseEntity<ServerCliquesQuery> postCliquesQuery(List<String> ids) {
        return null;
    }
}
