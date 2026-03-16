# RunHub - Architecture Documentation

## Overview

RunHub is a monolithic full-stack application for running communities. It provides a centralized platform for tracking running activities, building communities, organizing events, social interactions, training programs, and competitive rankings.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENT                              │
│                   Angular 17 SPA                            │
│              (localhost:4200 / nginx:80)                     │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP/REST + JWT
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                       BACKEND                               │
│               Spring Boot 3.2 (Java 21)                     │
│                    localhost:8080                            │
│                                                             │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
│  │   Auth   │ │  Users   │ │ Running  │ │ Communities  │  │
│  │ Module   │ │  Module  │ │  Module  │ │   Module     │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
│  │  Events  │ │   Feed   │ │   Chat   │ │    Badges    │  │
│  │  Module  │ │  Module  │ │  Module  │ │    Module    │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                    │
│  │ Programs │ │Rankings  │ │    AI    │                    │
│  │  Module  │ │  Module  │ │  Module  │                    │
│  └──────────┘ └──────────┘ └──────────┘                    │
│                                                             │
│           Spring Security + JWT Filter Chain               │
└──────────────────────────┬──────────────────────────────────┘
                           │ JPA / JDBC
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                      DATABASE                               │
│                  PostgreSQL 15                              │
│                  localhost:5432                             │
│                  database: runhub                           │
└─────────────────────────────────────────────────────────────┘
```

## Module Structure

Each module follows the same pattern:

```
module/
├── controller/   HTTP layer - request/response handling
├── service/      Business logic
├── repository/   Data access (Spring Data JPA)
├── model/        JPA entities
├── dto/          Data transfer objects
└── mapper/       MapStruct mappers
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login and get JWT |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/users/me | Get current user profile |
| PUT | /api/users/me | Update profile |
| GET | /api/users/{id} | Get user by ID |

### Activities
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/activities | Get all activities |
| POST | /api/activities | Log new activity |
| GET | /api/activities/user | Get current user's activities |
| GET | /api/activities/stats | Get current user's stats |

### Communities
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/communities | List all communities |
| POST | /api/communities | Create community |
| GET | /api/communities/{id} | Get community details |
| POST | /api/communities/{id}/join | Join community |
| GET | /api/communities/{id}/members | List members |

### Events
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/events | List all events |
| POST | /api/events | Create event (ORGANIZER+) |
| GET | /api/events/{id} | Get event details |
| POST | /api/events/{id}/register | Register for event |
| GET | /api/events/{id}/participants | List participants |

### Social Feed
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/posts | Get posts (paginated) |
| POST | /api/posts | Create post |
| POST | /api/posts/{id}/comments | Add comment |
| POST | /api/posts/{id}/like | Like/unlike post |

### Chat
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/messages | Get messages (communityId or eventId param) |
| POST | /api/messages | Send message |

### Badges
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/badges | List all badges |
| POST | /api/badges | Create badge (ADMIN) |
| GET | /api/badges/my | Get current user's badges |
| GET | /api/badges/user/{id} | Get user's badges |

### Programs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/programs | List all programs |
| POST | /api/programs | Create program (ADMIN) |
| GET | /api/programs/{id} | Get program with sessions |
| POST | /api/programs/{id}/start | Start a program |
| GET | /api/programs/my-progress | Get user's active programs |

### Rankings
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/rankings/global | Global rankings (type: weekly/monthly/alltime) |
| GET | /api/rankings/community/{id} | Community rankings |

### AI
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/ai/ask | Ask AI assistant a running question |

## Security

- **Authentication**: JWT Bearer tokens
- **Token lifetime**: 24 hours (configurable)
- **Roles**: USER, ADMIN, ORGANIZER
- **Public endpoints**: /api/auth/**, /api/rankings/**, GET /api/events/**, GET /api/communities/**
- **Protected**: All other endpoints require valid JWT

## Database Schema

### Key Tables
- `users` - User accounts and profiles
- `running_activities` - Running workout logs
- `communities` - Running groups/clubs
- `community_members` - Community membership (composite PK)
- `events` - Running events/races
- `event_registrations` - Event sign-ups
- `posts` - Social feed posts
- `comments` - Post comments
- `likes` - Post likes (unique constraint per user/post)
- `messages` - Chat messages
- `badges` - Achievement definitions
- `user_badges` - Earned badges
- `programs` - Training programs
- `program_sessions` - Individual training sessions
- `user_program_progress` - User program enrollment

## Frontend Structure

```
src/app/
├── app.config.ts          # Application bootstrap config
├── app.routes.ts          # Route definitions
├── core/
│   ├── models/            # TypeScript interfaces
│   ├── services/          # HTTP services (one per API module)
│   ├── interceptors/      # JWT auth interceptor
│   └── guards/            # Route auth guard
├── shared/
│   └── components/
│       └── layout/        # App shell with sidenav + toolbar
└── features/              # One folder per page
    ├── auth/              # Login + Register
    ├── dashboard/         # Stats overview
    ├── activities/        # Activity log
    ├── communities/       # Community browser
    ├── events/            # Event browser
    ├── feed/              # Social feed
    ├── programs/          # Training programs
    ├── rankings/          # Leaderboards
    ├── profile/           # User profile
    └── chat/              # Community chat
```

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend Framework | Spring Boot 3.2 |
| Language (Backend) | Java 21 |
| ORM | Spring Data JPA + Hibernate |
| Security | Spring Security + JWT (jjwt) |
| Object Mapping | MapStruct |
| Boilerplate Reduction | Lombok |
| Database | PostgreSQL 15 |
| Build Tool | Maven |
| Frontend Framework | Angular 17 (standalone) |
| UI Components | Angular Material 17 |
| Language (Frontend) | TypeScript 5.2 |
| Reactive Programming | RxJS 7.8 |
| Containerization | Docker + Docker Compose |
