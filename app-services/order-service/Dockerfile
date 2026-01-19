# lightweight Java 17 runtime image
FROM eclipse-temurin:17-jre

# Setting working directory
WORKDIR /app

# Copy the built jar from the Maven target directory into the container
# The jar name should match what Maven produces
COPY target/order-service.jar app.jar

# Expose the port order service listens on
EXPOSE 8081

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]