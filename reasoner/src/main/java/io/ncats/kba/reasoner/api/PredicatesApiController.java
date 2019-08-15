package io.ncats.kba.reasoner.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-12T21:13:13.403Z[GMT]")
@Controller
public class PredicatesApiController implements PredicatesApi {

    private static final Logger log = LoggerFactory.getLogger(PredicatesApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public PredicatesApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Map<String, Map<String, List>>> predicatesGet() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                List<String> examplePredicates = new ArrayList();
                examplePredicates.add("directly_interacts_with");
                examplePredicates.add("decreases_activity_of");

                Map<String,List> exampleObject = new HashMap();
                exampleObject.put("gene", examplePredicates);

                Map<String, Map<String,List>> exampleSubject = new HashMap();
                exampleSubject.put("chemical_substance", exampleObject);

                return new ResponseEntity<Map<String, Map<String,List>>> (
                                    exampleSubject,
                                    HttpStatus.NOT_IMPLEMENTED
                            );
            } catch (Exception e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Map<String, Map<String, List>>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Map<String, Map<String,List>>>(HttpStatus.NOT_IMPLEMENTED);
    }

}
