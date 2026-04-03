# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /build

# Optimization: Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Security: Create a non-root user to run the application
RUN addgroup -S orkestra && adduser -S orkestra -G orkestra
USER orkestra

# Copy only the jar from the build stage
COPY --from=build /build/target/*.jar app.jar

# Network documentation
EXPOSE 8080

# Performance: Use the JVM options defined in the .env/docker-compose
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]