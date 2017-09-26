package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.Concept;
import bio.knowledge.server.model.ConceptDetail;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-08-15T11:46:37.748-07:00")

@Controller
public class ConceptsApiController implements ConceptsApi {
	
	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ConceptDetail>> getConceptDetails(@ApiParam(value = "(url-encoded) CURIE identifier of concept of interest, e.g. wd:Q126691",required=true ) @PathVariable("conceptId") String conceptId,
        @ApiParam(value = "set of IDs of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
        @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId) {

    	return ctrl.getConceptDetails(conceptId, beacons, sessionId);
    }

    public ResponseEntity<List<Concept>> getConcepts( @NotNull@ApiParam(value = "a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms, e.g. diabetes.", required = true) @RequestParam(value = "keywords", required = true) String keywords,
        @ApiParam(value = "a (url-encoded) space-delimited set of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain concepts matched by the main keyword search (see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ") @RequestParam(value = "semgroups", required = false) String semgroups,
        @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
        @ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize,
        @ApiParam(value = "set of IDs of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
        @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId) {

    	return ctrl.getConcepts(keywords, semgroups, pageNumber, pageSize, beacons, sessionId);
    }

}
