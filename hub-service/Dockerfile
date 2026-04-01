FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY common common
COPY hub-service hub-service
RUN ./gradlew :hub-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/hub-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
