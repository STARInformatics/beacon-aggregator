#!/bin/sh
# This script attempts to download swagger-codegen-cli.jar and then use
# it to generate a SpringBoot server stub

usage="usage: $(basename "$0") <specification> [<output-dir>] -- program to generate a swagger server stub
\n\n
where:\n
\tspecification\t the path to a json or yaml specification file\n
\toutput-dir\t the relative location of the generated server project. If not set then it will default to ./server"

BASE_PACKAGE="bio.knowledge.server"
CONFIG_PACKAGE="bio.knowledge.server.configuration"
MODEL_PACKAGE="bio.knowledge.server.model"
API_PACKAGE="bio.knowledge.server.api"

# Get the specification file
if [ -z "$1" ]; then
	echo $usage
	exit 1
else
	SPECIFICATION_FILE_PATH="$1"
	echo "Using specification file at: "$SPECIFICATION_FILE_PATH
fi

# Get the output directory, if not provided then assume it's ./server
if [ -z "$2" ]; then
	OUTPUT_DIR="server"
else
	OUTPUT_DIR="$2"
fi

echo "Outputting server directory at: "$OUTPUT_DIR

# Attempt to download swagger-codegen-cli.jar if it doesn't already exist
if [ -f "./swagger-codegen-cli.jar" ]; then
	echo "swagger-codegen-cli.jar already downloaded\n"
else

	echo "downloading swagger-codegen-cli.jar\n"
	# wget creates a file whether or not it's able to download it or not. So if there's a download error we want to delete the file created.
	wget http://central.maven.org/maven2/io/swagger/swagger-codegen-cli/2.2.2/swagger-codegen-cli-2.2.2.jar -O swagger-codegen-cli.jar || rm -f api/swagger-codegen-cli.jar
fi

# Use swagger-codegen-cli.jar to generate a Spring server
if [ -f "./swagger-codegen-cli.jar" ]; then
	java -jar swagger-codegen-cli.jar generate -i $SPECIFICATION_FILE_PATH -l spring -o $OUTPUT_DIR --model-package $MODEL_PACKAGE --api-package $API_PACKAGE --additional-properties basePackage=$BASE_PACKAGE,configPackage=$CONFIG_PACKAGE
else

	echo "\nDownload failed! Aborting.\n"
fi

exit 0

