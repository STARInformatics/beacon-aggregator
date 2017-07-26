FROM openjdk:8

RUN wget -q https://services.gradle.org/distributions/gradle-3.4.1-bin.zip && \
    unzip gradle-3.4.1-bin.zip -d /opt && \
    rm gradle-3.4.1-bin.zip && \
    mkdir /home/beacon-aggregator

ENV PATH $PATH:/opt/gradle-3.4.1/bin/

COPY . /home/beacon-aggregator/

RUN cd home/beacon-aggregator && \
    gradle clean -x test && \
    gradle build -x test

WORKDIR /home/beacon-aggregator

ENTRYPOINT ["java", "-jar", "build/libs/beacon-aggregator-*.jar"]