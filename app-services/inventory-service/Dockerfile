# A lightweight Java 17 runtime image
FROM eclipse-temurin:17-jre

# Setting a working directory inside the container
WORKDIR /app

# Copying the built jar into the container
COPY target/inventory-service.jar app.jar

# Exposing the port Inventory Service listens on
EXPOSE 8083

# Running the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]