package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerLogEntry;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-11T17:59:49.447Z")

@Api(value = "errorlog", description = "the errorlog API")
public interface ErrorlogApi {

    @ApiOperation(value = "", notes = "Get a log of the system errors associated with a specified query ", response = ServerLogEntry.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with most recent errors ", response = ServerLogEntry.class) })
    @RequestMapping(value = "/errorlog",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerLogEntry>> getErrors( @NotNull @ApiParam(value = "query identifier returned from a POSTed query ", required = true) @RequestParam(value = "queryId", required = true) String queryId);

}
