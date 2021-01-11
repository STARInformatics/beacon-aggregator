package bio.knowledge.server.controller;

import bio.knowledge.aggregator.KnowledgeBeaconRegistry;
import bio.knowledge.server.api.PredicatesApiDelegate;
import bio.knowledge.server.blackboard.Blackboard;
import bio.knowledge.server.blackboard.BlackboardException;
import bio.knowledge.server.blackboard.MetadataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PredicatesController implements PredicatesApiDelegate {

    private static Logger _logger = LoggerFactory.getLogger(PredicatesController.class);

    @Autowired private LogService kbaLog;
    @Autowired private MetadataService metadataService;

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public PredicatesController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public Optional<ObjectMapper> getObjectMapper() {
        return Optional.of(this.objectMapper);
    }

    @Override
    public Optional<HttpServletRequest> getRequest() {
        return Optional.of(this.request);
    }

    /**
     *
     * @return
     */
    @Override
    public ResponseEntity<Map<String, Map<String, List<String>>>> getPredicates() {

        try {

            Map<String, Map<String,List<String>>> predicate_map = metadataService.getPredicates();

            return new ResponseEntity<>(predicate_map, HttpStatus.OK);

        } catch (BlackboardException bbe) {
            kbaLog.logError("global", bbe);
            return ResponseEntity.badRequest().build();
        }

    }

}
