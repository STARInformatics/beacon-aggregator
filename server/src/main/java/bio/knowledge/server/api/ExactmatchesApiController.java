package bio.knowledge.server.api;


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

import bio.knowledge.server.impl.ControllerImpl;

import java.util.List;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-31T10:50:26.572-07:00")

@Controller
public class ExactmatchesApiController implements ExactmatchesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<String>> getExactMatchesToConcept(@ApiParam(value = "(url-encoded) CURIE identifier of the concept to be matched",required=true ) @PathVariable("conceptId") String conceptId,
         @ApiParam(value = "set of IDs of beacons to be used as knowledge sources for the query ") @RequestParam(value = "sources", required = false) List<String> sources) {
        return ctrl.getExactMatches(conceptId, sources);
    }

    public ResponseEntity<List<String>> getExactMatchesToConceptList( @NotNull @ApiParam(value = "set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of exactly matching concepts, to be used in a search for additional exactly matching concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). ", required = true) @RequestParam(value = "c", required = true) List<String> c,
         @ApiParam(value = "set of IDs of beacons to be used as knowledge sources for the query ") @RequestParam(value = "sources", required = false) List<String> sources) {
        return ctrl.getExactMatches(c, sources);
    }

}
