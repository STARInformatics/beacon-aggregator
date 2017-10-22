package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.ServerStatement;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-21T21:32:27.439-07:00")

@Controller
public class StatementsApiController implements StatementsApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerStatement>> getStatements(@ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier, as returned  by any other endpoint of the beacon aggregator API, of an exactly matching  concept clique of interest. ",required=true ) @PathVariable("cliqueId") String cliqueId,
         @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
         @ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize,
         @ApiParam(value = "a (url-encoded, space-delimited) string of keywords or substrings against which to match the subject, predicate or object names of the set of concept-relations matched by any of the input exact matching concepts ") @RequestParam(value = "keywords", required = false) String keywords,
         @ApiParam(value = "a (url-encoded, space-delimited) string of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain the subject or object concepts associated with the query seed concept (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ") @RequestParam(value = "semanticGroups", required = false) String semanticGroups,
         @ApiParam(value = "a (url-encoded, space-delimited) string of predicate relation identifiers with which to constrain the statement relations retrieved  for the given query seed concept. The predicate ids sent should  be as published by the beacon-aggregator by the /predicates API endpoint. ") @RequestParam(value = "relations", required = false) String relations,
         @ApiParam(value = "set of aggregator indices of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
         @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId) {
         return ctrl.getStatements(cliqueId, pageNumber, pageSize, keywords, semanticGroups, relations, beacons, sessionId);
    }

}
