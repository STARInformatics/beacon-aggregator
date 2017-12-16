package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.ServerStatement;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-05T23:10:08.342-08:00")

@Controller
public class StatementsApiController implements StatementsApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ServerStatement>> getStatements( @NotNull @ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of the  exactly matching 'source' clique, as defined by other endpoints of the beacon aggregator API.  ", required = true) @RequestParam(value = "source", required = true) String source,
         @ApiParam(value = "a (url-encoded, space-delimited) string of predicate relation identifiers with which to constrain the statement relations retrieved  for the given query seed concept. The predicate ids sent should  be as published by the beacon-aggregator by the /predicates API endpoint. ") @RequestParam(value = "relations", required = false) String relations,
         @ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of the  exactly matching 'target' clique, as defined by other endpoints of the beacon aggregator API.  ") @RequestParam(value = "target", required = false) String target,
         @ApiParam(value = "a (url-encoded, space-delimited) string of keywords or substrings against which to match the 'target' concept or 'predicate' names of the set of concept-relations matched by the 'source' concepts. ") @RequestParam(value = "keywords", required = false) String keywords,
         @ApiParam(value = "a (url-encoded, space-delimited) string of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain 'target' concepts associated with the given 'source' concept  (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ") @RequestParam(value = "semanticGroups", required = false) String semanticGroups,
         @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
         @ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize,
         @ApiParam(value = "set of aggregator indices of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
         @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId) {
         return ctrl.getStatements(source, relations, target, keywords, semanticGroups, pageNumber, pageSize, beacons, sessionId);
    }

}
