# RunHub — Project Brain

## What this is
Full-stack social running platform. Users track activities, join communities, view rankings, attend events, chat in rooms, and sync with Strava/Garmin.

## Stack
| Layer | Tech |
|---|---|
| Backend | Spring Boot 3 / Java 21, Spring Security + JWT, JPA/Hibernate 6 |
| Database | PostgreSQL 15 (Docker), schema in `database/schema.sql` |
| Frontend | Angular 17 standalone components, Tailwind CSS v3 |
| Infra | Docker Compose, Nginx reverse proxy (`/api/*` → backend:8080) |
| OAuth | Strava (OAuth 2.0), Garmin (OAuth 1.0a / ScribeJava) |
| Storage | Google Drive API v3 for community photo albums |

## Repo layout
```
backend/src/main/java/com/runhub/
  auth/         JWT auth + Strava/Garmin OAuth
  users/        User model, profile, UserService
  running/      RunningActivity, Strava/Garmin sync
  communities/  Community, CommunityMember, invites, Google Drive sync
  feed/         Post, Comment, Like (TEXT + PHOTO_ALBUM post types)
  events/       Event + EventRegistration
  programs/     Training programs + user progress
  rankings/     Leaderboard queries
  chat/         Direct messages
  rooms/        Community rooms (group chat)
  badges/       Badge award system
  ai/           AI coach endpoint
  config/       SecurityConfig, JwtAuthFilter, JwtService, GlobalExceptionHandler

frontend/src/app/
  core/         models/, services/, guards/, interceptors/
  features/     One folder per page (auth, dashboard, communities, feed, etc.)
  shared/       layout component (sidebar desktop / bottom nav mobile)
```

## Docker commands
```bash
# from /Users/macbook/Documents/CODE/urunner/runhub
docker compose up -d
docker compose build backend   # after Java changes
docker compose build frontend  # after Angular changes
docker compose logs backend --tail=30
docker compose exec postgres psql -U runhub -d runhub
```
App: http://localhost:4200 (or http://localhost)

## Key patterns
- **Backend package structure:** each domain has `controller/`, `service/`, `repository/`, `model/`, `dto/`, `mapper/`
- **Security:** `JwtAuthFilter` runs before every request; public routes whitelisted in `SecurityConfig`
- **Enums stored as VARCHAR** — all enums converted to VARCHAR(20) in DB to avoid Hibernate cast issues
- **Angular services** in `core/services/` call `/api/*`; auth token attached by `auth.interceptor.ts`
- **Standalone components** — no NgModules; routes in `app.routes.ts`

## Database — current live state
Schema file: `database/schema.sql` (source of truth for structure)

**Extra columns applied directly (not in schema.sql):**
- `communities`: `drive_folder_id`, `cover_url`, `is_private`
- `posts`: `community_id`, `post_type`, `photo_urls`, `deleted`, `pinned`
- `community_invites` table (see memory for full DDL)

**Enum → VARCHAR migrations already applied:**
`event_registrations.status`, `programs.level`, `user_program_progress.status`, `community_members.role`

## Auth & seed data
- JWT secret in `.env` (git-ignored)
- Seed users password: `password123`
- Admin: `alice@example.com` / `alice_runner` (admin of communities 4 & 5)
- Global admin: `admin@runhub.com` / `admin`
- Strava Client ID: `201864`

## Communities (seeded)
| ID | Name | Creator |
|----|------|---------|
| 1 | City Marathon Club | david_ultra |
| 2 | Trail Blazers | carol_trails |
| 3 | Morning Runners Squad | frank_morning |
| 4 | UR : Runners casablanca | alice_runner |
| 5 | Community AR Running | alice_runner |

## What's built
- JWT login + Strava OAuth callback
- Dark Tailwind UI (bg `#050a18`), responsive layout
- Communities: create, join/leave, feed (TEXT + PHOTO_ALBUM), like/comment
- Google Drive sync → PHOTO_ALBUM post
- Admin controls: kick, role change, delete/pin posts
- Invite by username (UUID token, 7-day expiry)
- Activity tracking (manual + Strava/Garmin sync)
- Events with registration
- Training programs with session progress
- Rankings/leaderboard
- Direct chat + community rooms
- Badge system
- AI coach endpoint

## Backlog & Stories
All product work is tracked in `docs/backlog/`. One folder, four states:
- `docs/backlog/done/` — shipped MVP1 work
- `docs/backlog/sprint/` — active MVP2 stories (US-007 → US-016)
- `docs/backlog/next/` — MVP3 planned work (bugs, garmin suite, payments, etc.)
- `docs/backlog/ideas/` — future ideation pool (150+ consolidated ideas)

Master index: `docs/backlog/README.md`
Story template: `docs/backlog/_TEMPLATE.md`

## Dev rules
- Changes to backend require `docker compose build backend && docker compose up -d`
- Never re-run already-applied SQL migrations
- Post types: `TEXT` or `PHOTO_ALBUM` (stored as VARCHAR in `posts.post_type`)
- Community member roles: `ADMIN`, `MODERATOR`, `MEMBER` (VARCHAR)
- GitHub: https://github.com/visionamorg/urunner (push after each feature)
