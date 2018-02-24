package bio.knowledge.server.api;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.ServerConceptType;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-24T08:51:45.143-08:00")

@Controller
public class TypesApiController implements TypesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerConceptType>> getConceptTypes( @ApiParam(value = "set of aggregator indices of beacons to constrain knowledge sources accessed by the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
         @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId) {
         return ctrl.getConceptTypes(beacons, sessionId);
    }

}
