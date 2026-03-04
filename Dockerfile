# -------- Build stage --------
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# 🔧 FIX: give execute permission to gradlew
RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon

# -------- Run stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]