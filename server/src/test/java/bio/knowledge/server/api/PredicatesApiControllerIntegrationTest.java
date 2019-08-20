package bio.knowledge.server.api;

import java.util.Map;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PredicatesApiControllerIntegrationTest {

    @Autowired
    private PredicatesApi api;

    @Test
    public void predicatesGetTest() throws Exception {
        ResponseEntity<Map<String, Map<String>> responseEntity = api.predicatesGet();
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

}
