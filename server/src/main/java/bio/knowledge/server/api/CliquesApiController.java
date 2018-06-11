package bio.knowledge.server.api;

import bio.knowledge.server.controller.ControllerImpl;
import bio.knowledge.server.model.ServerCliquesQuery;
import bio.knowledge.server.model.ServerCliquesQueryResult;
import bio.knowledge.server.model.ServerCliquesQueryStatus;

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

import java.util.List;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-06T22:00:04.615Z")

@Controller
public class CliquesApiController implements CliquesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<ServerCliquesQueryResult> getCliques(@ApiParam(value = "the query identifier of a concepts query previously posted by the /cliques endpoint",required=true ) @PathVariable("queryId") String queryId) {
        return ctrl.getCliques(queryId);
    }

    public ResponseEntity<ServerCliquesQueryStatus> getCliquesQueryStatus(@ApiParam(value = "an active query identifier as returned by a POST of clique query parameters.",required=true ) @PathVariable("queryId") String queryId) {
        return ctrl.getCliquesQueryStatus(queryId);
    }

    public ResponseEntity<ServerCliquesQuery> postCliquesQuery( @NotNull @ApiParam(value = "an array of [CURIE-encoded](https://www.w3.org/TR/curie/)  identifiers of interest to be resolved to a list of concept cliques", required = true) @RequestParam(value = "ids", required = true) List<String> ids) {
        return ctrl.postCliquesQuery(ids);
    }

}
