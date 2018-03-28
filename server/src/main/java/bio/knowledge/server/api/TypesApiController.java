package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerConceptType;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T23:32:19.734-07:00")

@Controller
public class TypesApiController implements TypesApi {



    public ResponseEntity<List<ServerConceptType>> getConceptTypes( @ApiParam(value = "set of aggregator indices of beacons to constrain types returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
        // do some magic!
        return new ResponseEntity<List<ServerConceptType>>(HttpStatus.OK);
    }

}
