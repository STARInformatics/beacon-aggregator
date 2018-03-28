package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerStatementsQuery;
import bio.knowledge.server.model.ServerStatementsQueryResult;
import bio.knowledge.server.model.ServerStatementsQueryStatus;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T23:32:19.734-07:00")

@Controller
public class StatementsApiController implements StatementsApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<ServerStatementsQuery> postStatementsQuery( @NotNull @ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of the  exactly matching 'source' clique, as defined by other endpoints of the beacon aggregator API.  ", required = true) @RequestParam(value = "source", required = true) String source,
         @ApiParam(value = "a subset (array) of identifiers of predicate relation identifiers with which to constrain the statement relations retrieved  for the given query seed concept. The predicate ids sent should  be as published by the beacon-aggregator by the /predicates API endpoint. ") @RequestParam(value = "relations", required = false) List<String> relations,
         @ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of the  exactly matching 'target' clique, as defined by other endpoints of the beacon aggregator API.  ") @RequestParam(value = "target", required = false) String target,
         @ApiParam(value = "a (url-encoded, space-delimited) string of keywords or substrings against which to match the 'target' concept or 'predicate' names of the set of concept-relations matched by the 'source' concepts. ") @RequestParam(value = "keywords", required = false) String keywords,
         @ApiParam(value = "a subset (array) of identifiers of concept types to which to constrain 'target' concepts associated with the given 'source' concept ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes). ") @RequestParam(value = "types", required = false) List<String> types,
         @ApiParam(value = "set of aggregator indices of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
         return ctrl.postStatementsQuery(source, relations, target, keywords, types, beacons);
    }

    public ResponseEntity<ServerStatementsQueryStatus> getStatementsQueryStatus(@ApiParam(value = "an active query identifier as returned by a POST of statements  query parameters.",required=true ) @PathVariable("queryId") String queryId,
         @ApiParam(value = "subset of aggregator indices of beacons whose status is being polled (if omitted, then the status of all beacons from the query are returned) ") @RequestParam(value = "beacons", required = false) List<Integer> beacons) {
         return ctrl.getStatementsQueryStatus(queryId, beacons);
    }

    public ResponseEntity<ServerStatementsQueryResult> getStatementsQuery(@ApiParam(value = "an active query identifier as returned by a POST of statement query parameters.",required=true ) @PathVariable("queryId") String queryId,
         @ApiParam(value = "subset of aggregator indices of beacons whose statements are to be retrieved ") @RequestParam(value = "beacons", required = false) List<Integer> beacons,
         @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
         @ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
         return ctrl.getStatements(queryId, beacons, pageNumber, pageSize);
    }
}
