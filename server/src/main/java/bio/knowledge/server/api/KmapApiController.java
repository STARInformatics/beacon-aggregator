package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerKnowledgeMap;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-02T12:58:15.341-07:00")

@Controller
public class KmapApiController implements KmapApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerKnowledgeMap>> getKnowledgeMap( @ApiParam(value = "set of aggregator indices of beacons constraining knowledge maps returned  ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
         return ctrl.getKnowledgeMap(beacons);
    }

}
