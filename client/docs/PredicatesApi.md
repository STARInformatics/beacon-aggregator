# PredicatesApi

All URIs are relative to *https://reference-beacon.ncats.io/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getPredicates**](PredicatesApi.md#getPredicates) | **GET** /predicates | 


<a name="getPredicates"></a>
# **getPredicates**
> List&lt;InlineResponse2003&gt; getPredicates()



Get a list of predicates used in statements issued by the knowledge source 

### Example
```java
// Import classes:
//import bio.knowledge.client.ApiException;
//import bio.knowledge.client.api.PredicatesApi;


PredicatesApi apiInstance = new PredicatesApi();
try {
    List<InlineResponse2003> result = apiInstance.getPredicates();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling PredicatesApi#getPredicates");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;InlineResponse2003&gt;**](InlineResponse2003.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

