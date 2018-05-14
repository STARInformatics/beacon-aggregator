package bio.knowledge.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import bio.knowledge.server.model.ServerCliqueIdentifier;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-14T14:44:00.373-07:00")

@Controller
public class CliqueApiController implements CliqueApi {



    public ResponseEntity<ServerCliqueIdentifier> getClique(@ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of interest to be resolved to a concept clique",required=true ) @PathVariable("identifier") String identifier) {
        // do some magic!
        return new ResponseEntity<ServerCliqueIdentifier>(HttpStatus.OK);
    }

}
