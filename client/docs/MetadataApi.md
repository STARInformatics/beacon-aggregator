# MetadataApi

All URIs are relative to *https://rkb.ncats.io/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getConceptTypes**](MetadataApi.md#getConceptTypes) | **GET** /types | 
[**getKnowledgeMap**](MetadataApi.md#getKnowledgeMap) | **GET** /kmap | 
[**getPredicates**](MetadataApi.md#getPredicates) | **GET** /predicates | 


<a name="getConceptTypes"></a>
# **getConceptTypes**
> List&lt;BeaconConceptType&gt; getConceptTypes()



Get a list of types and # of instances in the knowledge source, and a link to the API call for the list of equivalent terminology 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.MetadataApi;


MetadataApi apiInstance = new MetadataApi();
try {
    List<BeaconConceptType> result = apiInstance.getConceptTypes();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling MetadataApi#getConceptTypes");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;BeaconConceptType&gt;**](BeaconConceptType.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getKnowledgeMap"></a>
# **getKnowledgeMap**
> List&lt;BeaconKnowledgeMapStatement&gt; getKnowledgeMap()



Get a high level knowledge map of the all the beacons by subject semantic type, predicate and semantic object type 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.MetadataApi;


MetadataApi apiInstance = new MetadataApi();
try {
    List<BeaconKnowledgeMapStatement> result = apiInstance.getKnowledgeMap();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling MetadataApi#getKnowledgeMap");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;BeaconKnowledgeMapStatement&gt;**](BeaconKnowledgeMapStatement.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getPredicates"></a>
# **getPredicates**
> List&lt;BeaconPredicate&gt; getPredicates()



Get a list of predicates used in statements issued by the knowledge source 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.MetadataApi;


MetadataApi apiInstance = new MetadataApi();
try {
    List<BeaconPredicate> result = apiInstance.getPredicates();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling MetadataApi#getPredicates");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;BeaconPredicate&gt;**](BeaconPredicate.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

