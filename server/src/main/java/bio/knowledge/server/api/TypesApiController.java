package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.InlineResponse200;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-19T15:08:40.849-07:00")

@Controller
public class TypesApiController implements TypesApi {
	
	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<InlineResponse200>> linkedTypes() {
    	return ctrl.linkedTypes();
    }

}
