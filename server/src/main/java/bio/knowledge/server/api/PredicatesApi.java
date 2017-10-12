package bio.knowledge.server.api;

import bio.knowledge.server.model.InlineResponse200;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-11T17:55:45.568-07:00")

@Api(value = "predicates", description = "the predicates API")
public interface PredicatesApi {

    @ApiOperation(value = "", notes = "Get a list of predicates used in statements issued by the knowledge source ", response = InlineResponse200.class, responseContainer = "List", tags={ "predicates", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with types and frequency returned ", response = InlineResponse200.class) })
    @RequestMapping(value = "/predicates",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<InlineResponse200>> getPredicates( @ApiParam(value = "set of IDs of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
         @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId);

}
