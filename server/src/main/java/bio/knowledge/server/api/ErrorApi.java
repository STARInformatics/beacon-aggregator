package bio.knowledge.server.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T16:36:52.027-07:00")

@Api(value = "error", description = "the error API")
public interface ErrorApi {

    @ApiOperation(value = "", notes = "General purpose error message when an invalid API endpoint is called ", response = String.class, tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "General purpose error message ", response = String.class) })
    @RequestMapping(value = "/error",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<String> getErrorMessage();

}
