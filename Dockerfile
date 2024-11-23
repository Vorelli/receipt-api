# Use a base image with JDK installed.
FROM amazoncorretto:21 AS builder

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper files first to leverage Docker's caching mechanism
COPY gradle/wrapper/* gradle/wrapper/
COPY gradlew ./

# Ensure the Gradle wrapper script is executable
RUN chmod +x gradlew

# Initialize gradle wrapper and deps
RUN ./gradlew

# Copy the rest of the application source code to the container
COPY src ./src
RUN ls -al .
RUN ls -al src
RUN ls -al src/main
RUN ls -al src/main/resources
COPY settings.gradle.kts ./
COPY build.gradle.kts ./

# Build the application with Gradle
RUN ./gradlew build 

# Use a Jakarta EE compatible server image
FROM amazoncorretto:21

# Copy the application JAR file to the deployment directory of the server
COPY --from=builder /app/build/libs/api-0.0.1-SNAPSHOT.jar /api.jar
COPY entrypoint.sh ./entrypoint.sh
RUN chmod +x entrypoint.sh

# Set the server to run on start
ENTRYPOINT ["/entrypoint.sh"]
# Set the container to run indefinitely to allow inspection
#CMD ["tail", "-f", "/dev/null"]