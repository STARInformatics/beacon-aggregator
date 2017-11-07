FROM ubuntu:16.04
MAINTAINER Richard Bruskiewich <richard@starinformatics.com>
USER root
RUN apt-get -y update
RUN apt-get -y install default-jre
COPY ./server/build/libs/beacon-aggregator-*.jar /home/beacon-aggregator.jar
COPY ./test-beacon-list.yaml /home/test-beacon-list.yaml
ENTRYPOINT ["java", "-jar", "/home/beacon-aggregator.jar"]
