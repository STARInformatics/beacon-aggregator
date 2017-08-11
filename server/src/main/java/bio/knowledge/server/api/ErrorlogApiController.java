package bio.knowledge.server.api;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.LogEntry;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-08T10:59:06.986-07:00")

@Controller
public class ErrorlogApiController implements ErrorlogApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<LogEntry>> getErrors( @NotNull @ApiParam(value = "session identifier ", required = true) @RequestParam(value = "sessionId", required = true) String sessionId) {
        // do some magic!
        return ctrl.getErrors(sessionId);
    }

}
