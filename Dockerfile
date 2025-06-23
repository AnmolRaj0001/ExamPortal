# Use a JDK base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the JAR file from target to /app
COPY target/NadezhdaExams-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render sets PORT via env var)
EXPOSE 8080

# Run the JAR file
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
