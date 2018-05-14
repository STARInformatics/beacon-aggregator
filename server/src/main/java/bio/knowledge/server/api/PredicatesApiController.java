package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.ServerPredicates;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T14:44:00.373-07:00")

@Controller
public class PredicatesApiController implements PredicatesApi {



    public ResponseEntity<List<ServerPredicates>> getPredicates( @ApiParam(value = "set of aggregator indices of beacons to constrain predicates returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
        // do some magic!
        return new ResponseEntity<List<ServerPredicates>>(HttpStatus.OK);
    }

}
