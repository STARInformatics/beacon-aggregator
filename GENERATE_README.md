# generate.sh

The 'generate.sh' script can be used to update or regenerate the beacon-aggregator 
client and server code. Type the script name without any arguments to get the usage.

Note that the API files used for the command are in the 'api' folder. The client API
specification is in the knowledge-beacon-api.yaml file, whereas the server API
specification is in the beacon-aggregator-api.yaml file. The two API's are currently
very distinct.

Note that after running the server script, you will need to fix the generated code
implementations to delegate their business logic by calling the functions in the
bio.knowledge.server.impl.Controller class of the server package. For the client
code, you mainly need to fix the functions calling the client in the KnowledgeBeaconService
of the bio.knowledge.aggregator package in the 'aggregator' subproject.

Since the application.properties file is overwritten by generate.sh, you will also
need to copy over the 'beacon-yaml-list' property from the application.properties-template
file, back into the application.properties file, for the regenerated application to work.