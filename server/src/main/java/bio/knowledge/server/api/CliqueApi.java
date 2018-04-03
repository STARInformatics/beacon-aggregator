package bio.knowledge.server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bio.knowledge.server.model.ServerCliqueIdentifier;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-02T12:58:15.341-07:00")

@Api(value = "clique", description = "the clique API")
public interface CliqueApi {

    @ApiOperation(value = "", notes = "Retrieves the beacon aggregator assigned clique of equivalent concepts that includes the specified (url-encoded) CURIE identifier. Note that the clique to which a given concept CURIE belongs may change over time as the aggregator progressively discovers the members of the clique. ", response = ServerCliqueIdentifier.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with clique identifier returned ", response = ServerCliqueIdentifier.class) })
    @RequestMapping(value = "/clique/{identifier}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ServerCliqueIdentifier> getClique(@ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of interest to be resolved to a concept clique",required=true ) @PathVariable("identifier") String identifier);

}
