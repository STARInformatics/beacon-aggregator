package bio.knowledge.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerCliqueIdentifier;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T22:57:55.565-07:00")

@Controller
public class CliqueApiController implements CliqueApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<ServerCliqueIdentifier> getClique(@ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of interest to be resolved to a concept clique",required=true ) @PathVariable("identifier") String identifier) {
         return ctrl.getClique(identifier);
    }

}
