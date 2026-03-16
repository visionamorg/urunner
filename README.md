# RunHub - Running Community Platform

A full-stack running community platform built with Spring Boot and Angular.

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 20+
- npm 10+
- PostgreSQL 15+ (or Docker)

---

## Option 1: Docker Compose (Recommended)

```bash
# From the runhub/ root directory
docker-compose up --build
```

- Frontend: http://localhost:4200
- Backend API: http://localhost:8080
- Database: localhost:5432

---

## Option 2: Manual Setup

### 1. Start PostgreSQL

```bash
# Using Docker for just the database
docker run -d \
  --name runhub-postgres \
  -e POSTGRES_DB=runhub \
  -e POSTGRES_USER=runhub \
  -e POSTGRES_PASSWORD=runhub123 \
  -p 5432:5432 \
  postgres:15-alpine
```

Or install PostgreSQL locally and create:
```sql
CREATE DATABASE runhub;
CREATE USER runhub WITH PASSWORD 'runhub123';
GRANT ALL PRIVILEGES ON DATABASE runhub TO runhub;
```

### 2. Initialize Database Schema & Seed Data

```bash
psql -U runhub -d runhub -f database/schema.sql
psql -U runhub -d runhub -f database/seed.sql
```

> Note: If using Spring Boot with `ddl-auto: update`, the schema will auto-create.
> Run only seed.sql after first boot.

### 3. Start Backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts at: http://localhost:8080

### 4. Start Frontend

```bash
cd frontend
npm install
npm start
```

Frontend starts at: http://localhost:4200

---

## Demo Accounts

All accounts use password: `password123`

| Email | Username | Role |
|-------|----------|------|
| admin@runhub.com | admin | ADMIN |
| alice@example.com | alice_runner | USER |
| bob@example.com | bob_sprints | USER |
| carol@example.com | carol_trails | USER |
| david@example.com | david_ultra | ORGANIZER |
| emma@example.com | emma_pace | ORGANIZER |
| grace@example.com | grace_newbie | USER |

---

## API Documentation

### Auth
```
POST /api/auth/register   - Register new user
POST /api/auth/login      - Login, returns JWT
```

### Activities
```
GET  /api/activities         - All activities (public)
POST /api/activities         - Log activity (auth required)
GET  /api/activities/user    - My activities
GET  /api/activities/stats   - My stats
```

### Communities
```
GET  /api/communities           - List communities
POST /api/communities           - Create community
GET  /api/communities/{id}      - Get community
POST /api/communities/{id}/join - Join community
GET  /api/communities/{id}/members - List members
```

### Events
```
GET  /api/events                     - List events
POST /api/events                     - Create event
GET  /api/events/{id}                - Get event
POST /api/events/{id}/register       - Register
GET  /api/events/{id}/participants   - List participants
```

### Feed
```
GET  /api/posts              - Get posts (paginated)
POST /api/posts              - Create post
POST /api/posts/{id}/like    - Like/unlike post
POST /api/posts/{id}/comments - Add comment
```

### Chat
```
GET  /api/messages?communityId={id} - Get messages
POST /api/messages                   - Send message
```

### Programs
```
GET  /api/programs           - List programs
GET  /api/programs/{id}      - Get program
POST /api/programs/{id}/start - Start program
GET  /api/programs/my-progress - My progress
```

### Rankings
```
GET /api/rankings/global?type=weekly|monthly|alltime
GET /api/rankings/community/{id}
```

### AI Assistant
```
POST /api/ai/ask   - { "question": "How do I train for a marathon?" }
```

---

## Project Structure

```
runhub/
├── backend/          # Spring Boot application
│   └── src/main/java/com/runhub/
│       ├── auth/         # Authentication
│       ├── users/        # User management
│       ├── running/      # Activity tracking
│       ├── communities/  # Running communities
│       ├── events/       # Race events
│       ├── feed/         # Social feed
│       ├── chat/         # Messaging
│       ├── badges/       # Achievements
│       ├── programs/     # Training programs
│       ├── rankings/     # Leaderboards
│       └── ai/           # AI assistant
├── frontend/         # Angular application
│   └── src/app/
│       ├── core/         # Services, models, guards
│       ├── shared/       # Layout component
│       └── features/     # Pages
├── database/         # SQL schema & seed data
└── docs/             # Architecture docs
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Java 21 |
| Security | Spring Security + JWT |
| Database | PostgreSQL 15 |
| ORM | Spring Data JPA + Hibernate |
| Frontend | Angular 17 (standalone) |
| UI | Angular Material 17 |
| Containers | Docker + Docker Compose |
