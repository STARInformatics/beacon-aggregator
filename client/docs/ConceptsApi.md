# ConceptsApi

All URIs are relative to *https://reference-beacon.ncats.io/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getConceptDetails**](ConceptsApi.md#getConceptDetails) | **GET** /concepts/{conceptId} | 
[**getConcepts**](ConceptsApi.md#getConcepts) | **GET** /concepts | 


<a name="getConceptDetails"></a>
# **getConceptDetails**
> List&lt;BeaconConceptWithDetails&gt; getConceptDetails(conceptId)



Retrieves details for a specified concepts in the system, as specified by a (url-encoded) CURIE identifier of a concept known the given knowledge source. 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.ConceptsApi;


ConceptsApi apiInstance = new ConceptsApi();
String conceptId = "conceptId_example"; // String | (url-encoded) CURIE identifier of concept of interest
try {
    List<BeaconConceptWithDetails> result = apiInstance.getConceptDetails(conceptId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConceptsApi#getConceptDetails");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **conceptId** | **String**| (url-encoded) CURIE identifier of concept of interest |

### Return type

[**List&lt;BeaconConceptWithDetails&gt;**](BeaconConceptWithDetails.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getConcepts"></a>
# **getConcepts**
> List&lt;BeaconConcept&gt; getConcepts(keywords, semanticGroups, pageNumber, pageSize)



Retrieves a (paged) list of concepts in the system 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.ConceptsApi;


ConceptsApi apiInstance = new ConceptsApi();
String keywords = "keywords_example"; // String | a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms
String semanticGroups = "semanticGroups_example"; // String | a (url-encoded) space-delimited set of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain concepts matched by the main keyword search (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) 
Integer pageNumber = 56; // Integer | (1-based) number of the page to be returned in a paged set of query results 
Integer pageSize = 56; // Integer | number of concepts per page to be returned in a paged set of query results 
try {
    List<BeaconConcept> result = apiInstance.getConcepts(keywords, semanticGroups, pageNumber, pageSize);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConceptsApi#getConcepts");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **keywords** | **String**| a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms |
 **semanticGroups** | **String**| a (url-encoded) space-delimited set of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain concepts matched by the main keyword search (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes)  | [optional]
 **pageNumber** | **Integer**| (1-based) number of the page to be returned in a paged set of query results  | [optional]
 **pageSize** | **Integer**| number of concepts per page to be returned in a paged set of query results  | [optional]

### Return type

[**List&lt;BeaconConcept&gt;**](BeaconConcept.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

