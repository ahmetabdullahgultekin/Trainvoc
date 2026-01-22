# ============================================
# Trainvoc Backend Dockerfile
# Multi-stage build for optimized image size
# ============================================

# Build stage
FROM eclipse-temurin:24-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle files first for better layer caching
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# ============================================
# Runtime stage
# ============================================
FROM eclipse-temurin:24-jre-alpine

LABEL maintainer="Trainvoc Team"
LABEL description="Trainvoc Multiplayer Backend API"
LABEL version="1.0.0"

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1000 trainvoc && \
    adduser -u 1000 -G trainvoc -s /bin/sh -D trainvoc

# Copy JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set ownership
RUN chown -R trainvoc:trainvoc /app

# Switch to non-root user
USER trainvoc

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/game/rooms || exit 1

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} app.jar"]
