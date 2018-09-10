package bio.knowledge.server.api;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerKnowledgeMap;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-28T14:42:53.737-07:00")

@Controller
public class KmapApiController implements KmapApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerKnowledgeMap>> getKnowledgeMap( @ApiParam(value = "set of aggregator indices of beacons constraining knowledge maps returned  ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
        return ctrl.getKnowledgeMap(beacons);
    }

}
