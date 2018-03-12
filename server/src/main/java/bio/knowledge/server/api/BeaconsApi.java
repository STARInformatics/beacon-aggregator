package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerKnowledgeBeacon;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-12T16:26:47.889-07:00")

@Api(value = "beacons", description = "the beacons API")
public interface BeaconsApi {

    @ApiOperation(value = "", notes = "Get a list of all of the knowledge beacons that the aggregator can query ", response = ServerKnowledgeBeacon.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with beacons ", response = ServerKnowledgeBeacon.class) })
    @RequestMapping(value = "/beacons",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons();

}
