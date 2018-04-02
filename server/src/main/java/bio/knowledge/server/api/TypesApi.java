package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerConceptTypes;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-02T11:38:45.201-07:00")

@Api(value = "types", description = "the types API")
public interface TypesApi {

    @ApiOperation(value = "", notes = "Get a list of types and number of instances in the knowledge source, and a link to the API call for the list of equivalent terminology ", response = ServerConceptTypes.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with types and frequency returned ", response = ServerConceptTypes.class) })
    @RequestMapping(value = "/types",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerConceptTypes>> getConceptTypes( @ApiParam(value = "set of aggregator indices of beacons to constrain types returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);

}
