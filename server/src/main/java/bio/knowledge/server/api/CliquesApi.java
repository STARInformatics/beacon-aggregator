package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.ServerCliquesQuery;
import bio.knowledge.server.model.ServerCliquesQueryResult;
import bio.knowledge.server.model.ServerCliquesQueryStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T16:36:52.027-07:00")

@Api(value = "cliques", description = "the cliques API")
public interface CliquesApi {

    @ApiOperation(value = "", notes = "Retrieves a list of concept cliques based on  'data ready' from a previously /cliques posted query parameter submission ", response = ServerCliquesQueryResult.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with clique identifiers returned ", response = ServerCliquesQueryResult.class) })
    @RequestMapping(value = "/cliques/data/{queryId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ServerCliquesQueryResult> getCliques(@ApiParam(value = "the query identifier of a concepts query previously posted by the /cliques endpoint",required=true ) @PathVariable("queryId") String queryId);


    @ApiOperation(value = "", notes = "Retrieves the status of a given query about the cliques in the system ", response = ServerCliquesQueryStatus.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Current query status returned ", response = ServerCliquesQueryStatus.class) })
    @RequestMapping(value = "/cliques/status/{queryId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ServerCliquesQueryStatus> getCliquesQueryStatus(@ApiParam(value = "an active query identifier as returned by a POST of clique query parameters.",required=true ) @PathVariable("queryId") String queryId);


    @ApiOperation(value = "", notes = "Retrieves the beacon aggregator assigned cliques of equivalent concepts that includes the specified (url-encoded) CURIE identifiers. Note that the clique to which a given concept CURIE belongs may change over time as the aggregator progressively discovers the members of the clique. Any unmatched identifiers will be ignored (e.g. the id couldn't be found in any of the beacons)  ", response = ServerCliquesQuery.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful clique query initialization, with initial query status returned ", response = ServerCliquesQuery.class) })
    @RequestMapping(value = "/cliques",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<ServerCliquesQuery> postCliquesQuery( @NotNull @ApiParam(value = "an array of [CURIE-encoded](https://www.w3.org/TR/curie/)  identifiers of interest to be resolved to a list of concept cliques", required = true) @RequestParam(value = "ids", required = true) List<String> ids);

}
