# ============================================
# Trainvoc Web Dockerfile
# Multi-stage build for React/Vite application
# ============================================

# Build stage
FROM node:22-alpine AS builder

WORKDIR /app

# Copy package files for dependency caching
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm ci --legacy-peer-deps

# Copy source code
COPY . .

# Build arguments for environment variables
ARG VITE_API_URL=http://localhost:8080/
ARG VITE_WS_URL=ws://localhost:8080/ws

# Set environment variables for build
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_WS_URL=$VITE_WS_URL

# Build the application
RUN npm run build

# ============================================
# Production stage - nginx
# ============================================
FROM nginx:1.25-alpine

LABEL maintainer="Trainvoc Team"
LABEL description="Trainvoc Web Frontend"
LABEL version="1.0.0"

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built assets from builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port
EXPOSE 80

# Install curl for health check (wget not available in nginx-alpine)
RUN apk add --no-cache curl

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -sf http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
