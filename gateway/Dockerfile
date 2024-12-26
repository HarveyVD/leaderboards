FROM maven:3.9.9-amazoncorretto-17-alpine AS build
WORKDIR /opt/src
COPY pom.xml .
RUN mvn verify clean --fail-never
COPY src src
RUN mvn clean package -Dmaven.test.skip=true

FROM amazoncorretto:17-alpine-jdk
COPY --from=build /opt/src/target/gateway-0.0.1-SNAPSHOT.jar /opt/app.jar
ENTRYPOINT ["java","-jar","/opt/app.jar"]

