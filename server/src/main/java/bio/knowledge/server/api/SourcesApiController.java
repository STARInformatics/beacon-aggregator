package bio.knowledge.server.api;

import bio.knowledge.server.model.InlineResponse2005;

import io.swagger.annotations.*;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-07-27T15:18:21.292-07:00")

@Controller
public class SourcesApiController implements SourcesApi {



    public ResponseEntity<List<InlineResponse2005>> getSources() {
        // do some magic!
        return new ResponseEntity<List<InlineResponse2005>>(HttpStatus.OK);
    }

}
