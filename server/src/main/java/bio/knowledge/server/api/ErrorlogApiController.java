package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.ServerLogEntry;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-12T16:26:47.889-07:00")

@Controller
public class ErrorlogApiController implements ErrorlogApi {



    public ResponseEntity<List<ServerLogEntry>> getErrors( @NotNull @ApiParam(value = "query identifier returned from a POSTed query ", required = true) @RequestParam(value = "queryId", required = true) String queryId) {
        // do some magic!
        return new ResponseEntity<List<ServerLogEntry>>(HttpStatus.OK);
    }

}
