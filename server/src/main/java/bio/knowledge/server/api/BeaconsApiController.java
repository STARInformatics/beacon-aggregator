package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerKnowledgeBeacon;

import io.swagger.annotations.*;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-13T09:29:19.027-07:00")

@Controller
public class BeaconsApiController implements BeaconsApi {



    public ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons() {
        // do some magic!
        return new ResponseEntity<List<ServerKnowledgeBeacon>>(HttpStatus.OK);
    }

}
