# beacon-aggregator

https://kba.ncats.io

A web service that operates over the Beacon network to provide a single software interface over the all the Beacons.

See the ~/server/src/main/resources/application.properties file for possible customizations (for context path, port and beacon list file)

The official list of beacons that are aggregated over is hosted [here](https://github.com/NCATS-Tangerine/translator-knowledge-beacon/blob/develop/api/knowledge-beacon-list.yaml). Upon starting, the application will download the [raw file](https://raw.githubusercontent.com/NCATS-Tangerine/translator-knowledge-beacon/develop/api/knowledge-beacon-list.yaml), and initialize knowledge beacons that are tagged with "status: deployed".

See https://kba.ncats.io/swagger-ui.html for documentation of API calls and their parameters.
