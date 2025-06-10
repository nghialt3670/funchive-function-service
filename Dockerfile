# Stage 1: Build the application
FROM eclipse-temurin:24-jdk-alpine AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]