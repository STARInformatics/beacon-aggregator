package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.KnowledgeBeacon;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-10T12:47:04.653-07:00")

@Api(value = "beacons", description = "the beacons API")
public interface BeaconsApi {

    @ApiOperation(value = "", notes = "Get a list of the knowledge beacons that the aggregator can query ", response = KnowledgeBeacon.class, responseContainer = "List", tags={ "aggregator", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with beacons ", response = KnowledgeBeacon.class) })
    @RequestMapping(value = "/beacons",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<KnowledgeBeacon>> getBeacons( @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId);

}
