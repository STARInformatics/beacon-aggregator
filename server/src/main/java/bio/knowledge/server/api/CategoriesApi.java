package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerConceptCategory;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-08-28T14:42:53.737-07:00")

@Api(value = "categories", description = "the categories API")
public interface CategoriesApi {

    @ApiOperation(value = "", notes = "Get a list of semantic categories and number of instances in each  available knowledge beacon, including associated beacon-specific metadata ", response = ServerConceptCategory.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with concept categories and frequency returned ", response = ServerConceptCategory.class) })
    @RequestMapping(value = "/categories",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerConceptCategory>> getConceptCategories( @ApiParam(value = "set of aggregator indices of beacons to constrain categories returned ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);

}
