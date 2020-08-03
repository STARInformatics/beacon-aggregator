package bio.knowledge.server.api;

import bio.knowledge.server.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A delegate to be called by the {@link QueryApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-20T20:17:56.260Z[GMT]")
public interface QueryApiDelegate {

    Logger log = LoggerFactory.getLogger(QueryApi.class);

    default Optional<ObjectMapper> getObjectMapper() {
        return Optional.empty();
    }

    default Optional<HttpServletRequest> getRequest() {
        return Optional.empty();
    }

    default Optional<String> getAcceptHeader() {
        return getRequest().map(r -> r.getHeader("Accept"));
    }

    /**
     * @see QueryApi#query
     */
    default ResponseEntity<Message> query( Map<String, Object>  body) {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    return new ResponseEntity<>(getObjectMapper().get().readValue("{"+
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
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default QueryApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
