package bio.knowledge.server.controller;

import bio.knowledge.server.blackboard.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class LogService {

    private static Logger _logger = LoggerFactory.getLogger(LogService.class);

    @Autowired private MetadataService metadataService;

    /*
     * @param request
     * @return url used to make the request
     */
    private String getUrl(HttpServletRequest request) {
        String query = request.getQueryString();
        query = (query == null)? "" : "?" + query;
        return request.getRequestURL() + query;
    }

    /*
     *
     * @param queryId
     * @param e
     */
    public void logError(String queryId, Exception e) {

        if(queryId.isEmpty()) queryId = "Global";

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String message = e.getMessage();
        if(message!=null) _logger.error(queryId+": "+message);

        metadataService.logError(queryId, null, getUrl(request), e.getMessage());
    }
}
