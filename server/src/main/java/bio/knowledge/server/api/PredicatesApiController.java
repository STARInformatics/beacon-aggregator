package bio.knowledge.server.api;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerPredicate;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-20T20:17:56.260Z[GMT]")
@Controller
public class PredicatesApiController implements PredicatesApi {

    private final PredicatesApiDelegate delegate;

    @org.springframework.beans.factory.annotation.Autowired
    public PredicatesApiController(PredicatesApiDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public PredicatesApiDelegate getDelegate() {
        return delegate;
    }

    @Autowired
    ControllerImpl ctrl;

    public ResponseEntity<List<ServerPredicate>> getPredicatesDetails(@ApiParam(value = "set of aggregator indices of beacons to constrain predicates returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
        return ctrl.getPredicatesDetails(beacons);
    }
}
