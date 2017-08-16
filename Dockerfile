FROM ubuntu:16.04
MAINTAINER Richard Bruskiewich <richard@starinformatics.com>
LABEL "NCATS Translator Knowledge.Bio Beacon Aggregator"

USER root

RUN apt-get -y update
RUN apt-get -y install default-jre

COPY ./server/build/libs/beacon-aggregator-*.jar /home/beacon-aggregator.jar

ENTRYPOINT ["java", "-jar", "/home/beacon-aggregator.jar"]