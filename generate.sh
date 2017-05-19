#!/bin/sh
# This script attempts to download swagger-codegen-cli.jar and then use
# it to generate a SpringBoot server stub

usage="usage: $(basename "$0") <specification> -- program to generate a swagger server stub
\n\n
where:\n
\tspecification\t the path to a json or yaml specification file\n"

# Here we define the package structure of the server and client
SERVER_BASE_PACKAGE="bio.knowledge.server"
SERVER_CONFIG_PACKAGE="bio.knowledge.server.configuration"
SERVER_MODEL_PACKAGE="bio.knowledge.server.model"
SERVER_API_PACKAGE="bio.knowledge.server.api"

CLIENT_BASE_PACKAGE="bio.knowledge.client"
CLIENT_CONFIG_PACKAGE="bio.knowledge.client.configuration"
CLIENT_MODEL_PACKAGE="bio.knowledge.client.model"
CLIENT_API_PACKAGE="bio.knowledge.client.api"

SERVER_OUTPUT_DIR="server"
CLIENT_OUTPUT_DIR="client"

# Get the specification file
if [ -z "$1" ]; then
	echo $usage
	exit 1
else
	SPECIFICATION_FILE_PATH="$1"
	echo "Using specification file at: "$SPECIFICATION_FILE_PATH
fi

# Attempt to download swagger-codegen-cli.jar if it doesn't already exist
if [ -f "./swagger-codegen-cli.jar" ]; then
	echo "swagger-codegen-cli.jar already downloaded\n"
else

	echo "downloading swagger-codegen-cli.jar\n"
	# wget creates a file whether or not it's able to download it or not. So if there's a download error we want to delete the file created.
	wget http://central.maven.org/maven2/io/swagger/swagger-codegen-cli/2.2.2/swagger-codegen-cli-2.2.2.jar -O swagger-codegen-cli.jar || rm -f api/swagger-codegen-cli.jar
fi

# Use swagger-codegen-cli.jar to generate the server and client stub
if [ -f "./swagger-codegen-cli.jar" ]; then
	java -jar swagger-codegen-cli.jar generate -i $SPECIFICATION_FILE_PATH -l spring -o $SERVER_OUTPUT_DIR --model-package $SERVER_MODEL_PACKAGE --api-package $SERVER_API_PACKAGE --additional-properties basePackage=$SERVER_BASE_PACKAGE,configPackage=$SERVER_CONFIG_PACKAGE

	java -jar swagger-codegen-cli.jar generate -i $SPECIFICATION_FILE_PATH -l java -o $CLIENT_OUTPUT_DIR --model-package $CLIENT_MODEL_PACKAGE --api-package $CLIENT_API_PACKAGE --additional-properties basePackage=$CLIENT_BASE_PACKAGE,configPackage=$CLIENT_CONFIG_PACKAGE
else

	echo "\nDownload failed! Aborting.\n"
	exit 1
fi

exit 0

