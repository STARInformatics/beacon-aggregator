package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.model.ServerKnowledgeBeacon;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-12T16:26:47.889-07:00")

@Controller
public class BeaconsApiController implements BeaconsApi {



    public ResponseEntity<List<ServerKnowledgeBeacon>> getBeacons() {
        // do some magic!
        return new ResponseEntity<List<ServerKnowledgeBeacon>>(HttpStatus.OK);
    }

}
