package bio.knowledge.server.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.controller.ControllerImpl;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T16:36:52.027-07:00")

@Controller
public class ErrorApiController implements ErrorApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<String> getErrorMessage() {
         return ctrl.getErrorMessage();
    }

}
