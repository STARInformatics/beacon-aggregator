package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.Statement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-10T12:47:04.653-07:00")

@Api(value = "statements", description = "the statements API")
public interface StatementsApi {

    @ApiOperation(value = "", notes = "Given a list of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of exactly matching concepts, retrieves a paged list of concept-relations where either the subject or object concept matches at least one concept in the input list ", response = Statement.class, responseContainer = "List", tags={ "statements", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response returns a list of concept-relations where there is an exact match of an input concept identifier either to the subject or object concepts of the statement ", response = Statement.class) })
    @RequestMapping(value = "/statements",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<Statement>> getStatements( @NotNull @ApiParam(value = "set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of exactly matching concepts to be used in a search for associated concept-relation statements, e.g. wd:Q126691, wd:Q420626 ", required = true) @RequestParam(value = "c", required = true) List<String> c,
         @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
         @ApiParam(value = "number of concepts per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize,
         @ApiParam(value = "a (url-encoded, space-delimited) string of keywords or substrings against which to match the subject, predicate or object names of the set of concept-relations matched by any of the input exact matching concepts ") @RequestParam(value = "keywords", required = false) String keywords,
         @ApiParam(value = "a (url-encoded, space-delimited) string of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain the subject or object concepts associated with the query seed concept (see [SemGroups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) ") @RequestParam(value = "semgroups", required = false) String semgroups,
         @ApiParam(value = "set of IDs of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<String> beacons,
         @ApiParam(value = "client-defined session identifier ") @RequestParam(value = "sessionId", required = false) String sessionId);

}
