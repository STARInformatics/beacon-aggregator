FROM openjdk:8-jdk-alpine
MAINTAINER Richard Bruskiewich <richard@starinformatics.com>
USER root
COPY ./server/build/libs/beacon-aggregator-*.jar /home/beacon-aggregator.jar
COPY ./local-beacon-list.yaml /home/local-beacon-list.yaml
COPY ./.env /home/.env
RUN apk add --no-cache bash
ENTRYPOINT ["java", "-jar", "/home/beacon-aggregator.jar"]
