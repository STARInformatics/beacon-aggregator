/*
 * Translator Knowledge Beacon API
 * This is the Translator Knowledge Beacon web service application programming interface (API).  This OpenAPI (\"Swagger\") document may be used as the input specification into a tool like [Swagger-Codegen](https://github.com/swagger-api/swagger-codegen/blob/master/README.md) to generate client and server code stubs implementing the API, in any one of several supported computer languages and frameworks. In order to customize usage to your web site, you should change the 'host' directive below to your hostname. 
 *
 * OpenAPI spec version: 1.0.11
 * Contact: richard@starinformatics.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package bio.knowledge.client.api;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import bio.knowledge.client.ApiException;
import bio.knowledge.client.model.Summary;

/**
 * API tests for SummaryApi
 */
@Ignore
public class SummaryApiTest {

    private final SummaryApi api = new SummaryApi();

    
    /**
     * 
     *
     * Get a list of types and # of instances in the knowledge source, and a link to the API call for the list of equivalent terminology 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void linkedTypesTest() throws ApiException {
        List<Summary> response = api.linkedTypes(null, null);

        // TODO: test validations
    }
    
}
