
# BeaconConceptWithDetails

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **String** | local object CURIE for the concept in the specified knowledge source database  |  [optional]
**uri** | **String** | (optional) equivalent to expansion of the CURIE  |  [optional]
**name** | **String** | canonical human readable name of the concept  |  [optional]
**symbol** | **String** | (optional) symbol, used for genomic data  |  [optional]
**categories** | **List&lt;String&gt;** | concept semantic type &#39;category&#39;. Should be specified from the [Biolink Model](https://biolink.github.io/biolink-model).  |  [optional]
**description** | **String** | (optional) narrative concept definition  |  [optional]
**synonyms** | **List&lt;String&gt;** | list of synonyms for concept  |  [optional]
**exactMatches** | **List&lt;String&gt;** | List of [CURIE](https://www.w3.org/TR/curie/)  identifiers of concepts thought to be exactly matching concepts, [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). This is generally the same list returned by the /exact_matches endpoint (given the concept &#39;id&#39; as input)  |  [optional]
**details** | [**List&lt;BeaconConceptDetail&gt;**](BeaconConceptDetail.md) |  |  [optional]



