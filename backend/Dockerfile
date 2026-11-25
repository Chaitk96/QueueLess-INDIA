FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY .mvn/settings.xml /tmp/maven-settings.xml
RUN mvn -s /tmp/maven-settings.xml -B dependency:go-offline
COPY src ./src
RUN mvn -s /tmp/maven-settings.xml -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/queueless-api-0.1.0.jar /app/queueless-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/queueless-api.jar"]
