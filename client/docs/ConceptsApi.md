# ConceptsApi

All URIs are relative to *https://rkb.ncats.io/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getConceptDetails**](ConceptsApi.md#getConceptDetails) | **GET** /concepts/{conceptId} | 
[**getConcepts**](ConceptsApi.md#getConcepts) | **GET** /concepts | 
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
> List&lt;BeaconConcept&gt; getConcepts(keywords, categories, size)



Retrieves a (paged) list of whose concept in the  beacon knowledge base with names and/or synonyms  matching a set of keywords or substrings.  The (possibly paged) results returned should generally  be returned in order of the quality of the match,  that is, the highest ranked concepts should exactly  match the most keywords, in the same order as the  keywords were given. Lower quality hits with fewer  keyword matches or out-of-order keyword matches,  should be returned lower in the list. 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.ConceptsApi;


ConceptsApi apiInstance = new ConceptsApi();
List<String> keywords = Arrays.asList("keywords_example"); // List<String> | an array of keywords or substrings against which to match concept names and synonyms
List<String> categories = Arrays.asList("categories_example"); // List<String> | an array set of concept categories - specified as Biolink name labels codes gene, pathway, etc. - to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms) 
Integer size = 56; // Integer | maximum number of concept entries requested by the query (default 100) 
try {
    List<BeaconConcept> result = apiInstance.getConcepts(keywords, categories, size);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConceptsApi#getConcepts");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **keywords** | [**List&lt;String&gt;**](String.md)| an array of keywords or substrings against which to match concept names and synonyms |
 **categories** | [**List&lt;String&gt;**](String.md)| an array set of concept categories - specified as Biolink name labels codes gene, pathway, etc. - to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms)  | [optional]
 **size** | **Integer**| maximum number of concept entries requested by the query (default 100)  | [optional]

### Return type

[**List&lt;BeaconConcept&gt;**](BeaconConcept.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getExactMatchesToConceptList"></a>
# **getExactMatchesToConceptList**
> List&lt;ExactMatchResponse&gt; getExactMatchesToConceptList(c)



Given an input array of [CURIE](https://www.w3.org/TR/curie/) identifiers of known exactly matched concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch), retrieves the list of [CURIE](https://www.w3.org/TR/curie/) identifiers of additional concepts that are deemed by the given knowledge source to be exact matches to one or more of the input concepts **plus** whichever concept identifiers from the input list were specifically matched to  these additional concepts, thus giving the whole known set of equivalent concepts known to this particular knowledge source.  If an empty set is  returned, the it can be assumed that the given knowledge source does  not know of any new equivalent concepts matching the input set. The caller of this endpoint can then decide whether or not to treat  its input identifiers as its own equivalent set. 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.ConceptsApi;


ConceptsApi apiInstance = new ConceptsApi();
List<String> c = Arrays.asList("c_example"); // List<String> | an array set of [CURIE-encoded](https://www.w3.org/TR/curie/)  identifiers of concepts thought to be exactly matching concepts, to be used in a search for additional exactly matching concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). 
try {
    List<ExactMatchResponse> result = apiInstance.getExactMatchesToConceptList(c);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConceptsApi#getExactMatchesToConceptList");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **c** | [**List&lt;String&gt;**](String.md)| an array set of [CURIE-encoded](https://www.w3.org/TR/curie/)  identifiers of concepts thought to be exactly matching concepts, to be used in a search for additional exactly matching concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch).  |

### Return type

[**List&lt;ExactMatchResponse&gt;**](ExactMatchResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

