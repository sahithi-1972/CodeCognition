# Build stage
FROM maven:3.9.11-eclipse-temurin-17 AS builder
WORKDIR /app
COPY backend-java/pom.xml .
RUN mvn dependency:go-offline
COPY backend-java/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/codecognition-ai-3.3.0.jar app.jar
EXPOSE 8000
CMD ["java", "-jar", "app.jar"]
