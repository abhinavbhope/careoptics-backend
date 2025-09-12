# Stage 1: Build the JAR
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Use Render dynamic port
ENV SERVER_PORT=${PORT:-8080}
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
