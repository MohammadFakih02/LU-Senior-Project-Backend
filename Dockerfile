# ---- Build Stage ----
# Use an Eclipse Temurin image with Maven and JDK 21
FROM maven:3.9-eclipse-temurin-21-jammy AS builder
WORKDIR /app

# Copy the POM file and download dependencies to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
# Ensure your aiven_truststore.jks is in src/main/resources/certs/
COPY src ./src

# Build the application JAR
# -DskipTests to speed up the build if tests are run separately
RUN mvn clean package -DskipTests

# ---- Runtime Stage ----
# Use a slim Eclipse Temurin JRE image for the runtime environment
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Environment variables that your application.properties expects
# These can be overridden by docker-compose or Cloud Run configurations
# Setting them here provides defaults if not overridden (though sensitive ones should always be overridden)
ENV DB_HOST_ENV="lu-senior-project-database-mohammadfakih025-916d.l.aivencloud.com"
ENV DB_PORT_ENV="12356"
ENV DB_NAME_ENV="defaultdb"
# Sensitive variables like DB_USERNAME_ENV, DB_PASSWORD_ENV, DB_KEYSTORE_PASSWORD_ENV
# should NOT have defaults here. They will be injected at runtime.

# Copy the JAR file from the build stage
COPY --from=builder /app/target/internetprovidermanagement-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot application runs on (default is 8080)
EXPOSE 8080

# Command to run the application
# The actual sensitive environment variables will be provided when running the container
ENTRYPOINT ["java", "-jar", "app.jar"]