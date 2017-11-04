/*
 * Translator Knowledge Beacon API
 * This is the Translator Knowledge Beacon web service application programming interface (API). 
 *
 * OpenAPI spec version: 1.0.15
 * Contact: richard@starinformatics.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package bio.knowledge.client.api;

import bio.knowledge.client.ApiCallback;
import bio.knowledge.client.ApiClient;
import bio.knowledge.client.ApiException;
import bio.knowledge.client.ApiResponse;
import bio.knowledge.client.Configuration;
import bio.knowledge.client.Pair;
import bio.knowledge.client.ProgressRequestBody;
import bio.knowledge.client.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import bio.knowledge.client.model.BeaconConcept;
import bio.knowledge.client.model.BeaconConceptWithDetails;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConceptsApi {
    private ApiClient apiClient;

    public ConceptsApi() {
        this(Configuration.getDefaultApiClient());
    }

    public ConceptsApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /* Build call for getConceptDetails */
    private com.squareup.okhttp.Call getConceptDetailsCall(String conceptId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/concepts/{conceptId}".replaceAll("\\{format\\}","json")
        .replaceAll("\\{" + "conceptId" + "\\}", apiClient.escapeString(conceptId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call getConceptDetailsValidateBeforeCall(String conceptId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'conceptId' is set
        if (conceptId == null) {
            throw new ApiException("Missing the required parameter 'conceptId' when calling getConceptDetails(Async)");
        }
        
        
        com.squareup.okhttp.Call call = getConceptDetailsCall(conceptId, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * 
     * Retrieves details for a specified concepts in the system, as specified by a (url-encoded) CURIE identifier of a concept known the given knowledge source. 
     * @param conceptId (url-encoded) CURIE identifier of concept of interest (required)
     * @return List&lt;BeaconConceptWithDetails&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<BeaconConceptWithDetails> getConceptDetails(String conceptId) throws ApiException {
        ApiResponse<List<BeaconConceptWithDetails>> resp = getConceptDetailsWithHttpInfo(conceptId);
        return resp.getData();
    }

    /**
     * 
     * Retrieves details for a specified concepts in the system, as specified by a (url-encoded) CURIE identifier of a concept known the given knowledge source. 
     * @param conceptId (url-encoded) CURIE identifier of concept of interest (required)
     * @return ApiResponse&lt;List&lt;BeaconConceptWithDetails&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<BeaconConceptWithDetails>> getConceptDetailsWithHttpInfo(String conceptId) throws ApiException {
        com.squareup.okhttp.Call call = getConceptDetailsValidateBeforeCall(conceptId, null, null);
        Type localVarReturnType = new TypeToken<List<BeaconConceptWithDetails>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Retrieves details for a specified concepts in the system, as specified by a (url-encoded) CURIE identifier of a concept known the given knowledge source. 
     * @param conceptId (url-encoded) CURIE identifier of concept of interest (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getConceptDetailsAsync(String conceptId, final ApiCallback<List<BeaconConceptWithDetails>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = getConceptDetailsValidateBeforeCall(conceptId, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<BeaconConceptWithDetails>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /* Build call for getConcepts */
    private com.squareup.okhttp.Call getConceptsCall(String keywords, String semanticGroups, Integer pageNumber, Integer pageSize, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;
        
        // create path and map variables
        String localVarPath = "/concepts".replaceAll("\\{format\\}","json");

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        if (keywords != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "keywords", keywords));
        if (semanticGroups != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "semanticGroups", semanticGroups));
        if (pageNumber != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "pageNumber", pageNumber));
        if (pageSize != null)
        localVarQueryParams.addAll(apiClient.parameterToPairs("", "pageSize", pageSize));

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
            
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new com.squareup.okhttp.Interceptor() {
                @Override
                public com.squareup.okhttp.Response intercept(com.squareup.okhttp.Interceptor.Chain chain) throws IOException {
                    com.squareup.okhttp.Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }
    
    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call getConceptsValidateBeforeCall(String keywords, String semanticGroups, Integer pageNumber, Integer pageSize, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        
        // verify the required parameter 'keywords' is set
        if (keywords == null) {
            throw new ApiException("Missing the required parameter 'keywords' when calling getConcepts(Async)");
        }
        
        
        com.squareup.okhttp.Call call = getConceptsCall(keywords, semanticGroups, pageNumber, pageSize, progressListener, progressRequestListener);
        return call;

        
        
        
        
    }

    /**
     * 
     * Retrieves a (paged) list of concepts in the system 
     * @param keywords a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms (required)
     * @param semanticGroups a (url-encoded) space-delimited set of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain concepts matched by the main keyword search (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes)  (optional)
     * @param pageNumber (1-based) number of the page to be returned in a paged set of query results  (optional)
     * @param pageSize number of concepts per page to be returned in a paged set of query results  (optional)
     * @return List&lt;BeaconConcept&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public List<BeaconConcept> getConcepts(String keywords, String semanticGroups, Integer pageNumber, Integer pageSize) throws ApiException {
        ApiResponse<List<BeaconConcept>> resp = getConceptsWithHttpInfo(keywords, semanticGroups, pageNumber, pageSize);
        return resp.getData();
    }

    /**
     * 
     * Retrieves a (paged) list of concepts in the system 
     * @param keywords a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms (required)
     * @param semanticGroups a (url-encoded) space-delimited set of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain concepts matched by the main keyword search (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes)  (optional)
     * @param pageNumber (1-based) number of the page to be returned in a paged set of query results  (optional)
     * @param pageSize number of concepts per page to be returned in a paged set of query results  (optional)
     * @return ApiResponse&lt;List&lt;BeaconConcept&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<List<BeaconConcept>> getConceptsWithHttpInfo(String keywords, String semanticGroups, Integer pageNumber, Integer pageSize) throws ApiException {
        com.squareup.okhttp.Call call = getConceptsValidateBeforeCall(keywords, semanticGroups, pageNumber, pageSize, null, null);
        Type localVarReturnType = new TypeToken<List<BeaconConcept>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     *  (asynchronously)
     * Retrieves a (paged) list of concepts in the system 
     * @param keywords a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms (required)
     * @param semanticGroups a (url-encoded) space-delimited set of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain concepts matched by the main keyword search (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes)  (optional)
     * @param pageNumber (1-based) number of the page to be returned in a paged set of query results  (optional)
     * @param pageSize number of concepts per page to be returned in a paged set of query results  (optional)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getConceptsAsync(String keywords, String semanticGroups, Integer pageNumber, Integer pageSize, final ApiCallback<List<BeaconConcept>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = getConceptsValidateBeforeCall(keywords, semanticGroups, pageNumber, pageSize, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<BeaconConcept>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
