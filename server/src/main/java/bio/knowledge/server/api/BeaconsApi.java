package bio.knowledge.server.api;

import bio.knowledge.server.model.Beacon;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-08T10:59:06.986-07:00")

@Api(value = "beacons", description = "the beacons API")
public interface BeaconsApi {

    @ApiOperation(value = "", notes = "Get a list of the knowledge beacons that the aggregator can query ", response = Beacon.class, responseContainer = "List", tags={ "aggregator", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with beacons ", response = Beacon.class) })
    @RequestMapping(value = "/beacons",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<Beacon>> getBeacons( @ApiParam(value = "identifier to be used for tagging session data ") @RequestParam(value = "sessionId", required = false) String sessionId);

}
