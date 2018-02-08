FROM openjdk:8-jdk-alpine
MAINTAINER Richard Bruskiewich <richard@starinformatics.com>
USER root
COPY ./server/build/libs/beacon-aggregator-*.jar /home/beacon-aggregator.jar
COPY ./test-beacon-list.yaml /home/test-beacon-list.yaml
ENTRYPOINT ["java", "-jar", "/home/beacon-aggregator.jar"]
