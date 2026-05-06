FROM maven:3.9.8-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY quartz-manager-parent/pom.xml ./quartz-manager-parent/
COPY quartz-manager-parent/lombok.config ./quartz-manager-parent/
COPY quartz-manager-parent/quartz-manager-common ./quartz-manager-parent/quartz-manager-common/
COPY quartz-manager-parent/quartz-manager-starter-api ./quartz-manager-parent/quartz-manager-starter-api/
COPY quartz-manager-parent/quartz-manager-starter-persistence ./quartz-manager-parent/quartz-manager-starter-persistence/
COPY quartz-manager-parent/quartz-manager-starter-security ./quartz-manager-parent/quartz-manager-starter-security/
COPY quartz-manager-parent/quartz-manager-starter-ui ./quartz-manager-parent/quartz-manager-starter-ui/
COPY quartz-manager-parent/quartz-manager-web-showcase ./quartz-manager-parent/quartz-manager-web-showcase/
COPY quartz-manager-parent/lombok.config ./quartz-manager-parent/
COPY quartz-manager-frontend ./quartz-manager-frontend/
WORKDIR /app/quartz-manager-parent
RUN mvn clean package -DskipTests -P=build-webjar


# Stage 2: Create the final image
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/quartz-manager-parent/quartz-manager-web-showcase/target/*-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
