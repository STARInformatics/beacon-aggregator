# beacon-aggregator #

https://kba.ncats.io

A web service that operates over the Beacon network to provide a single software interface over the all the Beacons.

# Comparison to Knowledge Beacon API #

The core of the API specified and what it does is very similar to the [Knowledge Beacon API ("KBAPI") ](https://github.com/NCATS-Tangerine/translator-knowledge-beacon) with some specific differences:

1. The Beacon Aggregator keeps an indexed catalog of beacons it knows about. This list is published in a new */beacons* endpoint. Each beacon is given a (beacon-aggregator local) index number which can be used as an additional input parameter to several other API calls when needed to constrain the scope of the API call to the given beacon. 

2. Many of the endpoints are similar to the KBAPI, but rather return the aggregate set of responses received from every beacon accessed. Each result is generally tagged by the corresponding 'beaconId'. The default behaviour is to aggregate over all known beacons unless the API call is made by explicitly specifying an array of beaconIds to which to limit the query.

3. Concepts in the system are heuristically aggregated by "exact match" heuristics into equivalent concept cliques, each of which is tagged by a given canonical clique identifier, assigned by an ordering of precedence of namespaces (i.e. more specific universally accepted identifiers are preferred over more generic identifiers, e.g. NCBIgene Ids trump WikiData ids...).

4. The concept details and statement API calls take clique identifiers as their input concepts,  rather than a local CURIE or list of CURIES. Such clique identifiers are generally resolved in the original /concepts keyword search for concepts.

5. The beacon aggregator has a concept of a user tagging the runtime logs of their transactions with the system, using a user-specified "session" identifier. This user specified sessionId needs to be provided to API calls, allowing the aggregator to tag log outputs during beacon calls, so that the user can use an new endpoint "/errorlog" to retrieve those tagged logs. 

See https://kba.ncats.io/swagger-ui.html for full documentation of API calls and their parameters.

# Configuration #

See the ~/server/src/main/resources/application.properties file for possible customizations (for context path, port and beacon list file)

The official list of beacons that are currently aggregated over is hosted [here](https://github.com/NCATS-Tangerine/translator-knowledge-beacon/blob/develop/api/knowledge-beacon-list.yaml). Upon starting, the application will download the [raw file](https://raw.githubusercontent.com/NCATS-Tangerine/translator-knowledge-beacon/develop/api/knowledge-beacon-list.yaml), and initialize knowledge beacons that are tagged with "status: deployed".

