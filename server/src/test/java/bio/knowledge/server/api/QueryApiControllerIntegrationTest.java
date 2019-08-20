package bio.knowledge.server.api;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import bio.knowledge.server.model.Message;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QueryApiControllerIntegrationTest {

    @Autowired
    private QueryApi api;

    @Test
    public void queryTest() throws Exception {
        Map<String, Object> body = new HashMap();
        ResponseEntity<Message> responseEntity = api.query(body);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

}
