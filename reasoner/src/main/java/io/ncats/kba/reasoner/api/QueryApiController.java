package io.ncats.kba.reasoner.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ncats.kba.reasoner.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

import io.swagger.annotations.*;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-12T21:13:13.403Z[GMT]")
@Controller
public class QueryApiController implements QueryApi {

    private static final Logger log = LoggerFactory.getLogger(QueryApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public QueryApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Message> query(@ApiParam(value = "Query information to be submitted" ,required=true )  @Valid @RequestBody Map<String, Object> body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Message>(objectMapper.readValue("{"+
                        "\"knowledge_graph\" : \"\","+
                        "\"results\" : [ {"+
                        "  \"edge_bindings\" : [ {"+
                        "      \"kg_id\" : \"\","+
                        "      \"qg_id\" : \"qg_id\""+
                        "    }, {"+
                        "      \"kg_id\" : \"\","+
                        "      \"qg_id\" : \"qg_id\""+
                        "   } ],"+
                        "    \"node_bindings\" : [ {"+
                        "      \"kg_id\" : \"\","+
                        "      \"qg_id\" : \"qg_id\""+
                        "    }, {"+
                        "      \"kg_id\" : \"\","+
                        "      \"qg_id\" : \"qg_id\""+
                        "    } ]"+
                        "  }, {"+
                        "    \"edge_bindings\" : [ {"+
                        "      \"kg_id\" : \"\","+
                        "      \"qg_id\" : \"qg_id\""+
                        "    }, {"+
                        "      \"kg_id\" : \"\","+
                        "      \"qg_id\" : \"qg_id\""+
                        "    } ],"+
                        "    \"node_bindings\" : [ {"+
                        "      \"kg_id\" : \"\","+
                        "      \"qg_id\" : \"qg_id\""+
                        "    }, {"+
                        "      \"kg_id\" : \"\","+
                        "      \"qg_id\" : \"qg_id\""+
                        "    } ]"+
                        "  } ],"+
                        "  \"query_graph\" : {"+
                        "    \"nodes\" : [ {"+
                        "      \"curie\" : \"OMIM:603903\","+
                        "      \"id\" : \"n00\","+
                        "      \"type\" : \"disease\""+
                        "    }, {"+
                        "      \"curie\" : \"OMIM:603903\","+
                        "      \"id\" : \"n00\","+
                        "      \"type\" : \"disease\""+
                        "    } ],"+
                        "    \"edges\" : [ {"+
                        "      \"target_id\" : \"https://www.uniprot.org/uniprot/P00738\","+
                        "      \"id\" : \"e00\","+
                        "      \"source_id\" : \"https://omim.org/entry/603903\","+
                        "      \"type\" : \"affects\""+
                        "    }, {"+
                        "      \"target_id\" : \"https://www.uniprot.org/uniprot/P00738\","+
                        "      \"id\" : \"e00\","+
                        "      \"source_id\" : \"https://omim.org/entry/603903\","+
                        "      \"type\" : \"affects\""+
                        "    } ]"+
                        "  }"+
                        "}", Message.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Message>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Message>(HttpStatus.NOT_IMPLEMENTED);
    }

}
