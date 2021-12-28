# syntax=docker/dockerfile:experimental
FROM maven:3.6.3-jdk-8 AS build 
WORKDIR /usr/src/app
COPY pom.xml .

RUN --mount=type=cache,id=lemonade-serving,target=/root/.m2 mvn -f /usr/src/app/pom.xml dependency:go-offline

COPY src ./src/
RUN --mount=type=cache,id=lemonade-serving,target=/root/.m2 mvn -f /usr/src/app/pom.xml compile package

FROM openjdk:8
COPY config.yaml /usr/app/
COPY --from=build /usr/src/app/target/custom-mleap-serving-0.1.0.jar /usr/app/

# Change to the correct path. If using a filesystem, specify the URL using file://
# Supports file://, http://, https:// and hdfs:// protocols.
ENV MLEAP_MODEL=https://github.com/combust/mleap/raw/master/mleap-benchmark/src/main/resources/models/airbnb.model.lr.zip

EXPOSE 8150
ENTRYPOINT ["java","-jar","/usr/app/custom-mleap-serving-0.1.0.jar", "server", "/usr/app/config.yaml"]
