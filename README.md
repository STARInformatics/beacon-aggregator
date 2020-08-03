# The Knowledge Beacon Aggregator

Try it!

 * Knowledge Beacon Aggregator OpenAPI: https://kba.ncats.io

## About

The [Knowledge Source Application Programming Interface ("KSAPI")](https://github.com/NCATS-Tangerine/translator-knowledge-beacon) specifies a web services interface of a semantically enabled knowledge discovery and management workflow, for implementation on top of diverse (biomedical) data sources. 

The **KSAPI** is currently documented as a Swagger 2.0 API REST specification [1].

This project, the [Knowledge Beacon Aggregator ("KBA")]() is similarly specified as a Swagger 2.0 web service API on top of a web services application which provides various value added features to the [Knowledge Beacon](https://github.com/NCATS-Tangerine/translator-knowledge-beacon) world. More specifically, the **KBA**:

1. Provides a single web source point of entry for querying across a network of registered Knowledge Beacons which implement the **KSAPI** and which support Knowledge Graph building standards such as the Biolink Model concept types and predicates.

2. Supports most of the **KSAPI** specified end points but in a manner which generalizes concept identification to "cliques" (see below) and which aggregates the returned results into normalized collections of beacon metadata, concepts and relationships, generally indexed by *Beacon Id* source (see diagram here below and also, item 5).

![Knowledge Beacon Aggregator Application Programming Interface](https://github.com/NCATS-Tangerine/beacon-aggregator/blob/develop/docs/KBA_API_Workflow.png "Knowledge Beacon Aggregator API Workflow")

3. Has the */beacons* endpoint that returns a *Beacon Id* indexed list of registered beacons.   Note that the *Beacon Id* is a **KBA** generated (not global) beacon identification number, a list of which can be used as an additional input parameter to other **KBA** calls when needed to constrain the scope of the API call to a specified subset of beacons.

4. Has the */errorlog* endpoint which returns a partial *Query Id* indexed log of beacon endpoint calls that were made with that *Query Id*. Note that the *Session Id* is simply a unique string provided to various **KBA** endpoint calls as a parameter, by clients calling the **KBA**.  **KBA** simply uses that string value to tag the log output from the given endpoint call for later retrieval by the */errorlog* endpoint call. 

5. Constructs "cliques" of (CURIE formatted) equivalent concept identifiers directly harvested from beacons using */exactmatches*  **KSAPI** endpoints, plus the application of additional heuristics (such as checking if the concept names look like HGNC gene symbols, etc.).  Each clique is identified using a 'canonical' concept CURIE, which itself serves as a unified concept id specification for several endpoints returning aggregated beacon results relating to those cliques and is assigned by an ordering of precedence of CURIE name spaces (i.e. more specific universally accepted identifiers are preferred over more generic identifiers, e.g. NCBIgene Ids trump WikiData ids...).  The **KBA** also provides an endpoint */clique* which resolves concept CURIEs into a clique.

6. The **KBA** provides some facilities for **KBA** caching concepts and relationships ("knowledge subgraphs") returned, to improve query performance when concepts and relationships are revisited after their initial retrieval from the beacon network. This is, in effect, a kind of local 'blackboard' of retrieved knowledge [2].

7. The latest version of KBA manages the /concepts, /cliques, and /statements endpoints as [asychronous queries](https://github.com/NCATS-Tangerine/beacon-aggregator/issues/33) with a three step process: 1) posting query parameters, 2) checking query status and 3) retrieving data when available.
The UML-like Sequence diagram here below illustrates the asynchrononous workflow:

![KBA Asynchronous Query Workflow](https://github.com/NCATS-Tangerine/beacon-aggregator/blob/develop/docs/KBA_Asynch_Workflow.png "Knowledge Beacon Aggregator /concepts and /statements Asychronous Query Workflow")

See the **KBA** [Swagger API specification](https://kba.ncats.io/swagger-ui.html) for the full documentation of API calls and their parameters.

A [reference NCATS production deployment of the KBA](https://kba.ncats.io) is deployed online.

# Cloning and Configuring a (Local) Installation of KBA

## First Decision: Where and how will you run KBA

The following installation instructions assume a Linux operating system as the target operating environment for KBA. Beyond that, core configuration instructions are applicable for any suitable recent-release Linux system. There are several options for running the turnkey.

The first decision you need to make is where (on what Linux server) to run the application. Your choices generally are:

1) Directly on a Linux "bare metal" server
 
2) Within a suitably configured Linux Virtual Machine (e.g. VMWare, Parallels, VirtualBox, Amazon Web Services, OpenStack etc.)

Your choice of Linux operating system is not too critical except that the specific details on how to configure the system may differ between Linux flavors. For the moment, we are working with Ubuntu 16.04.

## Getting the Software - Clone the Repository

You will need to git clone this project and all submodules onto your machine in order to set up a local instance of KBA. You need to decide where to clone it. A convenient recommended location for hosting your code is the folder location **/opt/kba** (if you decide otherwise, modify the configuration instructions below to suit your needs).

To start, you need to create your hosting folder location and properly set its access permissions to your user account, i.e.

```
$ sudo mkdir -p /opt/kba

# Substitute your actual Linux group and username for mygroup and myusername below 
$ sudo chown mygroup:myusername /opt/kba 
```

Next, ensure that you have a recent version of git installed.

```
$ git --version
The program 'git' is currently not installed. You can install it by typing:
sudo apt install git
```

Oops! Better install git first!

```
$ sudo apt install git  # note: some Linux flavors use 'yum' not 'apt' to install software
```

For git cloning of the code, you have two Github access options (see the github doc links provided for configuration details):

1. [Configure, connect and clone the project using HTTPS](https://help.github.com/articles/cloning-a-repository/)
2. [Configure, connect and clone the project using SSH](https://help.github.com/articles/connecting-to-github-with-ssh/)

Once you have configured your selected access option, then you do the following:

```
# First, set your directory to your hosting folder location
$ cd /opt/kba

# Then, either clone project using HTTPS or...
$ git clone https://github.com/NCATS-Tangerine/beacon-aggregator.git

# ... clone the project with SSH
$ git clone git@github.com:NCATS-Tangerine/beacon-aggregator.git

```
The software can now be configured to access a given site's own 
customized registry of beacons and other site-specific parameters.  

## Dependencies ##

### Ontology submodule

The 'beacon-aggregator' project is currently composed of [this root project](https://github.com/NCATS-Tangerine/beacon-aggregator) containing some top level resources, a separate *ontology* submodule linked to the [beacon-ontology repository](https://github.com/NCATS-Tangerine/beacon-ontology), and a set of [Docker](https://www.docker.com) container 'compose' directives.

After git cloning the code base (i.e. into **/opt/kba/beacon-aggregator**), you need to ensure that the submodules are initialized as well, as follows:

```
$ cd /opt/kba/beacon-aggregator

# Initialize the submodule(s). This command should also 
# checkout the current relevant code for each submodule
$ git submodule update --recursive --init
```

### Neo4j Database

The KBA uses the [Neo4j database](https://neo4j.com/) which serves as a "cache" for concepts and relationships (a.k.a. "knowledge subgraphs") harvested from its registered Knowledge Beacon Network. You will need to install Neo4j release 3.3.4 and configure KBA to point to local instance, which should already be running before you start up the KBA applcation.  Alternately, KBA may be run in Docker (see below) using *docker-compose*, which sets up its own Neo4j instance in its 'database' container.
 
# Building KBA

The KBA project is written in the Java computing language as a Gradle build. Thus, you first need to make sure that you've [installed the Gradle Build Tool](https://gradle.org/install/). Note that the project currently expects to use the release 4.6 or later version of Gradle.

## 1. Configuring the Build

The first task that needs to be done before building the code is configuration. To protect sensitive settings from becoming accidentally visible, these are given as templates that must be copied. It is set up so that git ignores them and won't push these copied configurations if you update the code.

If you are in the directory in which the project code for beacon-aggregator was cloned (i.e. /opt/kba/beacon-aggregator), change your directory to the resources file of the server subproject (opt/kba/beacon-aggregator/server/src/main/resources), then copy over the **applications.properties-template** and **ogm.properties-template** files into **applications.properties** and **ogm.properties** (just remove the "-template" part of the file name):

```
# Move to the directory where configuration is located
$ cd /opt/kba/beacon-aggregator/server/src/main/resources/

# While copying application.properties-template into the same location, remove the suffix
$ cp application.properties-template application.properties

# Similarly for the other configurations...
$ cp ogm.properties-template ogm.properties
```
Once these two properties file are created, open them with your favorite text editor and review their contents to set the properties for possible customization to your site conditions and how you plan to run the software (outside or inside docker, with or without pointing to the official registry of beacons or a local beacon list.  Some needed configurations will be explained when we run the Docker build).

The registry of beacons used by KBA are currently specified as an external YAML file which the location of which is specified within the by the *beacon-yaml-list* property in the **applications.properties** file. If you are happy to use the standard NCATS reference list of beacons, point this property to [here](https://raw.githubusercontent.com/NCATS-Tangerine/translator-knowledge-beacon/develop/api/knowledge-beacon-list.yaml).  However, you may substitute your own local YAML file, as long as the same YAML 
field names are properly populated with beacon metadata (and active beacons tagged as Status: 'deployed').

### Building for Docker Containers

Note that in the case of a "Docker" container deployment (see below), if you wish to point to the local *test-beacon-list.yaml* file, 
since the Dockerfile copies this file to */home/test-beacon-list.yaml* in the Docker container, you need to set 
the *beacon-yaml-list* parameter as follows:

```
beacon-yaml-list=file:///home/test-beacon-list.yaml
```
In addition, the Docker container expects to see a system file **.env** in the root directory (Note: this file is normally only used when a Dockerized KBA is running as a system daemon, but it needs to be present for the docker-compose build to work). 

There is a **dot.env-template** which can be copied into **.env** and customized to the same Neo4j user name and password as specified in the *server/src/main/resources/ogm.properties* file.

Remember to run a fresh 'gradle build' (see below) after any changes are made to property, *.env*, and other configuration files.

## 2. Building the Code

The project is configured to be built using the Gradle build tool which should be installed on your target machine as per the official [Gradle software web site](https://gradle.org/). The project assumes usage of the release 4.6 or better. After setting your Java properties noted above, the software itself may be built using the Gradle build tool:

```
$ cd /opt/kba/beacon-aggregator
$ gradle clean build --refresh-dependencies
```

Use of the *--refresh-dependencies* flag is recommended to ensure that the software class path is properly updated and required dependencies are properly downloaded for the build.

Once the Java build succeeds, KBA may be run directly or built into a Docker container for execution.

Note that every time you git pull a fresh release of the code, it is advisable to also update the submodules, in case this part of the code tree has been updated.

```
$ git submodule update --recursive
```

Note that the --init flag is NOT needed for such updating, after the original cloning of the code.

# Directly Running KBA

Once built with gradle (see above), ensure KBA is pointing to your local Neo4j instance by pointing the 
**ogm.properties** file to your local database like so:

```
#
# Local Neo4j Instance (outside docker)
#
URI=http://neo4j:<password>@localhost:7474
```

Where *<password>* is your chosen Neo4j password (can be set using the Neo4j web client the first time the 
database is fired up and accessed using the client).

KBA may be directly run from within your IDE (e.g. from within Eclipse or IdeaJ) or from the command line:
 
 1. **Run .. As Java Application**: the Swagger2SpringBoot class in the *server* subproject inside the 
 *bio.knowledge.server package* (server/src/main/java/bio/knowledge/server/Swagger2SpringBoot.java)
 
 or
 
 2. **java -jar <path/to/jar/file>**: where the JAR file is the one located inside the server subproject 
 in the *build/libs* folder called something like *beacon-aggregator-#.#.#* where *#.#.#* is the release 
 number of the application (e.g. 1.0.11)
 
# Docker Deployment of KBA

KBA is typically run within a **Docker** container when the application is run on a Linux server or 
virtual machine. Some preparation is required.

The following steps also assume that you have already run the *gradle clean build* on the project (see above) 
from within the */opt/kba/beacon-aggregator* directory of your server) to generate the requisite 
JAR file for Docker to use.

## Installation of Docker

To run Docker, you'll obviously need to [install Docker first](https://docs.docker.com/engine/installation/) 
in your target Linux operating environment (bare metal server or virtual machine running Linux).

For our installations, we typically use Ubuntu Linux, for which there is an 
[Ubuntu-specific docker installation using the repository](https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/#install-using-the-repository).

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

You will then also need to [install Docker Compose](https://docs.docker.com/compose/install/) alongside Docker 
on your target Linux operating environment.

Note that under Ubuntu, you need to run docker (and docker-compose) as 'sudo'. 

## Testing Docker Compose

In order to ensure Docker Compose is working correctly, issue the following command:
```
$ docker-compose --version
docker-compose version 1.18.0, build 8dd22a9
```
Note that your particular version and build number may be different than what is shown here. We don't currently 
expect that docker-compose version differences should have a significant impact on the build, but if in doubt, 
refer to the release notes of the docker-compose site for advice.

## Running the KBA Docker Compose build

## Configuring and Building your Docker Containers

Simply cloning the project,  installing Docker and applying the above 
configuration details does not automatically build Docker images to run. 
Rather, you need to explicitly create them using a suitable docker-compose.yml 
specification.  A default file which you can copy then customize is provided 
in the home subdirectory. 

This default docker-compose.yml file expects that an environment variable 
*NEO4J_AUTH* is set to the same Neo4j username/password credentials,
that are set in the KBA *server/src/main/resources* **ogm.properties file**. Note: to run
KBA as a system daemon, one must also also set this variable inside of the **.env** file.

Note that you should not normally have any local Neo4j database running when running your Docker instance
since the Docker Compose specification redirects internal ports for external access (so you can see
your Neo4j instance through an external web client) thus port contention may result. 
If you need to run an external Neo4j database instance alongside your docker version, then you need
to make a copy of the docker-compose.yml file and change the port redirections to non-contentious ports,
and perform your Docker build (below) using the modified file.

Finally, it is important to note that the *docker-compose.yml* file points to a host system
directory for the Neo4j database (i.e. ${HOME}/neo4j) external to the Docker instance.
Depending on how those directories were accessed in the past (e.g. via 'sudo' perhaps), the
file access settings may be too restrictive (i.e. 'root-only' access). You should change the
ownership or file access settings to the host user account under which you run the docker-compose.
(**TODO: not sure how to get this to properly work, so we us the internal Neo4j defaults folders for now
by commenting out the blackboard database volume mappings; the problem with this is that the cache database is not 
persisted in between docker container sessions).**

When running KBA ('aggregator' service) and its Neo4j database ("blackboard' service) database using 
the *docker-compose.yml* configuration of docker containers, note that the resulting containers communicate 
with one another over a private Docker 'default' bridge network. The practical consequence is that the 
URL parameter, in the KBA server *ogm.properties file*, should be set as follows:

```
URI=http://neo4j:<password>@blackboard:7474
```

where '<password'> is your Neo4j password.

Assuming that you have completed the above configuration, then you can 
run the following command from within the project directory on your 
Linux matchine:

```
 $ sudo docker-compose build
```

This command make take some time to execute, as it is downloading 
and build your docker containers.

If you wish to customize your docker images, then you can create an overlay
docker-compose-mysite.yml file and use it to override the default 
configuration file during the build, as follows:

```
 $ sudo docker-compose -f docker-compose.yaml -f /path/to/my/docker-compose-mysite.yaml build
```
# Running the KBA Docker Container

Running a KBA Docker container directly using Docker Compose is as simple as the following command:

```
# Start up all the services --detached in the background
$ sudo docker-compose up -d

# or if you have some override Docker Compose parameters...
$ sudo docker-compose -f docker-compose.yaml -f /path/to/my/docker-compose-mysite.yaml up
```

You should now see KBA running in your web browser at **http://localhost:8080** (note that you can override 
this port mapping in an override or subsitute copy of the docker-compose.yml file)

To turn the KBA Docker container off, type the following:

```
$ sudo docker-compose down

# or if you have some override Docker Compose parameters...
$ sudo docker-compose -f docker-compose.yaml -f /path/to/my/docker-compose-mysite.yaml down
```

# Configuring systemd

In order to have a KBA Docker container run automatically restart when the machine reboots,
a *kba.service* systemd service file needs to be set up on the Linux machine running Docker. 

Note that the *kba.service* file assumes that the turnkey code is located under the 
**/opt/kba/beacon-aggregator** subdirectory.

This path should be fixed or a symbolic link made to the real location of the code on the target system.

Moreover, the **/opt/kba/beacon-aggregator** directory is set as the working directory. Thus, you should generally
make a copy of the *dot.env-template* file into a file named **.env** to set the *NEO4J_AUTH* 
Neo4j database credentials, as mentioned in the section *Configuring and Building your Docker Containers* above.

```
# symbolic link (if necessary) to your local git clone directory for the beacon aggregator code
# The following assumes that your GIT clone of the beacon-aggregator repository
# is in ~ubuntu/beacon-aggregator. If not, replace ~ubuntu/beacon-aggregator with 
# the path where you git cloned the KBA repository.

sudo ln -s ~ubuntu/beacon-aggregator /opt/kba/beacon-aggregator
cd /opt/kba/beacon-aggregator
sudo cp systemd/kba.service /etc/systemd/system/kba.service
sudo systemctl daemon-reload
sudo systemctl enable docker
sudo systemctl enable kba
```
# Troubleshooting

If things don't run the first time, here are some tips about getting the application to work (some repetitive):

1. Make sure that you have the latest software updates: Java 8, Gradle 4.6 or better, Neo4j 3.3.5 or better.

2. Make sure that you get the latest code and refresh git modules after software updates:

```
$ git submodule update --recursive
```

3. Make sure that you copy Java properties from the template files and customized in the server/sr/main/resources. 
Set the *beacon-yaml-list* property in applications.properties, pointing to a valid file path (double check the TCP 
Schema: HTTP versus HTTPS resources); Set the Neo4j credentials in ogm.properties (and in .env, if you use Docker).  
Make sure that you copy the ogm.properties into database/src/test resources. 

4. After updates and properties setting, run a:

```
$ gradle clean build --recursive
```

before rebuilding the Docker image.

5. Rebuild the Docker images after building the code!

6. Docker treatment of Linux user id's (UIDs) is a bit esoteric. Typically, when a docker (compose) is run (i.e. 'up' 
directive is issued to start the application), the Docker container may not know what UID to use to access host volumes 
mapped into the container. Namely, in the KBA docker-compose.yml file, you will see the following directives for 
the Neo4j 'blackboard' service:

```
        volumes:
            # NOTE: if docker-compose is run under 'sudo' then $HOME will be 'root'
            - $HOME/neo4j/data:/data
            - $HOME/neo4j/import:/import
            - $HOME/neo4j/logs:/logs
```

Check the UID ownership of $HOME/neo4j, its subdirectories and files.  If the user UID is not "1000", then consider 
making your own copy of the docker-compose.yml file OR making an 'override' yml file, then changing the UID to the 
same UID as the user account that owns *$HOME/neo4j/* file structure. For example, if the account UID is, say, "1200', 
you could could create the following override.yml:

```
 blackboard:
        user: "1200"

```

then run docker build and application execution as follows:

```
$ sudo docker-compose -f docker-compose.yaml -f override.yaml build
$ sudo docker-compose -f docker-compose.yaml -f override.yaml up

```
This will ensure that the 'blackboard' nea4j successfully accesses, creates and/or modifies its files.

7. Another potential stumbling block for accessibility with Neo4j are the database credentials. With Docker, this 
can be problematic. The docker-compose.yml file does specify a NEO4J_AUTH environment parameter for this. The default 
credentials are 'neo4j/password', but this may be overidden. In principle, there should be some way to set NEO4J_AUTH 
exterior to the Docker process. An 'override.yml' file may also be one way to achieve this (TODO: we still need to 
figure out the most robust method to ensure this NEO4J_AUTH).

8. When using docker-compose.yml, make sure that your ogm.properties URL points to the http://blackboard:7474 
for Neo4j access.

# Footnotes

[1] The API may eventually be specified in OpenAPI 3.0 (or SmartAPI). This has not yet been done since KBA relies on 
Swagger CodeGen to keep the code base mapped to the API (OpenAPI code generation is still a bit immature as the time 
we are recording this note).

[2] The /statements endpoint still only returns direct first degree 'subject-predicate-object' relationships, but 
future iterations of the KBA may provide query facilities for the traversal of extended paths through cached knowledge 
subgraphs across multiple sequential edges and node.
