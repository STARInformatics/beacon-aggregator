package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.ServerAnnotation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-19T15:02:51.082-07:00")

@Api(value = "evidence", description = "the evidence API")
public interface EvidenceApi {

    @ApiOperation(value = "", notes = "Retrieves a (paged) list of annotations cited as evidence for a specified concept-relationship statement ", response = ServerAnnotation.class, responseContainer = "List", tags={ "statements", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful call returns a list of annotation with metadata ", response = ServerAnnotation.class) })
    @RequestMapping(value = "/evidence/{statementId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ServerAnnotation>> getEvidence(@ApiParam(value = "(url-encoded) CURIE identifier of the concept-relationship statement (\"assertion\", \"claim\") for which associated evidence is sought, e.g. kbs:Q420626_P2175_Q126691 ",required=true ) @PathVariable("statementId") String statementId,
         @ApiParam(value = "an array of keywords or substrings against which to filter an reference label (e.g. title) of a citation serving as statement evidence") @RequestParam(value = "keywords", required = false) List<String> keywords,
         @ApiParam(value = "(1-based) number of the page to be returned in a paged set of query results ") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
         @ApiParam(value = "number of cited references per page to be returned in a paged set of query results ") @RequestParam(value = "pageSize", required = false) Integer pageSize,
         @ApiParam(value = "set of aggregator indices of beacons to be used as knowledge sources for the query ") @RequestParam(value = "beacons", required = false) List<Integer> beacons);

}
