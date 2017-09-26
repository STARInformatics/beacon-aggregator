package bio.knowledge.server.api;

import bio.knowledge.server.model.LogEntry;

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

@Api(value = "errorlog", description = "the errorlog API")
public interface ErrorlogApi {

    @ApiOperation(value = "", notes = "Get a log of the most recent errors in this session ", response = LogEntry.class, responseContainer = "List", tags={ "aggregator", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with most recent errors ", response = LogEntry.class) })
    @RequestMapping(value = "/errorlog",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<LogEntry>> getErrors( @NotNull @ApiParam(value = "client-defined session identifier ", required = true) @RequestParam(value = "sessionId", required = true) String sessionId);

}
