
# BeaconPredicate

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **String** | CURIE-encoded identifier of predicate relation  |  [optional]
**uri** | **String** | The predicate URI which should generally resolves to the  full semantic description of the predicate relation |  [optional]
**edgeLabel** | **String** | A predicate edge label which must be taken from the minimal predicate (&#39;slot&#39;) list of the [Biolink Model](https://biolink.github.io/biolink-model).  |  [optional]
**relation** | **String** | The predicate relation, with the preferred format being a CURIE where one exists, but strings/labels acceptable. This relation  may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation in cases where the source provides more granularity  (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447)  |  [optional]
**localId** | **String** | CURIE-encoded identifier of the locally defined predicate relation. Such terms should be formally documented as mappings in the [Biolink Model](https://biolink.github.io/biolink-model)  |  [optional]
**localUri** | **String** | The predicate URI which should generally resolves  to the local predicate relation |  [optional]
**localRelation** | **String** | human readable name of the locally defined predicate relation  |  [optional]
**description** | **String** | human readable definition of predicate relation  provided by this beacon  |  [optional]
**frequency** | **Integer** | the number of statement entries using the specified predicate in the given beacon knowledge base |  [optional]



