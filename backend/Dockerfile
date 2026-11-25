FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN for attempt in 1 2 3; do \
      mvn -B -U -DskipTests package && exit 0; \
      echo "Maven dependency download failed; retrying in $((attempt * 15)) seconds..."; \
      sleep $((attempt * 15)); \
    done; \
    exit 1

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/queueless-api-0.1.0.jar /app/queueless-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/queueless-api.jar"]
