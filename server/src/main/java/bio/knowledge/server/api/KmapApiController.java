package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.ServerKnowledgeMap;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-24T18:37:28.321-08:00")

@Controller
public class KmapApiController implements KmapApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerKnowledgeMap>> getKnowledgeMap( @ApiParam(value = "set of aggregator indices of beacons to constrain knowledge sources accessed by the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
         @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId) {
         return ctrl.getKnowledgeMap(beacons, sessionId);
    }

}
