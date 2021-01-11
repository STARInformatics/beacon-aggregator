package bio.knowledge.server.api;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.controller.ConceptsController;
import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-09-17T21:48:57.324-07:00")

@Controller
public class ConceptsApiController implements ConceptsApi {

	@Autowired ControllerImpl ctrl;
	@Autowired ConceptsController controller;

    public ResponseEntity<ServerConceptWithDetails> getConceptDetails(
            @ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier, as returned  by any other endpoint of the beacon aggregator API, of an exactly matching  concept clique of interest.",required=true ) @PathVariable("cliqueId") String cliqueId,
            @ApiParam(value = "set of aggregator indices of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		int size = threadSet.size();
		ServerConceptWithDetails s = new ServerConceptWithDetails();
		s.setName("Thread count: " + String.valueOf(size));
		
//    	return ResponseEntity.ok(s);
    	return controller.getConceptDetails(cliqueId, beacons);
    }

    public ResponseEntity<ServerConceptsQueryResult> getConcepts(
            @ApiParam(value = "the query identifier of a concepts query previously posted by the /concepts endpoint",required=true ) @PathVariable("queryId") String queryId,
            @ApiParam(value = "set of aggregator indices of beacons whose data are to be retrieved ") @RequestParam(value = "beacons", required = false) List<Integer> beacons,
            @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results. Defaults to '1'. ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "number of concepts per page to be returned in a paged set of query results. Defaults to '10'. ") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
         return controller.getConcepts(queryId, beacons, pageNumber, pageSize);
    }

    public ResponseEntity<ServerConceptsQueryStatus> getConceptsQueryStatus(
            @ApiParam(value = "an active query identifier as returned by a POST of concept query parameters.",required=true ) @PathVariable("queryId") String queryId,
            @ApiParam(value = "subset of aggregator indices of beacons whose status is being polled (if omitted, then the status of all beacons from the query are returned) ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
         return controller.getConceptsQueryStatus(queryId, beacons);
    }

    public ResponseEntity<ServerConceptsQuery> postConceptsQuery(
            @NotNull @ApiParam(value = "an array of keywords or substrings against which to match concept names and synonyms", required = true) @RequestParam(value = "keywords", required = true) List<String> keywords,
            @ApiParam(value = "a subset array of concept categories (specified as codes 'gene',  'pathway', etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) ") @RequestParam(value = "categories", required = false) List<String> categories,
            @ApiParam(value = "subset of aggregator indices of beacons to be used as knowledge sources for the query (if omitted, then the all beacons are queried) ") @RequestParam(value = "beacons", required = false) List<Integer> beacons
    ) {
    	return controller.postConceptsQuery(keywords, categories, beacons);
//    	return ctrl.postConceptsQuery(keywords, categories, beacons);
    }

}
