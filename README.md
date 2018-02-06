# The Beacon Aggregator

The [Knowledge Beacon Application Programming Interface ("KSAPI")] (https://github.com/NCATS-Tangerine/translator-knowledge-beacon) specifies a web services interface of a semantically enabled knowledge discovery and management workflow, for implementation on top of diverse (biomedical) data sources. 

The **KSAPI** is currently documented as a Swagger 2.0 API REST specification [1].

This project, the Knowledge Beacon Aggregator ("KBA") is a similarly specified web service on top of a web services application which provides various value added features to the Knowledge Beacon world. That is, the **KBA**:

1. Has the */beacons* endpoint that returns a *Beacon Id* indexed list of registered beacons.   Note that the *Beacon Id* is a **KBA** generated (not global) beacon identification number, a list of which can be used as an additional input parameter to other **KBA** calls when needed to constrain the scope of the API call to a specified subset of beacons.

2. Has the */errorlog* endpoint which returns a partial *Session Id* indexed log of beacon endpoint calls that were made with that *Session Id*. Note that the *Session Id* is simply a unique string provided to various **KBA** endpoint calls as a parameter, by clients calling the **KBA**.  **KBA** simply uses that string value to tag the log output from the given endpoint call for later retrieval by the */errorlog* endpoint call. 

3. Otherwise, generally supports many of the KSAPI specified endpoints but sometimes in a manner which generalizes concept identification to "cliques" (see below) and which provides a single point of entry for querying across a network of registered Knowledge Beacons which implement the **KSAPI**, aggregating the returned results into normalized collections of beacon metadata, concepts and relationships, generally indexed by *Beacon Id* source.

4. Constructs "cliques" of (CURIE formatted) equivalent concept identifiers directly harvested from beacons using */exactmatches*  **KSAPI** endpoints, plus the application of additional heuristics (such as checking if the concept names look like HGNC gene symbols, etc.).  Each clique is identified using a 'canonical' concept CURIE, which itself serves as a unified concept id specification for several endpoints returning aggregated beacon results relating to those cliques and is assigned by an ordering of precedence of CURIE name spaces (i.e. more specific universally accepted identifiers are preferred over more generic identifiers, e.g. NCBIgene Ids trump WikiData ids...).  The **KBA** also provides an endpoint */clique* which resolves concept CURIEs into a clique.

5. The **KBA** provides some facilities for **KBA** caching concepts and relationships ("knowledge subgraphs") returned, to improve query performance when concepts and relationships are revisited after their initial retrieval from the beacon network. This is, in effect, a kind of local 'blackboard' of retrieved knowledge [2].

See the **KBA** [Swagger API specification](https://kba.ncats.io/swagger-ui.html) for the full documentation of API calls and their parameters.

A [reference NCATS production deployment of the KBA](https://kba.ncats.io) is deployed online.

# Configuration of a (Local) Installation of KBA

The software can also be locally installed and configured to access a given site's own customized registry of beacons and other site-specific parameters.  See the ~/server/src/main/resources/application.properties file for possible customizations (for context path, port and beacon list file)

The registry of beacons used by KBA are currently specified as an external YAML file URI. An NCATS reference list of beacons is provided [here](https://github.com/NCATS-Tangerine/translator-knowledge-beacon/blob/develop/api/knowledge-beacon-list.yaml) but users may substitute their own local YAML file, as long as the same YAML field names are properly populated with beacon metadata (and active beacons tagged as Status: 'deployed')

# Footnotes

[1] The API may eventually be specified in OpenAPI 3.0 (or SmartAPI).

[2] The /statements endpoint still only returns direct first degree 'subject-predicate-object' relationships, but future iterations of the KBA may provide query facilities for the traversal of extended paths through cached knowledge subgraphs across multiple sequential edges and node.
