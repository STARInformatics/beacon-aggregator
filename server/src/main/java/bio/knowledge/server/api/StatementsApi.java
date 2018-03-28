package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerStatementsQuery;
import bio.knowledge.server.model.ServerStatementsQueryResult;
import bio.knowledge.server.model.ServerStatementsQueryStatus;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-27T22:57:55.565-07:00")

@Api(value = "statements", description = "the statements API")
public interface StatementsApi {

    @ApiOperation(value = "", notes = "Given a specification [CURIE-encoded](https://www.w3.org/TR/curie/) a 'source' clique identifier for a set of exactly matching concepts,  retrieves a paged list of concept-relations where either the subject or object concept matches the 'source' clique identifier.  Optionally, a 'target' clique identifier may also be given, in which case the 'target' clique identifier should match the concept clique opposing the 'source', that is, if the 'source' matches a subject, then the  'target' should match the object of a given statement (or vice versa). ", response = ServerStatementsQueryResult.class, tags={ "statements", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response returns a list of concept-relations where there is an exact match of an input concept identifier either to the subject or object concepts of the statement ", response = ServerStatementsQueryResult.class) })
    @RequestMapping(value = "/statements/data/{queryId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ServerStatementsQueryResult> getStatementsQuery(@ApiParam(value = "an active query identifier as returned by a POST of statement query parameters.",required=true ) @PathVariable("queryId") String queryId,
         @ApiParam(value = "subset of aggregator indices of beacons whose statements are to be retrieved ") @RequestParam(value = "beacons", required = false) List<Integer> beacons,
         @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
         @ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize);


    @ApiOperation(value = "", notes = "Retrieves the status of a given query about the statements in the system ", response = ServerStatementsQueryStatus.class, tags={ "statements", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful concept query initialization, with initial query status returned ", response = ServerStatementsQueryStatus.class) })
    @RequestMapping(value = "/statements/status/{queryId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ServerStatementsQueryStatus> getStatementsQueryStatus(@ApiParam(value = "an active query identifier as returned by a POST of statements  query parameters.",required=true ) @PathVariable("queryId") String queryId,
         @ApiParam(value = "subset of aggregator indices of beacons whose status is being polled (if omitted, then the status of all beacons from the query are returned) ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);


    @ApiOperation(value = "", notes = "Posts a query to retrieve concept-relations where either the subject or object concept matches a [CURIE-encoded 'source'](https://www.w3.org/TR/curie/) clique identifier designating a set of exactly matching concepts. A 'target' clique identifier may optionally be given, in which case the 'target' clique identifier should match the concept clique opposing the 'source', that is, if the 'source' matches a subject, then the  'target' should match the object of a given statement (or vice versa). ", response = ServerStatementsQuery.class, tags={ "statements", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful concept query initialization, with initial query status returned ", response = ServerStatementsQuery.class) })
    @RequestMapping(value = "/statements",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<ServerStatementsQuery> postStatementsQuery( @NotNull @ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of the  exactly matching 'source' clique, as defined by other endpoints of the beacon aggregator API.  ", required = true) @RequestParam(value = "source", required = true) String source,
         @ApiParam(value = "a subset (array) of identifiers of predicate relation identifiers with which to constrain the statement relations retrieved  for the given query seed concept. The predicate ids sent should  be as published by the beacon-aggregator by the /predicates API endpoint. ") @RequestParam(value = "relations", required = false) List<String> relations,
         @ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier of the  exactly matching 'target' clique, as defined by other endpoints of the beacon aggregator API.  ") @RequestParam(value = "target", required = false) String target,
         @ApiParam(value = "a (url-encoded, space-delimited) string of keywords or substrings against which to match the 'target' concept or 'predicate' names of the set of concept-relations matched by the 'source' concepts. ") @RequestParam(value = "keywords", required = false) String keywords,
         @ApiParam(value = "a subset (array) of identifiers of concept types to which to constrain 'target' concepts associated with the given 'source' concept ((see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes). ") @RequestParam(value = "types", required = false) List<String> types,
         @ApiParam(value = "set of aggregator indices of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);

}
