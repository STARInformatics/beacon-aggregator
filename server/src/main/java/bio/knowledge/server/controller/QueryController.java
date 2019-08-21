package bio.knowledge.server.controller;

import bio.knowledge.server.api.QueryApiDelegate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class QueryController implements QueryApiDelegate {

    private static Logger _logger = LoggerFactory.getLogger(QueryController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public QueryController(ObjectMapper objectMapper, HttpServletRequest request) {
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


}
