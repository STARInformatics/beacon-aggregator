# StatementsApi

All URIs are relative to *https://rkb.ncats.io/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getEvidence**](StatementsApi.md#getEvidence) | **GET** /evidence/{statementId} | 
[**getStatements**](StatementsApi.md#getStatements) | **GET** /statements | 


<a name="getEvidence"></a>
# **getEvidence**
> List&lt;BeaconAnnotation&gt; getEvidence(statementId, keywords, size)



Retrieves a (paged) list of annotations cited as evidence for a specified concept-relationship statement 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.StatementsApi;


StatementsApi apiInstance = new StatementsApi();
String statementId = "statementId_example"; // String | (url-encoded) CURIE identifier of the concept-relationship statement (\"assertion\", \"claim\") for which associated evidence is sought 
List<String> keywords = Arrays.asList("keywords_example"); // List<String> | an array of keywords or substrings against which to filter citation titles
Integer size = 56; // Integer | maximum number of cited references requested by the query (default 100) 
try {
    List<BeaconAnnotation> result = apiInstance.getEvidence(statementId, keywords, size);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling StatementsApi#getEvidence");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statementId** | **String**| (url-encoded) CURIE identifier of the concept-relationship statement (\&quot;assertion\&quot;, \&quot;claim\&quot;) for which associated evidence is sought  |
 **keywords** | [**List&lt;String&gt;**](String.md)| an array of keywords or substrings against which to filter citation titles | [optional]
 **size** | **Integer**| maximum number of cited references requested by the query (default 100)  | [optional]

### Return type

[**List&lt;BeaconAnnotation&gt;**](BeaconAnnotation.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStatements"></a>
# **getStatements**
> List&lt;BeaconStatement&gt; getStatements(s, relations, t, keywords, categories, size)



Given a specified set of [CURIE-encoded](https://www.w3.org/TR/curie/)  &#39;source&#39; (&#39;s&#39;) concept identifiers,  retrieves a paged list of relationship statements where either the subject or object concept matches any of the input &#39;source&#39; concepts provided.  Optionally, a set of &#39;target&#39; (&#39;t&#39;) concept  identifiers may also be given, in which case a member of the &#39;target&#39; identifier set should match the concept opposing the &#39;source&#39; in the  statement, that is, if the&#39;source&#39; matches a subject, then the  &#39;target&#39; should match the object of a given statement (or vice versa). 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.StatementsApi;


StatementsApi apiInstance = new StatementsApi();
List<String> s = Arrays.asList("s_example"); // List<String> | an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  'source' concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure). 
List<String> relations = Arrays.asList("relations_example"); // List<String> | an array set of strings of Biolink predicate relation name labels against which to constrain the search for statement relations associated with the given query seed concept. The predicate  relation names for this parameter should be as published by  the beacon-aggregator by the /predicates API endpoint as taken from the minimal predicate list of the Biolink Model  (see [Biolink Model](https://biolink.github.io/biolink-model)  for the full list of predicates). 
List<String> t = Arrays.asList("t_example"); // List<String> | (optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of 'target' concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure). 
List<String> keywords = Arrays.asList("keywords_example"); // List<String> | an array of keywords or substrings against which to filter concept names and synonyms
List<String> categories = Arrays.asList("categories_example"); // List<String> | an array set of concept categories (specified as Biolink name labels codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) 
Integer size = 56; // Integer | maximum number of statement entries requested by the query (default 100) 
try {
    List<BeaconStatement> result = apiInstance.getStatements(s, relations, t, keywords, categories, size);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling StatementsApi#getStatements");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **s** | [**List&lt;String&gt;**](String.md)| an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  &#39;source&#39; concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure).  |
 **relations** | [**List&lt;String&gt;**](String.md)| an array set of strings of Biolink predicate relation name labels against which to constrain the search for statement relations associated with the given query seed concept. The predicate  relation names for this parameter should be as published by  the beacon-aggregator by the /predicates API endpoint as taken from the minimal predicate list of the Biolink Model  (see [Biolink Model](https://biolink.github.io/biolink-model)  for the full list of predicates).  | [optional]
 **t** | [**List&lt;String&gt;**](String.md)| (optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of &#39;target&#39; concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure).  | [optional]
 **keywords** | [**List&lt;String&gt;**](String.md)| an array of keywords or substrings against which to filter concept names and synonyms | [optional]
 **categories** | [**List&lt;String&gt;**](String.md)| an array set of concept categories (specified as Biolink name labels codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes)  | [optional]
 **size** | **Integer**| maximum number of statement entries requested by the query (default 100)  | [optional]

### Return type

[**List&lt;BeaconStatement&gt;**](BeaconStatement.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

