/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.10).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.ncats.kba.reasoner.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import io.swagger.annotations.*;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-12T21:13:13.403Z[GMT]")
@Api(value = "predicates", description = "the predicates API")
public interface PredicatesApi {

    @ApiOperation(value = "Get supported relationships by source and target", nickname = "predicatesGet", notes = "", response = Map.class, responseContainer = "Map", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Predicates by source and target", response = Map.class, responseContainer = "Map") })
    @RequestMapping(value = "/predicates",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Map<String, Map<String,String>>> predicatesGet();

}
