package bio.knowledge.server.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerPredicate;
import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpServletRequest;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-12T21:13:13.403Z[GMT]")

@Controller
public class PredicatesApiController implements PredicatesApi {

    private static final Logger logger = LoggerFactory.getLogger(PredicatesApiController.class);

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public PredicatesApiController(HttpServletRequest request) {
        this.request = request;
    }

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<Map<String, Map<String, List<String>>>> predicatesGet() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return ctrl.getPredicates();
        }

        return new ResponseEntity<Map<String, Map<String,List<String>>>>(HttpStatus.NOT_IMPLEMENTED);
    }
    public ResponseEntity<List<ServerPredicate>> getPredicatesDetails( @ApiParam(value = "set of aggregator indices of beacons to constrain predicates returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
         return ctrl.getPredicatesDetails(beacons);
    }

}
