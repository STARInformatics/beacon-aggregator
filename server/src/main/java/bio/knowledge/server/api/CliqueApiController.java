package bio.knowledge.server.api;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerCliqueIdentifier;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-01T20:11:14.227Z")

@Controller
public class CliqueApiController implements CliqueApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerCliqueIdentifier>> getClique(@ApiParam(value = "a list of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of interest to be resolved to a list of concept clique",required=true ) @PathVariable("identifiers") List<String> identifiers) {
        // do some magic!
        return ctrl.getClique(identifiers);
    }

}
