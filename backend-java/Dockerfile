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
ENV SPRING_DATASOURCE_URL=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
ENV SPRING_DATASOURCE_USERNAME=${DB_USER}
ENV SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT} --spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME} --spring.datasource.username=${DB_USER} --spring.datasource.password=${DB_PASSWORD} --spring.jpa.hibernate.ddl-auto=update --jwt.secret=${JWT_SECRET}"]
