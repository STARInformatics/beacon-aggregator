package bio.knowledge.server.api;

import bio.knowledge.server.model.Summary;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-26T14:52:59.489-07:00")

@Api(value = "types", description = "the types API")
public interface TypesApi {

    @ApiOperation(value = "", notes = "Get a list of types and # of instances in the knowledge source, and a link to the API call for the list of equivalent terminology ", response = Summary.class, responseContainer = "List", tags={ "summary", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with types and frequency returned ", response = Summary.class) })
    @RequestMapping(value = "/types",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<Summary>> linkedTypes( @ApiParam(value = "set of IDs of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
         @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId);

}
