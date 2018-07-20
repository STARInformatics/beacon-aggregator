package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerPredicate;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-11T17:59:49.447Z")

@Controller
public class PredicatesApiController implements PredicatesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerPredicate>> getPredicates( @ApiParam(value = "set of aggregator indices of beacons to constrain predicates returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
        return ctrl.getPredicates(beacons);
    }

}
