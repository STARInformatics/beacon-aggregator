package bio.knowledge.server.api;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * A delegate to be called by the {@link PredicatesApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-20T20:17:56.260Z[GMT]")
public interface PredicatesApiDelegate {

    Logger log = LoggerFactory.getLogger(PredicatesApi.class);

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
     * @see PredicatesApi#predicatesGet
     */
    default ResponseEntity<Map<String, Map<String,List<String>>>> getPredicates() {
        if(getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
            if (getAcceptHeader().get().contains("application/json")) {
                try {
                    List<String> examplePredicates = new ArrayList<>();
                    examplePredicates.add("directly_interacts_with");
                    examplePredicates.add("decreases_activity_of");

                    Map<String,List<String>> exampleObject = new HashMap<>();
                    exampleObject.put("gene", examplePredicates);

                    Map<String, Map<String,List<String>>> exampleSubject = new HashMap<>();
                    exampleSubject.put("chemical_substance", exampleObject);

                    return new ResponseEntity<Map<String, Map<String,List<String>>>> (
                            exampleSubject,
                            HttpStatus.NOT_IMPLEMENTED
                    );
                } catch (Exception e) {
                    log.error("Couldn't serialize response for content type application/json", e);
                    return new ResponseEntity<Map<String, Map<String, List<String>>>>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            log.warn("ObjectMapper or HttpServletRequest not configured in default PredicatesApi interface so no example is generated");
        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
