package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-10T12:47:04.653-07:00")

@Controller
public class BeaconsApiController implements BeaconsApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons( @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId) {
        return ctrl.getBeacons();
    }

}
