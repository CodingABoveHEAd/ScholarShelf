# Dockerfile --> build one container image. It defines what’s inside the container and how to run it.

# -------- Build stage --------
FROM gradle:8.5-jdk17 AS builder
 #--all subsequent command inside /app
WORKDIR /app  

COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon

# -------- Run stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]