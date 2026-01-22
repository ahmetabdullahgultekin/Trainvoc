#!/bin/bash
# ============================================
# Trainvoc Deployment Script
# ============================================
# Usage: ./deploy.sh [command]
# Commands:
#   setup     - Initial setup on new VPS
#   build     - Build all containers
#   start     - Start all services
#   stop      - Stop all services
#   restart   - Restart all services
#   logs      - View logs
#   status    - Check service status
#   ssl       - Setup/renew SSL certificates
#   backup    - Backup databases
#   restore   - Restore from backup
# ============================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
COMPOSE_FILE="docker-compose.yml"
BACKUP_DIR="./backups"
DOMAIN="trainvoc.rollingcatsoftware.com"

# ==========================================
# Helper Functions
# ==========================================

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_env() {
    if [ ! -f .env ]; then
        log_error ".env file not found!"
        log_info "Copy .env.example to .env and fill in the values:"
        echo "  cp .env.example .env"
        echo "  nano .env"
        exit 1
    fi
}

check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed!"
        log_info "Install Docker with: curl -fsSL https://get.docker.com | sh"
        exit 1
    fi

    if ! command -v docker compose &> /dev/null; then
        log_error "Docker Compose is not installed!"
        exit 1
    fi
}

# ==========================================
# Commands
# ==========================================

cmd_setup() {
    log_info "Setting up Trainvoc on this server..."

    # Check prerequisites
    check_docker

    # Create required directories
    mkdir -p nginx backups

    # Check .env
    if [ ! -f .env ]; then
        log_warning ".env file not found. Creating from example..."
        cp .env.example .env
        log_warning "Please edit .env with your settings before starting!"
        echo ""
        echo "Required settings:"
        echo "  - DB_PASSWORD: Secure database password"
        echo "  - CERTBOT_EMAIL: Email for SSL certificates"
        echo ""
        log_info "Edit with: nano .env"
        exit 0
    fi

    # Build web frontend
    if [ -d "TrainvocWeb" ]; then
        log_info "Building web frontend..."
        cd TrainvocWeb
        if command -v npm &> /dev/null; then
            npm install
            npm run build
        else
            log_warning "npm not found. Install Node.js to build frontend."
            log_info "On Ubuntu: sudo apt install nodejs npm"
        fi
        cd ..
    fi

    log_success "Setup complete!"
    log_info "Next steps:"
    echo "  1. Edit .env with your settings"
    echo "  2. Run: ./deploy.sh build"
    echo "  3. Run: ./deploy.sh ssl"
    echo "  4. Run: ./deploy.sh start"
}

cmd_build() {
    log_info "Building Docker images..."
    check_env
    check_docker

    # Build web frontend first
    if [ -d "TrainvocWeb" ] && command -v npm &> /dev/null; then
        log_info "Building React frontend..."
        cd TrainvocWeb
        npm install
        npm run build
        cd ..
    fi

    # Build Docker images
    docker compose -f $COMPOSE_FILE build --no-cache

    log_success "Build complete!"
}

cmd_start() {
    log_info "Starting Trainvoc services..."
    check_env
    check_docker

    # Check if frontend is built
    if [ ! -d "TrainvocWeb/dist" ]; then
        log_warning "Frontend not built. Building now..."
        if [ -d "TrainvocWeb" ] && command -v npm &> /dev/null; then
            cd TrainvocWeb
            npm install
            npm run build
            cd ..
        else
            log_error "Cannot build frontend. Ensure Node.js is installed."
            exit 1
        fi
    fi

    docker compose -f $COMPOSE_FILE up -d

    log_success "Services started!"
    log_info "Checking health..."
    sleep 10
    cmd_status
}

cmd_stop() {
    log_info "Stopping Trainvoc services..."
    check_docker

    docker compose -f $COMPOSE_FILE down

    log_success "Services stopped!"
}

cmd_restart() {
    log_info "Restarting Trainvoc services..."
    cmd_stop
    cmd_start
}

cmd_logs() {
    check_docker
    service=${2:-}
    if [ -z "$service" ]; then
        docker compose -f $COMPOSE_FILE logs -f --tail=100
    else
        docker compose -f $COMPOSE_FILE logs -f --tail=100 "$service"
    fi
}

cmd_status() {
    log_info "Service Status:"
    check_docker

    docker compose -f $COMPOSE_FILE ps

    echo ""
    log_info "Health Checks:"

    # Check backend health
    if curl -s http://localhost:8080/api/game/rooms > /dev/null 2>&1; then
        echo -e "  Backend API:  ${GREEN}Healthy${NC}"
    else
        echo -e "  Backend API:  ${RED}Unhealthy${NC}"
    fi

    # Check nginx
    if curl -s http://localhost > /dev/null 2>&1; then
        echo -e "  Nginx:        ${GREEN}Healthy${NC}"
    else
        echo -e "  Nginx:        ${RED}Unhealthy${NC}"
    fi
}

cmd_ssl() {
    log_info "Setting up SSL certificates..."
    check_env
    check_docker

    source .env

    if [ -z "$CERTBOT_EMAIL" ]; then
        log_error "CERTBOT_EMAIL not set in .env"
        exit 1
    fi

    # Start nginx temporarily for challenge
    docker compose -f $COMPOSE_FILE up -d nginx

    # Request certificates
    docker compose -f $COMPOSE_FILE run --rm certbot certonly \
        --webroot \
        --webroot-path=/var/www/certbot \
        --email "$CERTBOT_EMAIL" \
        --agree-tos \
        --no-eff-email \
        -d "$DOMAIN" \
        -d "api.$DOMAIN"

    # Restart nginx to load new certificates
    docker compose -f $COMPOSE_FILE restart nginx

    log_success "SSL certificates configured!"
}

cmd_backup() {
    log_info "Backing up databases..."
    check_docker

    timestamp=$(date +%Y%m%d_%H%M%S)
    mkdir -p "$BACKUP_DIR"

    # Backup primary database
    log_info "Backing up trainvoc database..."
    docker compose -f $COMPOSE_FILE exec -T postgres \
        pg_dump -U trainvoc trainvoc > "$BACKUP_DIR/trainvoc_$timestamp.sql"

    # Backup words database
    log_info "Backing up trainvoc-words database..."
    docker compose -f $COMPOSE_FILE exec -T postgres-words \
        pg_dump -U trainvoc trainvoc-words > "$BACKUP_DIR/trainvoc-words_$timestamp.sql"

    # Compress backups
    gzip "$BACKUP_DIR/trainvoc_$timestamp.sql"
    gzip "$BACKUP_DIR/trainvoc-words_$timestamp.sql"

    log_success "Backups created:"
    echo "  - $BACKUP_DIR/trainvoc_$timestamp.sql.gz"
    echo "  - $BACKUP_DIR/trainvoc-words_$timestamp.sql.gz"

    # Clean old backups (keep last 7 days)
    find "$BACKUP_DIR" -name "*.sql.gz" -mtime +7 -delete
    log_info "Old backups cleaned (keeping last 7 days)"
}

cmd_restore() {
    log_info "Restore from backup"
    check_docker

    if [ -z "$2" ] || [ -z "$3" ]; then
        log_error "Usage: ./deploy.sh restore <trainvoc_backup.sql.gz> <trainvoc-words_backup.sql.gz>"
        echo ""
        echo "Available backups:"
        ls -la "$BACKUP_DIR"/*.sql.gz 2>/dev/null || echo "  No backups found"
        exit 1
    fi

    log_warning "This will OVERWRITE the current databases!"
    read -p "Are you sure? (y/N): " confirm
    if [ "$confirm" != "y" ]; then
        log_info "Restore cancelled."
        exit 0
    fi

    # Decompress and restore
    log_info "Restoring trainvoc database..."
    gunzip -c "$2" | docker compose -f $COMPOSE_FILE exec -T postgres \
        psql -U trainvoc -d trainvoc

    log_info "Restoring trainvoc-words database..."
    gunzip -c "$3" | docker compose -f $COMPOSE_FILE exec -T postgres-words \
        psql -U trainvoc -d trainvoc-words

    log_success "Restore complete!"
}

cmd_help() {
    echo "Trainvoc Deployment Script"
    echo ""
    echo "Usage: ./deploy.sh [command]"
    echo ""
    echo "Commands:"
    echo "  setup     Initial setup on new VPS"
    echo "  build     Build all containers"
    echo "  start     Start all services"
    echo "  stop      Stop all services"
    echo "  restart   Restart all services"
    echo "  logs      View logs (optional: service name)"
    echo "  status    Check service status"
    echo "  ssl       Setup/renew SSL certificates"
    echo "  backup    Backup databases"
    echo "  restore   Restore from backup"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./deploy.sh setup          # First time setup"
    echo "  ./deploy.sh start          # Start services"
    echo "  ./deploy.sh logs backend   # View backend logs"
    echo "  ./deploy.sh backup         # Backup databases"
}

# ==========================================
# Main
# ==========================================

case "${1:-help}" in
    setup)   cmd_setup "$@" ;;
    build)   cmd_build "$@" ;;
    start)   cmd_start "$@" ;;
    stop)    cmd_stop "$@" ;;
    restart) cmd_restart "$@" ;;
    logs)    cmd_logs "$@" ;;
    status)  cmd_status "$@" ;;
    ssl)     cmd_ssl "$@" ;;
    backup)  cmd_backup "$@" ;;
    restore) cmd_restore "$@" ;;
    help)    cmd_help ;;
    *)
        log_error "Unknown command: $1"
        cmd_help
        exit 1
        ;;
esac
