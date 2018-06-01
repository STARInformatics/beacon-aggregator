package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerCliqueIdentifier;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-01T20:11:14.227Z")

@Api(value = "clique", description = "the clique API")
public interface CliqueApi {

    @ApiOperation(value = "", notes = "Retrieves the beacon aggregator assigned cliques of equivalent concepts that includes the specified (url-encoded) CURIE identifiers. Note that the clique to which a given concept CURIE belongs may change over time as the aggregator progressively discovers the members of the clique. ", response = ServerCliqueIdentifier.class, responseContainer = "List", tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with clique identifiers returned ", response = ServerCliqueIdentifier.class) })
    @RequestMapping(value = "/clique/{identifiers}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerCliqueIdentifier>> getClique(@ApiParam(value = "a list of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of interest to be resolved to a list of concept clique",required=true ) @PathVariable("identifiers") List<String> identifiers);

}
