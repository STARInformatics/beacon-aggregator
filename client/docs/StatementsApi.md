# StatementsApi

All URIs are relative to *https://reference-beacon.ncats.io/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getStatements**](StatementsApi.md#getStatements) | **GET** /statements | 


<a name="getStatements"></a>
# **getStatements**
> List&lt;BeaconStatement&gt; getStatements(s, relations, t, keywords, semanticGroups, pageNumber, pageSize)



Given a specified set of [CURIE-encoded](https://www.w3.org/TR/curie/)  &#39;source&#39; (&#39;s&#39;) concept identifiers,  retrieves a paged list of relationship statements where either the subject or object concept matches any of the input &#39;source&#39; concepts provided.  Optionally, a set of &#39;target&#39; (&#39;t&#39;) concept  identifiers may also be given, in which case a member of the &#39;target&#39; identifier set should match the concept opposing the &#39;source&#39; in the  statement, that is, if the&#39;source&#39; matches a subject, then the  &#39;target&#39; should match the object of a given statement (or vice versa). 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.StatementsApi;


StatementsApi apiInstance = new StatementsApi();
List<String> s = Arrays.asList("s_example"); // List<String> | a set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  'source' concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure). 
String relations = "relations_example"; // String | a (url-encoded, space-delimited) string of predicate relation identifiers with which to constrain the statement relations retrieved  for the given query seed concept. The predicate ids sent should  be as published by the beacon-aggregator by the /predicates API endpoint. 
List<String> t = Arrays.asList("t_example"); // List<String> | (optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/)  identifiers of 'target' concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure). 
String keywords = "keywords_example"; // String | a (url-encoded, space-delimited) string of keywords or substrings against which to match the subject, predicate or object names of the set of concept-relations matched by any of the input exact matching concepts 
String semanticGroups = "semanticGroups_example"; // String | a (url-encoded, space-delimited) string of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain the subject or object concepts associated with the query seed concept (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes) 
Integer pageNumber = 56; // Integer | (1-based) number of the page to be returned in a paged set of query results 
Integer pageSize = 56; // Integer | number of concepts per page to be returned in a paged set of query results 
try {
    List<BeaconStatement> result = apiInstance.getStatements(s, relations, t, keywords, semanticGroups, pageNumber, pageSize);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling StatementsApi#getStatements");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **s** | [**List&lt;String&gt;**](String.md)| a set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  &#39;source&#39; concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure).  |
 **relations** | **String**| a (url-encoded, space-delimited) string of predicate relation identifiers with which to constrain the statement relations retrieved  for the given query seed concept. The predicate ids sent should  be as published by the beacon-aggregator by the /predicates API endpoint.  | [optional]
 **t** | [**List&lt;String&gt;**](String.md)| (optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/)  identifiers of &#39;target&#39; concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure).  | [optional]
 **keywords** | **String**| a (url-encoded, space-delimited) string of keywords or substrings against which to match the subject, predicate or object names of the set of concept-relations matched by any of the input exact matching concepts  | [optional]
 **semanticGroups** | **String**| a (url-encoded, space-delimited) string of semantic groups (specified as codes CHEM, GENE, ANAT, etc.) to which to constrain the subject or object concepts associated with the query seed concept (see [Semantic Groups](https://metamap.nlm.nih.gov/Docs/SemGroups_2013.txt) for the full list of codes)  | [optional]
 **pageNumber** | **Integer**| (1-based) number of the page to be returned in a paged set of query results  | [optional]
 **pageSize** | **Integer**| number of concepts per page to be returned in a paged set of query results  | [optional]

### Return type

[**List&lt;BeaconStatement&gt;**](BeaconStatement.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

