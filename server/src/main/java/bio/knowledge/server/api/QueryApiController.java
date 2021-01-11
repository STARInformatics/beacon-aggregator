package bio.knowledge.server.api;

import org.springframework.stereotype.Controller;
import java.util.Optional;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-08-20T20:17:56.260Z[GMT]")
@Controller
public class QueryApiController implements QueryApi {

    private final QueryApiDelegate delegate;

    @org.springframework.beans.factory.annotation.Autowired
    public QueryApiController(QueryApiDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public QueryApiDelegate getDelegate() {
        return delegate;
    }
}
