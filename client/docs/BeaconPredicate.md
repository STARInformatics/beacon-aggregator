
# BeaconPredicate

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **String** | CURIE-encoded identifier of predicate relation  |  [optional]
**uri** | **String** | The predicate URI which should generally resolves to the  full semantic description of the predicate relation |  [optional]
**edgeLabel** | **String** | human readable name of a &#39;minimal&#39; Biolink model predicate relation terms (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms)  |  [optional]
**relation** | **String** | human readable name of a &#39;maximal&#39; Biolink model predicate relation terms (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms)  |  [optional]
**localId** | **String** | CURIE-encoded identifier of the locally defined predicate relation. Such terms should be formally documented as mappings in the  [Biolink Model](https://biolink.github.io/biolink-model)  |  [optional]
**localUri** | **String** | The predicate URI which should generally resolves  to the local predicate relation |  [optional]
**localRelation** | **String** | human readable name of the locally defined predicate relation  |  [optional]
**description** | **String** | human readable definition of predicate relation  provided by this beacon  |  [optional]
**frequency** | **Integer** | the number of statement entries using the specified predicate in the given beacon knowledge base |  [optional]



