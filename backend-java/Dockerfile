# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/codecognition-ai-3.3.0.jar app.jar

EXPOSE 8000

ENV PORT=8000
ENV DB_HOST=localhost
ENV DB_PORT=3306
ENV DB_USER=root
ENV DB_PASSWORD=password
ENV DB_NAME=codecognition
ENV JWT_SECRET=your-jwt-secret

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=${PORT}"]
