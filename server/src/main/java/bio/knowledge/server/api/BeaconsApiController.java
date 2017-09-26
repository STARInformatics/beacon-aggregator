package bio.knowledge.server.api;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.KnowledgeBeacon;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-26T14:52:59.489-07:00")

@Controller
public class BeaconsApiController implements BeaconsApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<KnowledgeBeacon>> getBeacons( @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId) {
        
        return ctrl.getBeacons();
    }

}
