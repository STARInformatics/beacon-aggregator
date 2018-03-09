# ConceptsApi

All URIs are relative to *https://rkb.ncats.io/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getConceptDetails**](ConceptsApi.md#getConceptDetails) | **GET** /concepts/{conceptId} | 
[**getConcepts**](ConceptsApi.md#getConcepts) | **GET** /concepts | 
[**getExactMatchesToConcept**](ConceptsApi.md#getExactMatchesToConcept) | **GET** /exactmatches/{conceptId} | 
[**getExactMatchesToConceptList**](ConceptsApi.md#getExactMatchesToConceptList) | **GET** /exactmatches | 


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
> List&lt;BeaconConcept&gt; getConcepts(keywords, types, pageNumber, pageSize)



Retrieves a (paged) list of whose concept in the  beacon knowledge base with names and/or synonyms  matching a set of keywords or substrings.  The (possibly paged) results returned should generally  be returned in order of the quality of the match,  that is, the highest ranked concepts should exactly  match the most keywords, in the same order as the  keywords were given. Lower quality hits with fewer  keyword matches or out-of-order keyword matches,  should be returned lower in the list. 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.ConceptsApi;


ConceptsApi apiInstance = new ConceptsApi();
String keywords = "keywords_example"; // String | a (urlencoded) space delimited set of keywords or substrings against which to match concept names and synonyms
String types = "types_example"; // String | a (url-encoded) space-delimited set of semantic groups (specified as codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) 
Integer pageNumber = 56; // Integer | (1-based) number of the page to be returned in a paged set of query results 
Integer pageSize = 56; // Integer | number of concepts per page to be returned in a paged set of query results 
try {
    List<BeaconConcept> result = apiInstance.getConcepts(keywords, types, pageNumber, pageSize);
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
 **types** | **String**| a (url-encoded) space-delimited set of semantic groups (specified as codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes)  | [optional]
 **pageNumber** | **Integer**| (1-based) number of the page to be returned in a paged set of query results  | [optional]
 **pageSize** | **Integer**| number of concepts per page to be returned in a paged set of query results  | [optional]

### Return type

[**List&lt;BeaconConcept&gt;**](BeaconConcept.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getExactMatchesToConcept"></a>
# **getExactMatchesToConcept**
> List&lt;String&gt; getExactMatchesToConcept(conceptId)



Retrieves a list of qualified identifiers of \&quot;exact match\&quot; concepts, [sensa SKOS](http://www.w3.org/2004/02/skos/core#exactMatch) associated with a specified (url-encoded) CURIE (without brackets) concept object identifier,  typically, of a concept selected from the list of concepts originally returned by a /concepts API call on a given KS.  

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.ConceptsApi;


ConceptsApi apiInstance = new ConceptsApi();
String conceptId = "conceptId_example"; // String | (url-encoded) CURIE identifier of the concept to be matched
try {
    List<String> result = apiInstance.getExactMatchesToConcept(conceptId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConceptsApi#getExactMatchesToConcept");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **conceptId** | **String**| (url-encoded) CURIE identifier of the concept to be matched |

### Return type

**List&lt;String&gt;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getExactMatchesToConceptList"></a>
# **getExactMatchesToConceptList**
> List&lt;String&gt; getExactMatchesToConceptList(c)



Given an input list of [CURIE](https://www.w3.org/TR/curie/) identifiers of known exactly matched concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch), retrieves the list of [CURIE](https://www.w3.org/TR/curie/) identifiers of additional concepts that are deemed by the given knowledge source to be exact matches to one or more of the input concepts **plus** whichever identifiers from the input list which specifically matched these new additional concepts.  If an empty set is returned, the it can be assumed that the given  knowledge source does not know of any new equivalent concepts matching the input set. 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.ConceptsApi;


ConceptsApi apiInstance = new ConceptsApi();
List<String> c = Arrays.asList("c_example"); // List<String> | set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of exactly matching concepts, to be used in a search for additional exactly matching concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). 
try {
    List<String> result = apiInstance.getExactMatchesToConceptList(c);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConceptsApi#getExactMatchesToConceptList");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **c** | [**List&lt;String&gt;**](String.md)| set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of exactly matching concepts, to be used in a search for additional exactly matching concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch).  |

### Return type

**List&lt;String&gt;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

