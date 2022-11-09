FROM maven:3.8.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml /app
COPY src /app/src
RUN mvn package

FROM eclipse-temurin:17.0.5_8-jre-alpine AS run
WORKDIR /app
COPY --from=build /app/target/*.jar /app/parking.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "parking.jar"]
