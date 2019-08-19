package bio.knowledge.server.api;

import java.util.List;
import java.util.Map;

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

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T21:48:57.324-07:00")

@Api(value = "predicates", description = "the predicates API")
public interface PredicatesApi {

    @ApiOperation(value = "Get supported relationships by source and target", nickname = "predicatesGet", notes = "", response = Map.class, responseContainer = "Map", tags={ "predicates", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Predicates by source and target", response = Map.class, responseContainer = "Map") })
    @RequestMapping(value = "/predicates",
            produces = { "application/json" },
            method = RequestMethod.GET)
    ResponseEntity<Map<String, Map<String,List<String>>>> predicatesGet();

    @ApiOperation(value = "Details of predicates used in statements issued, by the knowledge source", notes = "", response = ServerPredicate.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response with predicates with CURIE and definitions indexed by beacons which support the relation ", response = ServerPredicate.class) })
    @RequestMapping(value = "/predicates/details",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerPredicate>> getPredicatesDetails( @ApiParam(value = "set of aggregator indices of beacons to constrain predicates returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);

}
