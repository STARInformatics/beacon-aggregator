package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.ServerConceptCategories;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-02T12:58:15.341-07:00")

@Api(value = "types", description = "the types API")
public interface TypesApi {

    @ApiOperation(value = "", notes = "Get a list of types and number of instances in the knowledge source, and a link to the API call for the list of equivalent terminology ", response = ServerConceptCategories.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with types and frequency returned ", response = ServerConceptCategories.class) })
    @RequestMapping(value = "/types",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerConceptCategories>> getConceptTypes( @ApiParam(value = "set of aggregator indices of beacons to constrain types returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);

}
