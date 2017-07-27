package bio.knowledge.server.api;

import bio.knowledge.server.model.InlineResponse2005;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-27T15:18:21.292-07:00")

@Api(value = "sources", description = "the sources API")
public interface SourcesApi {

    @ApiOperation(value = "", notes = "Get a list of the knowledge beacons that the aggregator can query ", response = InlineResponse2005.class, responseContainer = "List", tags={ "aggregator", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with beacons ", response = InlineResponse2005.class) })
    @RequestMapping(value = "/sources",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<InlineResponse2005>> getSources();

}
