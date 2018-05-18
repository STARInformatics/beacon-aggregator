package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.ServerLogEntry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-18T08:22:36.281-07:00")

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
