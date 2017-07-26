FROM ubuntu:16.04

RUN apt-get update

RUN apt-get -y install openjdk-8-jdk

COPY ./server/build/libs/beacon-aggregator-*.jar /home/beacon-aggregator.jar

ENTRYPOINT ["java", "-jar", "/home/beacon-aggregator.jar"]