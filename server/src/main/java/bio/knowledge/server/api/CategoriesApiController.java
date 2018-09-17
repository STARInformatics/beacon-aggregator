package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerConceptCategory;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-28T14:42:53.737-07:00")

@Controller
public class CategoriesApiController implements CategoriesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerConceptCategory>> getConceptCategories( @ApiParam(value = "set of aggregator indices of beacons to constrain categories returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
    	return ctrl.getConceptCategories(beacons);
    }

}
