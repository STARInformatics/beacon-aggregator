# MetadataApi

All URIs are relative to *https://rkb.ncats.io/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getConceptCategories**](MetadataApi.md#getConceptCategories) | **GET** /categories | 
[**getKnowledgeMap**](MetadataApi.md#getKnowledgeMap) | **GET** /kmap | 
[**getPredicates**](MetadataApi.md#getPredicates) | **GET** /predicates | 


<a name="getConceptCategories"></a>
# **getConceptCategories**
> List&lt;BeaconConceptCategory&gt; getConceptCategories()



Get a list of concept categories and number of their concept instances documented by the knowledge source. These types should be mapped onto the Translator-endorsed Biolink Model concept type classes with local types, explicitly added as mappings to the Biolink Model YAML file.  

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.MetadataApi;


MetadataApi apiInstance = new MetadataApi();
try {
    List<BeaconConceptCategory> result = apiInstance.getConceptCategories();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling MetadataApi#getConceptCategories");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;BeaconConceptCategory&gt;**](BeaconConceptCategory.md)

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

