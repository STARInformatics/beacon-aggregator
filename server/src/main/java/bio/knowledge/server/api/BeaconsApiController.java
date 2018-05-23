package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerKnowledgeBeacon;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T15:02:51.082-07:00")

@Controller
public class BeaconsApiController implements BeaconsApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons() {
         return ctrl.getBeacons();
    }

}
