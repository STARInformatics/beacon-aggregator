# StatementsApi

All URIs are relative to *https://localhost/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getStatementDetails**](StatementsApi.md#getStatementDetails) | **GET** /statements/{statementId} | 
[**getStatements**](StatementsApi.md#getStatements) | **GET** /statements | 


<a name="getStatementDetails"></a>
# **getStatementDetails**
> BeaconStatementWithDetails getStatementDetails(statementId, keywords, size)



Retrieves a details relating to a specified concept-relationship statement include &#39;is_defined_by and &#39;provided_by&#39; provenance; extended edge properties exported as tag &#x3D; value; and any associated annotations (publications, etc.)  cited as evidence for the given statement. 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.StatementsApi;


StatementsApi apiInstance = new StatementsApi();
String statementId = "statementId_example"; // String | (url-encoded) CURIE identifier of the concept-relationship statement (\"assertion\", \"claim\") for which associated evidence is sought 
List<String> keywords = Arrays.asList("keywords_example"); // List<String> | an array of keywords or substrings against which to  filter annotation names (e.g. publication titles).
Integer size = 56; // Integer | maximum number of concept entries requested by the client; if this argument is omitted, then the query is expected to returned all  the available data for the query 
try {
    BeaconStatementWithDetails result = apiInstance.getStatementDetails(statementId, keywords, size);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling StatementsApi#getStatementDetails");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **statementId** | **String**| (url-encoded) CURIE identifier of the concept-relationship statement (\&quot;assertion\&quot;, \&quot;claim\&quot;) for which associated evidence is sought  |
 **keywords** | [**List&lt;String&gt;**](String.md)| an array of keywords or substrings against which to  filter annotation names (e.g. publication titles). | [optional]
 **size** | **Integer**| maximum number of concept entries requested by the client; if this argument is omitted, then the query is expected to returned all  the available data for the query  | [optional]

### Return type

[**BeaconStatementWithDetails**](BeaconStatementWithDetails.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStatements"></a>
# **getStatements**
> List&lt;BeaconStatement&gt; getStatements(s, edgeLabel, relation, t, keywords, categories, size)



Given a specified set of [CURIE-encoded](https://www.w3.org/TR/curie/) source (&#39;s&#39;) concept identifiers,  retrieves a list of relationship statements where either the subject or object concept matches any of the input &#39;source&#39; concepts provided.  Optionally, a set of target (&#39;t&#39;) concept  identifiers may also be given, in which case a member of the &#39;target&#39; identifier set should match the concept opposing the &#39;source&#39; in the  statement, that is, if the&#39;source&#39; matches a subject, then the &#39;target&#39; should match the object of a given statement (or vice versa). 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.StatementsApi;


StatementsApi apiInstance = new StatementsApi();
List<String> s = Arrays.asList("s_example"); // List<String> | an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  'source' concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure). 
String edgeLabel = "edgeLabel_example"; // String | (Optional) A predicate edge label against which to constrain the search for statements ('edges') associated with the given query seed concept. The predicate edge_names for this parameter should be as published by the /predicates API endpoint and must be taken from the minimal predicate ('slot') list of the [Biolink Model](https://biolink.github.io/biolink-model). 
String relation = "relation_example"; // String | (Optional) A predicate relation against which to constrain the search for statements ('edges') associated with the given query seed concept. The predicate relations for this parameter should be as published by the /predicates API endpoint and the preferred format is a CURIE  where one exists, but strings/labels acceptable. This relation may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation in cases where the source provides more granularity (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447) 
List<String> t = Arrays.asList("t_example"); // List<String> | (optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of 'target' concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure). 
List<String> keywords = Arrays.asList("keywords_example"); // List<String> | an array of keywords or substrings against which to filter concept names and synonyms
List<String> categories = Arrays.asList("categories_example"); // List<String> | an array set of concept categories (specified as Biolink name labels codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) 
Integer size = 56; // Integer | maximum number of concept entries requested by the client; if this argument is omitted, then the query is expected to returned all  the available data for the query 
try {
    List<BeaconStatement> result = apiInstance.getStatements(s, edgeLabel, relation, t, keywords, categories, size);
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
 **edgeLabel** | **String**| (Optional) A predicate edge label against which to constrain the search for statements (&#39;edges&#39;) associated with the given query seed concept. The predicate edge_names for this parameter should be as published by the /predicates API endpoint and must be taken from the minimal predicate (&#39;slot&#39;) list of the [Biolink Model](https://biolink.github.io/biolink-model).  | [optional]
 **relation** | **String**| (Optional) A predicate relation against which to constrain the search for statements (&#39;edges&#39;) associated with the given query seed concept. The predicate relations for this parameter should be as published by the /predicates API endpoint and the preferred format is a CURIE  where one exists, but strings/labels acceptable. This relation may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation in cases where the source provides more granularity (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447)  | [optional]
 **t** | [**List&lt;String&gt;**](String.md)| (optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of &#39;target&#39; concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure).  | [optional]
 **keywords** | [**List&lt;String&gt;**](String.md)| an array of keywords or substrings against which to filter concept names and synonyms | [optional]
 **categories** | [**List&lt;String&gt;**](String.md)| an array set of concept categories (specified as Biolink name labels codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes)  | [optional]
 **size** | **Integer**| maximum number of concept entries requested by the client; if this argument is omitted, then the query is expected to returned all  the available data for the query  | [optional]

### Return type

[**List&lt;BeaconStatement&gt;**](BeaconStatement.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

