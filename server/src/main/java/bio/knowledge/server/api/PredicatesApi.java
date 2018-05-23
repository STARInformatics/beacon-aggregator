package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.ServerPredicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T15:02:51.082-07:00")

@Api(value = "predicates", description = "the predicates API")
public interface PredicatesApi {

    @ApiOperation(value = "", notes = "Get a list of predicates used in statements issued by the knowledge source ", response = ServerPredicate.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with predicates with CURIE and definitions indexed by beacons which support the relation ", response = ServerPredicate.class) })
    @RequestMapping(value = "/predicates",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerPredicate>> getPredicates( @ApiParam(value = "set of aggregator indices of beacons to constrain predicates returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);

}
