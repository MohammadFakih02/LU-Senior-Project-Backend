# Dockerfile

# ---- Build Stage ----
# Use a Maven image to build the application JAR
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy the POM file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Runtime Stage ----
# Use a slim JRE image for the runtime environment
FROM openjdk:21-jre-slim
WORKDIR /app

# Environment variables that can have defaults (non-sensitive)
# Sensitive variables like passwords should be injected at runtime
ENV DB_HOST=lu-senior-project-database-mohammadfakih025-916d.l.aivencloud.com
ENV DB_PORT=12356
ENV DB_NAME=defaultdb
# SPRING_PROFILES_ACTIVE can also be set here if needed, e.g., ENV SPRING_PROFILES_ACTIVE=docker

# Copy the JAR file from the build stage
COPY --from=builder /app/target/internetprovidermanagement-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot application runs on (default is 8080)
EXPOSE 8080

# Command to run the application
# The environment variables DB_USERNAME_ENV, DB_PASSWORD_ENV, DB_KEYSTORE_PASSWORD_ENV
# will be provided when running the container.
ENTRYPOINT ["java", "-jar", "app.jar"]