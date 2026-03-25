# Epic: The Ultimate Deployment Playbook

This document is a **comprehensive, low-level guide** for Claude Code (or any DevOps automation tool/engineer) to deploy the `UR Urban Runners Casablanca` (Runhub) application from development to a production environment. 

**Read everything carefully.** Do not skip steps. Execute commands exactly as proposed, adjusting variables only where indicated.

---

## 1. Target Architecture & Prerequisites

We will deploy on a Linux Virtual Private Server (VPS) with the following stack:
- **Host System**: Ubuntu 22.04 LTS (minimum 2vCPU, 4GB RAM)
- **Containerization**: Docker & Docker Compose V2
- **Reverse Proxy / SSL**: Nginx (Running on the host) + Let's Encrypt (Certbot)
- **Database**: Managed PostgreSQL 15 (e.g., AWS RDS, DigitalOcean Managed DB)
- **App Containers**: 
  - `runhub-backend` (Spring Boot 3, Java 21) exposing `8080` internally.
  - `runhub-frontend` (Angular 17+, Nginx) exposing `80` mapped to `4200` internally.

### 🔑 1.1 Prerequisites Setup (For Claude Code)
1. Ensure the user provides a newly provisioned VPS IP address.
2. The user has pointed `urunner.com` and `api.urunner.com` to the VPS IP via A-records in their DNS provider.
3. Obtain connection strings for the Managed PostgreSQL database.

---

## 2. Phase 1: Securing Secrets & Variables

Before deploying, ensure no secrets are leaked.
1. Run a check to ensure `backend/.env` and `frontend/.env` are in `.gitignore`.
2. All these variables must be added as repository secrets in GitHub/GitLab:
   - `PROD_SSH_HOST` (VPS IP)
   - `PROD_SSH_KEY` (Private SSH key to access the VPS)
   - `PROD_SSH_USER` (e.g., `root` or `ubuntu`)
   - `GH_REGISTRY_TOKEN` (GitHub Personal Access Token with `write:packages` scope)
   - `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SPRING_DATASOURCE_URL` (Managed DB URI)
   - `STRAVA_CLIENT_ID`, `STRAVA_CLIENT_SECRET`, `GARMIN_CONSUMER_KEY`, `GARMIN_CONSUMER_SECRET`
   - `JWT_SECRET` (Generate a new secure hash)

---

## 3. Phase 2: Updating Dockerfiles for Production

The current Dockerfiles are well-structured multi-stage builds. Ensure they remain intact.

**Backend Dockerfile Validation (`backend/Dockerfile`)**:
It uses Maven to build and Eclipse Temurin 21 to run. No changes needed.

**Frontend Dockerfile Validation (`frontend/Dockerfile`)**:
It uses `npm run build -- --configuration production`.
Ensure `frontend/nginx.conf` exists and is structured to serve Angular routing correctly:
```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```
*Claude Code Task: Verify `frontend/nginx.conf` matches the above to prevent 404s on page refreshes.*

---

## 4. Phase 3: Creating the Production Docker Compose

*Claude Code Task: Create `docker-compose.prod.yml` in the project root.*

```yaml
version: '3.8'

services:
  backend:
    image: ghcr.io/${GITHUB_REPOSITORY}/runhub-backend:latest
    container_name: runhub-backend-prod
    restart: always
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
      - SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
      - SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
      - STRAVA_CLIENT_ID=${STRAVA_CLIENT_ID}
      - STRAVA_CLIENT_SECRET=${STRAVA_CLIENT_SECRET}
      - GARMIN_CONSUMER_KEY=${GARMIN_CONSUMER_KEY}
      - GARMIN_CONSUMER_SECRET=${GARMIN_CONSUMER_SECRET}
      - JWT_SECRET=${JWT_SECRET}
      - APP_BASE_URL=https://api.urunner.com
      - FRONTEND_URL=https://urunner.com
    ports:
      - "127.0.0.1:8080:8080" # Bound to localhost for security
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  frontend:
    image: ghcr.io/${GITHUB_REPOSITORY}/runhub-frontend:latest
    container_name: runhub-frontend-prod
    restart: always
    ports:
      - "127.0.0.1:4200:80" # Bound to localhost, host Nginx will proxy to this
```

---

## 5. Phase 4: CI/CD Pipeline Configuration (GitHub Actions)

*Claude Code Task: Create `.github/workflows/deploy-prod.yml`.*

```yaml
name: Deploy to Production

on:
  push:
    branches: [ "main" ]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  build-and-push-images:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v4
      
      - name: Log in to the Container registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
          
      - name: Build and push Backend
        uses: docker/build-push-action@v5
        with:
          context: ./backend
          push: true
          tags: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}/runhub-backend:latest
          
      - name: Build and push Frontend
        uses: docker/build-push-action@v5
        with:
          context: ./frontend
          push: true
          tags: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}/runhub-frontend:latest

  deploy-to-vps:
    needs: build-and-push-images
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Copy docker-compose.prod.yml to VPS
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.PROD_SSH_HOST }}
          username: ${{ secrets.PROD_SSH_USER }}
          key: ${{ secrets.PROD_SSH_KEY }}
          source: "docker-compose.prod.yml"
          target: "/opt/runhub/"
          
      - name: SSH and Deploy
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.PROD_SSH_HOST }}
          username: ${{ secrets.PROD_SSH_USER }}
          key: ${{ secrets.PROD_SSH_KEY }}
          script: |
            echo "${{ secrets.GITHUB_TOKEN }}" | docker login ghcr.io -u ${{ github.actor }} --password-stdin
            export SPRING_DATASOURCE_URL="${{ secrets.SPRING_DATASOURCE_URL }}"
            export POSTGRES_USER="${{ secrets.POSTGRES_USER }}"
            export POSTGRES_PASSWORD="${{ secrets.POSTGRES_PASSWORD }}"
            export JWT_SECRET="${{ secrets.JWT_SECRET }}"
            export STRAVA_CLIENT_ID="${{ secrets.STRAVA_CLIENT_ID }}"
            export STRAVA_CLIENT_SECRET="${{ secrets.STRAVA_CLIENT_SECRET }}"
            export GARMIN_CONSUMER_KEY="${{ secrets.GARMIN_CONSUMER_KEY }}"
            export GARMIN_CONSUMER_SECRET="${{ secrets.GARMIN_CONSUMER_SECRET }}"
            export GITHUB_REPOSITORY="${{ github.repository }}"
            
            cd /opt/runhub
            docker compose -f docker-compose.prod.yml pull
            docker compose -f docker-compose.prod.yml up -d
            docker system prune -af --volumes
```

---

## 6. Phase 5: Server Setup & Reverse Proxy (Executed Once on VPS)

*Claude Code Task: Provide these commands to the user to run on the production server via SSH, or execute them if SSH access is provided.*

1. **Install Nginx & Certbot**:
   ```bash
   sudo apt update
   sudo apt install -y nginx certbot python3-certbot-nginx
   ```
2. **Configure Nginx**: Create `/etc/nginx/sites-available/runhub`
   ```nginx
   server {
       server_name urunner.com www.urunner.com;
       location / {
           proxy_pass http://127.0.0.1:4200;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }

   server {
       server_name api.urunner.com;
       location / {
           proxy_pass http://127.0.0.1:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```
3. **Enable Site & SSL**:
   ```bash
   sudo ln -s /etc/nginx/sites-available/runhub /etc/nginx/sites-enabled/
   sudo nginx -t
   sudo systemctl reload nginx
   sudo certbot --nginx -d urunner.com -d www.urunner.com -d api.urunner.com
   ```

---

## 7. Phase 6: Database Initialization

1. Connect to the Managed PostgreSQL instance using `pgAdmin` or `psql`.
2. Ensure the Spring Boot `application-prod.yml` has `spring.jpa.hibernate.ddl-auto=update` or use **Flyway/Liquibase** to let the backend create tables.
3. *Crucially*, **DO NOT** run `database/seed_casablanca_community.sql` in production. It contains demo data. Wait for organic user sign-ups or run a tailored initialization script for production admins only.

---

## 8. Claude Code Execution Strategy Summary

If you (Claude Code) are instructed to deploy the application, follow this exact flowchart:
1. **Analyze**: Verify the Dockerfiles and `frontend/nginx.conf` as shown in Phase 2.
2. **Generate Files**: Write `docker-compose.prod.yml` (Phase 3) and `.github/workflows/deploy-prod.yml` (Phase 4).
3. **Commit & Push**: Push these files to the `main` branch.
4. **Instruct User**: Ask the user to inject the secrets listed in Phase 1 into their GitHub Repository Settings.
5. **Finalize**: Instruct the user on running the Phase 5 script on their VPS.

Do not assume configurations. Always verify against this document.
