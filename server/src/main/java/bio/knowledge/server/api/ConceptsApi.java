package bio.knowledge.server.api;

import bio.knowledge.server.model.ServerConceptWithDetails;
import bio.knowledge.server.model.ServerConceptsQuery;
import bio.knowledge.server.model.ServerConceptsQueryResult;
import bio.knowledge.server.model.ServerConceptsQueryStatus;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-12T22:38:51.826-07:00")

@Api(value = "concepts", description = "the concepts API")
public interface ConceptsApi {

    @ApiOperation(value = "", notes = "Retrieves details for a specified clique of equivalent concepts in the system,  as specified by a (url-encoded) CURIE identifier of a clique known to the aggregator ", response = ServerConceptWithDetails.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with details of a clique concept returned ", response = ServerConceptWithDetails.class) })
    @RequestMapping(value = "/concepts/details/{cliqueId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ServerConceptWithDetails> getConceptDetails(@ApiParam(value = "a [CURIE-encoded](https://www.w3.org/TR/curie/) identifier, as returned  by any other endpoint of the beacon aggregator API, of an exactly matching  concept clique of interest.",required=true ) @PathVariable("cliqueId") String cliqueId,
         @ApiParam(value = "set of aggregator indices of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<Integer> beacons,
         @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId);


    @ApiOperation(value = "", notes = "Retrieves a (paged) simple list of concepts from beacons with status 'data ready' from a previously /concepts posted query parameter submission ", response = ServerConceptsQueryResult.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with concept list returned ", response = ServerConceptsQueryResult.class) })
    @RequestMapping(value = "/concepts/data/{queryId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ServerConceptsQueryResult> getConcepts(@ApiParam(value = "the query identifier of a concepts query previously posted by the /concepts endpoint",required=true ) @PathVariable("queryId") String queryId,
         @ApiParam(value = "set of aggregator indices of beacons whose data are to be retrieved ") @RequestParam(value = "beacons", required = false) List<Integer> beacons,
         @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
         @ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize);


    @ApiOperation(value = "", notes = "Retrieves the status of a given query about the concepts in the system ", response = ServerConceptsQueryStatus.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Current query status returned ", response = ServerConceptsQueryStatus.class) })
    @RequestMapping(value = "/concepts/status/{queryId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<ServerConceptsQueryStatus> getConceptsQueryStatus(@ApiParam(value = "an active query identifier as returned by a POST of concept query parameters.",required=true ) @PathVariable("queryId") String queryId,
         @ApiParam(value = "subset of aggregator indices of beacons whose status is being polled (if omitted, then the status of all beacons from the query are returned) ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);


    @ApiOperation(value = "", notes = "Posts the query parameters to retrieves a (paged) list of  concepts from the system ", response = ServerConceptsQuery.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful concept query initialization, with initial query status returned ", response = ServerConceptsQuery.class) })
    @RequestMapping(value = "/concepts",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<ServerConceptsQuery> postConceptsQuery( @NotNull @ApiParam(value = "a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms, e.g. diabetes.", required = true) @RequestParam(value = "keywords", required = true) String keywords,
         @ApiParam(value = "a (url-encoded) space-delimited set of semantic groups (specified as codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) ") @RequestParam(value = "types", required = false) String types,
         @ApiParam(value = "subset of aggregator indices of beacons to be used as knowledge sources for the query (if omitted, then the all beacons are queried) ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);

}
