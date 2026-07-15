# ============================================
# Trainvoc Backend Dockerfile
# Multi-stage build for optimized image size
# ============================================

# Build stage — JDK 21 LTS (the backend dropped EOL JDK 24 in PR #40;
# a 24-based image can't provision the Java 21 toolchain Gradle needs)
#
# BUILD CONTEXT IS THE MONOREPO ROOT (compose/CI pass context: . with this
# dockerfile) so the client's seed_v18.json can ride into the jar: gradle's
# processResources copies it from ../TrainvocClient relative to the backend
# projectDir, and without it the words DB boots empty (importer skips). The
# root .dockerignore allowlists only the backend tree + that one manifest.
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle files first for better layer caching
COPY TrainvocBackend/gradle gradle
COPY TrainvocBackend/gradlew .
COPY TrainvocBackend/build.gradle .
COPY TrainvocBackend/settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY TrainvocBackend/src src

# Seed manifest: projectDir is /app, so gradle resolves ../TrainvocClient → /TrainvocClient
COPY TrainvocClient/app/src/main/assets/database/seed_v18.json /TrainvocClient/app/src/main/assets/database/seed_v18.json

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# ============================================
# Runtime stage
# ============================================
FROM eclipse-temurin:21-jre-alpine

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

# Health check using actuator endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} app.jar"]
