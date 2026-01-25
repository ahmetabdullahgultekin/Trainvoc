# SSL Certificate Setup for Trainvoc Backend

This guide covers setting up SSL/TLS for `api.trainvoc.rollingcatsoftware.com` using Let's Encrypt and Nginx reverse proxy.

---

## Architecture Overview

```
                    HTTPS (443)                 HTTP (8080)
    Internet ──────────────────► Nginx ──────────────────► Spring Boot
                  (SSL termination)              (localhost only)
```

**Why this approach?**
- Nginx handles SSL termination efficiently
- Spring Boot runs on localhost:8080 (no SSL overhead)
- Easy certificate renewal with Certbot
- Better security (Spring Boot not directly exposed)

---

## Prerequisites

- Ubuntu 20.04+ or Debian 11+ server (GCP Compute Engine / Hostinger VPS)
- Domain `api.trainvoc.rollingcatsoftware.com` pointing to server IP
- Root/sudo access
- Ports 80 and 443 open in firewall

---

## Step 1: Install Nginx and Certbot

```bash
# Update packages
sudo apt update && sudo apt upgrade -y

# Install Nginx
sudo apt install nginx -y

# Install Certbot and Nginx plugin
sudo apt install certbot python3-certbot-nginx -y

# Verify installations
nginx -v
certbot --version
```

---

## Step 2: Configure DNS

Ensure your domain points to the server:

```bash
# Check DNS resolution
dig api.trainvoc.rollingcatsoftware.com +short
# Should return your server's IP address

# Or using nslookup
nslookup api.trainvoc.rollingcatsoftware.com
```

**DNS Records Required:**
| Type | Name | Value |
|------|------|-------|
| A | api.trainvoc.rollingcatsoftware.com | YOUR_SERVER_IP |

---

## Step 3: Initial Nginx Configuration

Create Nginx config for the API:

```bash
sudo nano /etc/nginx/sites-available/trainvoc-api
```

Add this configuration:

```nginx
server {
    listen 80;
    server_name api.trainvoc.rollingcatsoftware.com;

    # Allow Let's Encrypt verification
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    # Redirect all other HTTP to HTTPS (after SSL setup)
    location / {
        return 301 https://$server_name$request_uri;
    }
}
```

Enable the site:

```bash
# Create symbolic link
sudo ln -s /etc/nginx/sites-available/trainvoc-api /etc/nginx/sites-enabled/

# Test configuration
sudo nginx -t

# Reload Nginx
sudo systemctl reload nginx
```

---

## Step 4: Obtain SSL Certificate

Run Certbot to get the certificate:

```bash
sudo certbot --nginx -d api.trainvoc.rollingcatsoftware.com
```

**During the process:**
1. Enter your email for renewal notices
2. Agree to terms of service
3. Choose whether to redirect HTTP to HTTPS (recommended: Yes)

**Verify certificate:**

```bash
# Check certificate
sudo certbot certificates

# Test SSL
curl -I https://api.trainvoc.rollingcatsoftware.com
```

---

## Step 5: Configure Nginx as Reverse Proxy

Update the Nginx configuration for full reverse proxy:

```bash
sudo nano /etc/nginx/sites-available/trainvoc-api
```

Replace with this complete configuration:

```nginx
# HTTP - Redirect to HTTPS
server {
    listen 80;
    server_name api.trainvoc.rollingcatsoftware.com;

    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    location / {
        return 301 https://$server_name$request_uri;
    }
}

# HTTPS - Main server block
server {
    listen 443 ssl http2;
    server_name api.trainvoc.rollingcatsoftware.com;

    # SSL certificates (managed by Certbot)
    ssl_certificate /etc/letsencrypt/live/api.trainvoc.rollingcatsoftware.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.trainvoc.rollingcatsoftware.com/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Logging
    access_log /var/log/nginx/trainvoc-api-access.log;
    error_log /var/log/nginx/trainvoc-api-error.log;

    # REST API proxy
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # WebSocket proxy
    location /ws/ {
        proxy_pass http://127.0.0.1:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket timeouts
        proxy_connect_timeout 7d;
        proxy_send_timeout 7d;
        proxy_read_timeout 7d;
    }

    # Health check endpoint
    location /actuator/health {
        proxy_pass http://127.0.0.1:8080/actuator/health;
        proxy_set_header Host $host;
    }

    # Block access to sensitive endpoints from outside
    location /actuator/ {
        deny all;
        return 403;
    }
}
```

Test and reload:

```bash
sudo nginx -t
sudo systemctl reload nginx
```

---

## Step 6: Configure Spring Boot

Update `application-prod.properties`:

```properties
# Server runs on localhost only (Nginx handles external traffic)
server.port=8080
server.address=127.0.0.1

# SSL disabled - handled by Nginx
server.ssl.enabled=false

# Trust proxy headers
server.forward-headers-strategy=native
server.tomcat.remoteip.remote-ip-header=X-Forwarded-For
server.tomcat.remoteip.protocol-header=X-Forwarded-Proto
```

---

## Step 7: Firewall Configuration

Configure UFW firewall:

```bash
# Allow SSH (important - don't lock yourself out!)
sudo ufw allow OpenSSH

# Allow HTTP (for Let's Encrypt renewal)
sudo ufw allow 80/tcp

# Allow HTTPS
sudo ufw allow 443/tcp

# Block direct access to Spring Boot (only localhost)
# Port 8080 should NOT be open externally

# Enable firewall
sudo ufw enable

# Check status
sudo ufw status
```

**Expected output:**
```
Status: active

To                         Action      From
--                         ------      ----
OpenSSH                    ALLOW       Anywhere
80/tcp                     ALLOW       Anywhere
443/tcp                    ALLOW       Anywhere
```

---

## Step 8: Automatic Certificate Renewal

Certbot automatically sets up a cron job for renewal. Verify:

```bash
# Test renewal process (dry run)
sudo certbot renew --dry-run

# Check the timer
sudo systemctl status certbot.timer
```

**Manual renewal (if needed):**

```bash
sudo certbot renew
sudo systemctl reload nginx
```

---

## Step 9: Start Spring Boot as a Service

Create a systemd service:

```bash
sudo nano /etc/systemd/system/trainvoc-backend.service
```

```ini
[Unit]
Description=Trainvoc Backend API
After=network.target postgresql.service

[Service]
Type=simple
User=trainvoc
Group=trainvoc
WorkingDirectory=/opt/trainvoc/backend
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod trainvoc-backend.jar
Restart=always
RestartSec=10

# Environment variables
Environment="DATABASE_URL=jdbc:postgresql://localhost:5432/trainvoc"
Environment="DATABASE_USER=trainvoc"
Environment="DATABASE_PASSWORD=your_secure_password"
Environment="DATABASE_WORDS_URL=jdbc:postgresql://localhost:5432/trainvoc-words"
Environment="DATABASE_WORDS_USER=trainvoc"
Environment="DATABASE_WORDS_PASSWORD=your_secure_password"
Environment="CORS_ALLOWED_ORIGINS=https://trainvoc.rollingcatsoftware.com"

[Install]
WantedBy=multi-user.target
```

Enable and start:

```bash
# Create user
sudo useradd -r -s /bin/false trainvoc

# Set ownership
sudo chown -R trainvoc:trainvoc /opt/trainvoc

# Reload systemd
sudo systemctl daemon-reload

# Enable and start
sudo systemctl enable trainvoc-backend
sudo systemctl start trainvoc-backend

# Check status
sudo systemctl status trainvoc-backend
```

---

## Verification Checklist

```bash
# 1. Check Nginx is running
sudo systemctl status nginx

# 2. Check Spring Boot is running
sudo systemctl status trainvoc-backend

# 3. Test HTTPS endpoint
curl -I https://api.trainvoc.rollingcatsoftware.com/actuator/health

# 4. Test WebSocket (using wscat)
npm install -g wscat
wscat -c wss://api.trainvoc.rollingcatsoftware.com/ws/game/test

# 5. Check SSL certificate
openssl s_client -connect api.trainvoc.rollingcatsoftware.com:443 -servername api.trainvoc.rollingcatsoftware.com < /dev/null 2>/dev/null | openssl x509 -noout -dates

# 6. SSL Labs test (in browser)
# https://www.ssllabs.com/ssltest/analyze.html?d=api.trainvoc.rollingcatsoftware.com
```

---

## Troubleshooting

### Certificate Issues

```bash
# View certificate details
sudo certbot certificates

# Force renewal
sudo certbot renew --force-renewal

# Check certificate files exist
ls -la /etc/letsencrypt/live/api.trainvoc.rollingcatsoftware.com/
```

### Nginx Issues

```bash
# Test configuration
sudo nginx -t

# View error logs
sudo tail -f /var/log/nginx/trainvoc-api-error.log

# Check if listening on ports
sudo netstat -tlnp | grep nginx
```

### Spring Boot Issues

```bash
# View logs
sudo journalctl -u trainvoc-backend -f

# Check if running
sudo netstat -tlnp | grep 8080

# Test locally
curl http://127.0.0.1:8080/actuator/health
```

### WebSocket Issues

```bash
# Check WebSocket upgrade in Nginx logs
sudo grep "upgrade" /var/log/nginx/trainvoc-api-access.log

# Test WebSocket connection
curl -i -N \
  -H "Connection: Upgrade" \
  -H "Upgrade: websocket" \
  -H "Sec-WebSocket-Version: 13" \
  -H "Sec-WebSocket-Key: test" \
  https://api.trainvoc.rollingcatsoftware.com/ws/game/test
```

---

## Security Best Practices

1. **Keep system updated**: `sudo apt update && sudo apt upgrade`
2. **Use strong database passwords**: Generate with `openssl rand -base64 32`
3. **Limit SSH access**: Use key-based authentication, disable password login
4. **Monitor logs**: Set up log rotation and monitoring
5. **Regular backups**: Database and configuration files
6. **Rate limiting**: Consider adding to Nginx config

---

## Quick Reference

| Service | Command |
|---------|---------|
| Start Nginx | `sudo systemctl start nginx` |
| Stop Nginx | `sudo systemctl stop nginx` |
| Reload Nginx | `sudo systemctl reload nginx` |
| Start Backend | `sudo systemctl start trainvoc-backend` |
| Stop Backend | `sudo systemctl stop trainvoc-backend` |
| View Backend Logs | `sudo journalctl -u trainvoc-backend -f` |
| Renew Certificate | `sudo certbot renew` |
| Test SSL | `openssl s_client -connect api.trainvoc.rollingcatsoftware.com:443` |

---

*Last Updated: 2026-01-25*
