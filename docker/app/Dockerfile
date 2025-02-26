# Build
FROM openjdk:17-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src/ src/
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# Deploy application
FROM openjdk:17-alpine
COPY --from=builder /app/build/libs/*.jar app.jar
COPY docker/app/run.sh /run.sh

ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
ENTRYPOINT ["/run.sh"]
