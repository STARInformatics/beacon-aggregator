# The Beacon Aggregator

The [Knowledge Beacon Application Programming Interface ("KSAPI")](https://github.com/NCATS-Tangerine/translator-knowledge-beacon) specifies a web services interface of a semantically enabled knowledge discovery and management workflow, for implementation on top of diverse (biomedical) data sources. 

The **KSAPI** is currently documented as a Swagger 2.0 API REST specification [1].

This project, the Knowledge Beacon Aggregator ("KBA") is a similarly specified as a Swagger 2.0 web service API on top of a web services application which provides various value added features to the Knowledge Beacon world. That is, the **KBA**:

1. Provides a single web source point of entry for querying across a network of registered Knowledge Beacons which implement the **KSAPI**.

2. Supports most of the **KSAPI** specified endpoints but in a manner which generalizes concept identification to "cliques" (see below) and which aggregates the returned results into normalized collections of beacon metadata, concepts and relationships, generally indexed by *Beacon Id* source (see below).

3. Has the */beacons* endpoint that returns a *Beacon Id* indexed list of registered beacons.   Note that the *Beacon Id* is a **KBA** generated (not global) beacon identification number, a list of which can be used as an additional input parameter to other **KBA** calls when needed to constrain the scope of the API call to a specified subset of beacons.

4. Has the */errorlog* endpoint which returns a partial *Session Id* indexed log of beacon endpoint calls that were made with that *Session Id*. Note that the *Session Id* is simply a unique string provided to various **KBA** endpoint calls as a parameter, by clients calling the **KBA**.  **KBA** simply uses that string value to tag the log output from the given endpoint call for later retrieval by the */errorlog* endpoint call. 

5. Constructs "cliques" of (CURIE formatted) equivalent concept identifiers directly harvested from beacons using */exactmatches*  **KSAPI** endpoints, plus the application of additional heuristics (such as checking if the concept names look like HGNC gene symbols, etc.).  Each clique is identified using a 'canonical' concept CURIE, which itself serves as a unified concept id specification for several endpoints returning aggregated beacon results relating to those cliques and is assigned by an ordering of precedence of CURIE name spaces (i.e. more specific universally accepted identifiers are preferred over more generic identifiers, e.g. NCBIgene Ids trump WikiData ids...).  The **KBA** also provides an endpoint */clique* which resolves concept CURIEs into a clique.

6. The **KBA** provides some facilities for **KBA** caching concepts and relationships ("knowledge subgraphs") returned, to improve query performance when concepts and relationships are revisited after their initial retrieval from the beacon network. This is, in effect, a kind of local 'blackboard' of retrieved knowledge [2].

See the **KBA** [Swagger API specification](https://kba.ncats.io/swagger-ui.html) for the full documentation of API calls and their parameters.

A [reference NCATS production deployment of the KBA](https://kba.ncats.io) is deployed online.

# Configuration of a (Local) Installation of KBA

The software can also be locally cloned by Git and configured to access a given site's own 
customized registry of beacons and other site-specific parameters.  See the 
~/server/src/main/resources/application.properties file for possible customizations 
(for context path, port and beacon-yaml-list applcation properties)

The registry of beacons used by KBA are currently specified as an external YAML file URI. 
An NCATS reference list of beacons is provided [here](https://github.com/NCATS-Tangerine/translator-knowledge-beacon/blob/develop/api/knowledge-beacon-list.yaml) 
but users may substitute their own local YAML file, as long as the same YAML 
field names are properly populated with beacon metadata (and active beacons tagged as Status: 'deployed')

# Docker Deployment of KBA

KBA is typically run within a **Docker** container when the application is run on a Linux server or virtual machine. Some preparation is required.

## Installation of Docker

To run Docker, you'll obviously need to [install Docker first](https://docs.docker.com/engine/installation/) in your target Linux operating environment (bare metal server or virtual machine running Linux).

For our installations, we typically use Ubuntu Linux, for which there is an [Ubuntu-specific docker installation using the repository](https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/#install-using-the-repository).
Note that you should have 'curl' installed first before installing Docker:

```
$ sudo apt-get install curl
```

For other installations, please find instructions specific to your choice of Linux variant, on the Docker site.

## Testing Docker

In order to ensure that Docker is working correctly, run the following command:

```
$ sudo docker run hello-world
```

This should result in something akin to the following output:

```
Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
ca4f61b1923c: Pull complete
Digest: sha256:be0cd392e45be79ffeffa6b05338b98ebb16c87b255f48e297ec7f98e123905c
Status: Downloaded newer image for hello-world:latest

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (amd64)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://cloud.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/engine/userguide/
```

## Installing Docker Compose

You will then also need to [install Docker Compose](https://docs.docker.com/compose/install/) alongside Docker on your target Linux operating environment.

Note that under Ubuntu, you need to run docker (and docker-compose) as 'sudo'. 

## Testing Docker Compose

In order to ensure Docker Compose is working correctly, issue the following command:
```
$ docker-compose --version
docker-compose version 1.18.0, build 8dd22a9
```
Note that your particular version and build number may be different than what is shown here. We don't currently expect that docker-compose version differences should have a significant impact on the build, but if in doubt, refer to the release notes of the docker-compose site for advice.

## Running the KBA Docker Compose build

## Configuring and Building your Docker Containers

Simply cloning the project,  installing Docker and applying the above 
configuration details does not automatically build Docker images to run. 
Rather, you need to explicitly create them using a suitable docker-compose.yml 
specification.  A default file which you can copy then customize is provided 
in the home subdirectory. 

This default docker-compose.yml file expects that an evironment variable 
*NEO4J_AUTH* is set to the same Neo4j username/password credentials,
that are set in the KBA server ogm.properties file. Note: when 
running KBA as a system daemon, you should set this
variable inside of the .env file (see below).

Assuming that you have completed the above configuration, then you can 
run the following command from within the project directory on your 
Linux matchine:

```
 $ sudo docker-compose -f your-docker-compose.yml build
```

This command make take some time to execute, as it is downloading 
and build your docker containers.

If you wish to customize your docker images, then you can create an overlay
docker-compose-mysite.yml file and use it to override the default 
configuration file during the build, as follows:

```
 $ sudo docker-compose -f run/docker-compose.yml -f /path/to/my/docker-compose-mysite.yml build
```
## Configuring systemd

In order to have the infrastructure automatically restart when the machine reboots,
a *kba.service* systemd service file needs to be set up on the Linux machine running Docker. 

Note that the *kba.service* file assumes that the turnkey code is located under the **/opt/kba** subdirectory.
This path should be fixed or a symbolic link made to the real location of the code on the target system.

Moreover, the **/opt/kba** directory is set as the working directory. Thus, you should generally
make a copy of the *dot.env-template* file into a file named **.env** to set the *NEO4J_AUTH* 
Neo4j database credentials, as mentioned in the section *Configuring and Building your Docker Containers* above.

```
# symbolic link (if necessary) to your local git clone directory for the beacon aggregator code
# The following assumes that your GIT clone of the beacon-aggregator repository
# is in ~ubuntu/beacon-aggregator. If not, replace ~ubuntu/beacon-aggregator with 
# the path where you git cloned the KBA repository.

sudo ln -s ~ubuntu/beacon-aggregator /opt/kba
cd /opt/kba
sudo cp systemd/kba.service /etc/systemd/system/kba.service
sudo systemctl daemon-reload
sudo systemctl enable docker
sudo systemctl enable kba
```

# Footnotes

[1] The API may eventually be specified in OpenAPI 3.0 (or SmartAPI). This has not yet been done since KBA relies on Swagger CodeGen to keep the code base mapped to the API (OpenAPI code generation is still a bit immature as the time we are recording this note).

[2] The /statements endpoint still only returns direct first degree 'subject-predicate-object' relationships, but future iterations of the KBA may provide query facilities for the traversal of extended paths through cached knowledge subgraphs across multiple sequential edges and node.
