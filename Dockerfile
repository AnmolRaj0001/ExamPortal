# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-focal as build
WORKDIR /app
COPY . .
# Add this line to give execute permissions to the Maven wrapper
RUN chmod +x ./mvnw
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/NadezhdaExams-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]