package bio.knowledge.server.api;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerLogEntry;

import io.swagger.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-11T17:59:49.447Z")

@Controller
public class ErrorlogApiController implements ErrorlogApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerLogEntry>> getErrors( @NotNull @ApiParam(value = "query identifier returned from a POSTed query ", required = true) @RequestParam(value = "queryId", required = true) String queryId) {
        return ctrl.getErrors(queryId);
    }

}
