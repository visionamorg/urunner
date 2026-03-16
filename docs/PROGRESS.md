# RunHub — Developer Progress Log

A chronological record of every prompt, decision, and change made during the development of the RunHub platform. Use this as a reference to understand what was built, why, and how.

---

## Session 1 — Project Bootstrap & Initial Commit

### Prompt
> "Run the application"

### What was done
- Analyzed the full-stack project structure: Spring Boot 3 / Java 21 backend, Angular 17 frontend, PostgreSQL 15 database, Docker Compose orchestration.
- Identified missing `GarminApi` class from scribejava 8.3.3 library (it was removed in newer versions).
- **Created** `backend/src/main/java/com/github/scribejava/apis/GarminApi.java` — custom OAuth 1.0a implementation for Garmin Connect endpoints.
- Fixed PostgreSQL `user_role` enum casting error in Hibernate 6 by adding `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` to `User.role`.
- Fixed enum column type mismatches for `registration_status`, `programs.level`, `user_program_progress.status`, `community_members.role` by running `ALTER TABLE ... ALTER COLUMN ... TYPE VARCHAR(20)` migrations directly on the DB.
- Application booted successfully with all services healthy.

### Key files changed
- `backend/src/main/java/com/github/scribejava/apis/GarminApi.java` *(new)*
- `backend/src/main/java/com/runhub/users/model/User.java` — added `@JdbcTypeCode` annotation

---

## Session 2 — Strava OAuth Integration

### Prompt
> "My Strava API information — update so I can use Strava as auth for now"
> (Provided: Client ID `201864`, Client Secret, Access Token, Refresh Token)

### What was done
- Updated `application.yml` to read Strava credentials from environment variables (`STRAVA_CLIENT_ID`, `STRAVA_CLIENT_SECRET`).
- Verified Strava OAuth callback flow: authorization → code exchange → JWT issuance.
- Debugged the "Authentication failed" screen after accepting Strava OAuth — traced it to a missing user profile fetch step post-callback.
- Fixed `OAuthCallbackComponent` which had a missing `MatIconModule` import causing a build error; replaced with plain `<span class="material-icons">` and CSS spinner.

### Key files changed
- `backend/src/main/resources/application.yml` — Strava credentials via env vars
- `frontend/src/app/features/auth/oauth-callback/oauth-callback.component.ts` — removed MatIconModule dependency

---

## Session 3 — Secrets Management & GitHub Push

### Prompt
> "Put the keys in env variables and ignore the .env file but include .env.example. Push to new GitHub repo `urunner` (user: visionamorg, email: abdo.elmahdi9901@gmail.com)"

### What was done
- Created `.env` at project root with all secrets:
  ```
  STRAVA_CLIENT_ID=201864
  STRAVA_CLIENT_SECRET=13caf0c2749c95a7497e964825c27b67c31c6d14
  JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  GOOGLE_DRIVE_API_KEY=AIzaSyAbu4NRx1BhrLYpDD_aDvpUFdoBFpbiOC8
  POSTGRES_DB=runhub
  POSTGRES_USER=runhub
  POSTGRES_PASSWORD=runhub123
  ```
- Created `.env.example` with placeholder values for documentation.
- Updated `.gitignore` to exclude `.env` but commit `.env.example`.
- Updated `docker-compose.yml` to use `${VAR}` substitution for all secrets.
- Configured git with user `visionamorg` / `abdo.elmahdi9901@gmail.com`.
- Created GitHub repo `urunner` and pushed initial commit.

### Key files changed
- `.env` *(new, git-ignored)*
- `.env.example` *(new)*
- `.gitignore`
- `docker-compose.yml`

### Git commit
`Initial commit — RunHub full-stack running platform`

---

## Session 4 — Frontend Redesign with Tailwind CSS

### Prompt
> "Refont the frontend to use Tailwind CSS and be responsive and good looking, energetic, perfect user experience"

### What was done
- Installed Tailwind CSS v3 into the Angular project (`tailwind.config.js`, `postcss.config.js`).
- Defined brand color palette in `tailwind.config.js`:
  - `brand-bg`: `#050a18` (deep dark navy)
  - `brand-card`: `#0d1526`
  - `brand-surface`: `#111827`
  - `brand-border`: `#1e293b`
- Added `safelist` for dynamic Tailwind classes containing `/` (opacity modifiers) to prevent purging.
- Created reusable component classes in `styles.scss`:
  - `.btn-primary` — orange-to-red gradient button
  - `.card` — dark card with border and rounded corners
  - `.input-field` — styled dark input
  - `.label` — form label
- Rebuilt all page components:
  - **Layout** — fixed desktop sidebar (w-64), mobile top bar + hamburger + slide-in overlay + bottom nav bar
  - **Dashboard** — stats cards, recent activity
  - **Events** — event cards with registration status badges
  - **Rankings** — leaderboard rows with rank badges
  - **Chat** — community list + message thread
  - **Profile** — user stats, activity history
  - **Auth pages** — login, register, OAuth callback
- Fixed Angular template parser errors: `[class.bg-orange-500/10]` and `[class.hover:bg-brand-surface]` are rejected by Angular. Replaced with `[ngClass]="condition ? 'class-name' : ''"` everywhere.

### Key files changed
- `frontend/tailwind.config.js` *(new)*
- `frontend/postcss.config.js` *(new)*
- `frontend/src/styles.scss`
- `frontend/src/app/shared/components/layout/layout.component.html`
- All feature component HTML files

### Git commit
`Redesign frontend with Tailwind CSS — dark energetic UI`

---

## Session 5 — Database Inspection & Seed Password Fix

### Prompt
> "Can you show me what is inserted into the database right now?"
> "I used alice@example.com on the login but it's not working?"
> "They say invalid credentials on the frontend login page"

### What was done
- Ran `SELECT id, username, email, role FROM users;` to inspect DB contents — confirmed 10 seed users present.
- Diagnosed password issue: BCrypt hash in `seed.sql` did not match `password123` (hash was generated with wrong input).
- Fixed by registering a test user through the API to get a valid BCrypt hash, then running:
  ```sql
  UPDATE users SET password = '$2a$10$<valid-hash>' WHERE email IN ('alice@example.com', ...);
  ```
- Updated all 10 seed accounts with correct hash. Login now works with `password123`.

### Key files changed
- `database/seed.sql` — corrected BCrypt password hashes

---

## Session 6 — Community Feature: Feed, Google Drive Sync, Like/Comment

### Prompt
> "As user I want to join any community. Each community will have a source of pictures from Google Drive which we will get and show on the community feed as +21 pictures like Facebook post. In the same community feed people can comment and like."

### What was done

#### Backend
- Added columns to `communities` table: `drive_folder_id`, `cover_url`, `is_private`.
- Added columns to `posts` table: `community_id`, `post_type`, `photo_urls`.
- Created `GoogleDriveService` — calls Google Drive API v3 REST using API key:
  - Endpoint: `https://www.googleapis.com/drive/v3/files?q='${folderId}'+in+parents&fields=files(id,mimeType)&key=${apiKey}`
  - Returns image URLs as `https://drive.google.com/uc?export=view&id={fileId}`
- Added `GOOGLE_DRIVE_API_KEY` to `application.yml` via env var.
- Added `syncDrivePhotos(communityId, user)` to `CommunityService` — fetches images from Drive folder, creates a `PHOTO_ALBUM` post.
- Updated `FeedService.createPhotoPost()` to accept list of URLs.
- Added `POST /api/communities/{id}/drive/sync` endpoint.
- Updated `PostDto` to include `photoUrls: List<String>` (parsed from JSON-stored `photo_urls` TEXT column).

#### Frontend
- Built `CommunityDetailComponent` with tabs: Feed, Members, Settings.
- Implemented Facebook-style photo grid layout:
  - 1 photo → full width
  - 2 photos → equal columns
  - 3 photos → left large + right 2 stacked
  - 4+ photos → 2×2 grid with `+N` overlay on 4th image
- Implemented `toggleLike()` with optimistic update (reverts on error).
- Implemented `toggleComments()` with lazy comment loading.
- Implemented `addComment()` — inline comment form per post.
- Drive sync button visible to admins in community header.
- `CommunityService` in Angular: added `getFeed()`, `createPost()`, `syncDrive()`.

### Key files changed
- `backend/src/main/java/com/runhub/communities/service/GoogleDriveService.java` *(new)*
- `backend/src/main/java/com/runhub/communities/service/CommunityService.java`
- `backend/src/main/java/com/runhub/communities/controller/CommunityController.java`
- `backend/src/main/resources/application.yml`
- `frontend/src/app/features/communities/community-detail/community-detail.component.ts`
- `frontend/src/app/features/communities/community-detail/community-detail.component.html`
- `frontend/src/app/core/services/community.service.ts`

### Git commit
`Add community feed, Google Drive photo sync, like/comment system`

---

## Session 7 — Admin Controls, Invite System, Pin/Delete Posts

### Prompt
> "The one who created the community is an admin so he needs to have more capabilities to control the community. We need also an invite space to invite people to the community. For Google Drive we need a button to sync all folders into posts on the community."

### What was done

#### Backend — New Entity: CommunityInvite
- Created `CommunityInvite` JPA entity (`community_invites` table):
  - Fields: `id`, `community`, `invitedUser`, `invitedBy`, `token` (UUID auto-generated), `status` (PENDING/ACCEPTED/DECLINED/CANCELLED), `createdAt`, `expiresAt` (7 days from creation).
- Created `CommunityInviteRepository` with custom finders:
  - `findByCommunityIdAndStatus(communityId, status)`
  - `findByInvitedUserIdAndStatus(userId, status)`
  - `findByToken(token)`
  - `existsByCommunityIdAndInvitedUserIdAndStatus(...)`
- Created `InviteDto` — full invite data including community info, usernames, token, status, timestamps.
- Created `InviteRequest` — simple `{ username }` request body.

#### Backend — Post Model Updates
- Added `deleted` (boolean, default false) — soft delete flag.
- Added `pinned` (boolean, default false) — pin to top of feed.
- Added `getCommunityId()` helper method.
- DB migration: `ALTER TABLE posts ADD COLUMN deleted BOOLEAN DEFAULT FALSE`, same for `pinned`.

#### Backend — CommunityService: New Methods
| Method | Description |
|--------|-------------|
| `kickMember(communityId, targetUserId, admin)` | Removes member, decrements count. Blocks kicking creator. |
| `changeMemberRole(communityId, targetUserId, newRole, admin)` | Changes role to ADMIN/MODERATOR/MEMBER. |
| `deletePost(communityId, postId, admin)` | Soft-deletes post (sets `deleted=true`). |
| `pinPost(communityId, postId, admin)` | Toggles `pinned` flag on post. |
| `inviteUser(communityId, username, admin)` | Creates PENDING invite with UUID token. Checks no duplicate pending invite. |
| `getCommunityInvites(communityId, admin)` | Returns all PENDING invites for a community. |
| `getMyInvites(user)` | Returns all PENDING invites for the logged-in user. |
| `respondToInvite(token, accept, user)` | Accepts or declines invite; on accept calls `joinCommunity`. |
| `cancelInvite(communityId, inviteId, admin)` | Sets invite status to CANCELLED. |

- Added `pendingInviteCount` to `toDtoWithMembership()` — shown in `CommunityDto` for admins.

#### Backend — New API Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `DELETE` | `/api/communities/{id}/members/{userId}` | Kick member |
| `PUT` | `/api/communities/{id}/members/{userId}/role` | Change member role |
| `DELETE` | `/api/communities/{id}/posts/{postId}` | Delete post |
| `POST` | `/api/communities/{id}/posts/{postId}/pin` | Toggle pin post |
| `POST` | `/api/communities/{id}/invites` | Send invite by username |
| `GET` | `/api/communities/{id}/invites` | List pending invites (admin) |
| `DELETE` | `/api/communities/{id}/invites/{inviteId}` | Cancel invite |
| `GET` | `/api/communities/invites/mine` | My pending invites |
| `POST` | `/api/communities/invites/{token}/accept` | Accept invite |
| `POST` | `/api/communities/invites/{token}/decline` | Decline invite |

#### Frontend — Model Updates
- `community.model.ts`: Added `pendingInviteCount?: number` to `Community`, added `InviteDto` interface.
- `post.model.ts`: Added `pinned: boolean`, `deleted?: boolean` fields.

#### Frontend — CommunityService: New Methods
- `kickMember(communityId, userId)`
- `changeMemberRole(communityId, userId, role)`
- `deletePost(communityId, postId)`
- `pinPost(communityId, postId)`
- `invite(communityId, username)` → `InviteDto`
- `getCommunityInvites(communityId)` → `InviteDto[]`
- `cancelInvite(communityId, inviteId)`
- `getMyInvites()` → `InviteDto[]`
- `acceptInvite(token)`
- `declineInvite(token)`

#### Frontend — community-detail component: New UI
- **4 tabs** (Feed / Members / Invites [admin] / Settings [admin])
- **Feed tab**: Post cards show pin/delete buttons for admins; pinned posts have orange border + "Pinned" badge with push_pin icon.
- **Members tab**: Admins see role `<select>` dropdown (MEMBER/MODERATOR/ADMIN) and kick button per member. Non-admins see static role badge.
- **Invites tab** (admin only):
  - Send invite form: username input + "Send Invite" button with loading state.
  - Success/error messages with auto-dismiss.
  - Pending invites list: avatar, username, inviter, timestamp, cancel button.
  - Count badge on tab.
- **Header**: Pending invite count badge shown next to "Joined" tag; Drive sync button disabled if no `driveFolderId` configured.

### Key files changed
- `backend/src/main/java/com/runhub/communities/model/CommunityInvite.java` *(new)*
- `backend/src/main/java/com/runhub/communities/repository/CommunityInviteRepository.java` *(new)*
- `backend/src/main/java/com/runhub/communities/dto/InviteDto.java` *(new)*
- `backend/src/main/java/com/runhub/communities/dto/InviteRequest.java` *(new)*
- `backend/src/main/java/com/runhub/communities/service/CommunityService.java`
- `backend/src/main/java/com/runhub/communities/controller/CommunityController.java`
- `backend/src/main/java/com/runhub/feed/model/Post.java`
- `backend/src/main/java/com/runhub/communities/dto/CommunityDto.java`
- `frontend/src/app/core/models/community.model.ts`
- `frontend/src/app/core/models/post.model.ts`
- `frontend/src/app/core/services/community.service.ts`
- `frontend/src/app/features/communities/community-detail/community-detail.component.ts`
- `frontend/src/app/features/communities/community-detail/community-detail.component.html`

### Git commit
`feat(communities): add admin controls, invite system, pin/delete posts`

---

## Bug Fixes & Technical Decisions

### Angular Template Class Binding with `/` and `:`
- **Problem**: `[class.bg-orange-500/10]` and `[class.hover:bg-brand-surface]` crash the Angular template parser because `/` and `:` are not valid in property binding names.
- **Fix**: Use `[ngClass]="condition ? 'bg-orange-500/10' : ''"` syntax everywhere.
- **Affected files**: `events.component.html`, `rankings.component.html`, `chat.component.html`, `community-detail.component.html`

### Hibernate 6 + PostgreSQL Custom Enum Types
- **Problem**: Hibernate 6 changed how it handles PostgreSQL custom enum types. Querying a column backed by a custom DB enum type (`user_role`) throws a cast exception.
- **Fix**: Annotate the Java enum field with `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` alongside `@Enumerated(EnumType.STRING)`.
- For other tables (`event_registrations`, `programs`, `community_members`, `user_program_progress`): converted the enum columns to `VARCHAR(20)` via `ALTER TABLE` migration — simpler and avoids further Hibernate compatibility issues.

### Community Creator Auto-Membership
- When a community is created, the creator is automatically added as an `ADMIN` `CommunityMember`.
- `requireAdmin()` helper checks both membership role AND creator ID to handle edge cases.

### Google Drive Public Folder Access
- Uses Drive API v3 REST with API key (no OAuth required for public folders).
- Query: `q='${folderId}'+in+parents&mimeType contains 'image/'`
- Image serving URL format: `https://drive.google.com/uc?export=view&id={fileId}`

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                   Docker Compose                    │
│                                                     │
│  ┌──────────────┐   ┌──────────────┐               │
│  │   Frontend   │   │   Backend    │               │
│  │  Angular 17  │──▶│ Spring Boot  │               │
│  │  Tailwind 3  │   │   Java 21    │               │
│  │  Port :80    │   │  Port :8080  │               │
│  └──────────────┘   └──────┬───────┘               │
│                            │                        │
│                     ┌──────▼───────┐               │
│                     │  PostgreSQL  │               │
│                     │   Port 5432  │               │
│                     └──────────────┘               │
└─────────────────────────────────────────────────────┘
```

**Frontend → Backend**: All API calls proxied through Nginx (`/api/*` → `backend:8080`).

**Authentication**: JWT tokens issued after Strava OAuth or local login. Token stored in localStorage, sent as `Authorization: Bearer <token>` header.

**External APIs**:
- Strava OAuth 2.0 — user authentication via Strava account
- Google Drive API v3 — fetch images from public folder using API key
- Garmin OAuth 1.0a — (configured, not yet fully integrated in UI)

---

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `POSTGRES_DB` | Database name | `runhub` |
| `POSTGRES_USER` | DB username | `runhub` |
| `POSTGRES_PASSWORD` | DB password | `runhub123` |
| `JWT_SECRET` | 256-bit hex secret for JWT signing | `404E635266...` |
| `STRAVA_CLIENT_ID` | Strava OAuth app client ID | `201864` |
| `STRAVA_CLIENT_SECRET` | Strava OAuth app client secret | `13caf0c2...` |
| `GOOGLE_DRIVE_API_KEY` | Google Drive API v3 key | `AIzaSy...` |

Copy `.env.example` to `.env` and fill in real values before running.

---

## Running the Project

```bash
# Clone
git clone https://github.com/visionamorg/urunner.git
cd urunner

# Set up secrets
cp .env.example .env
# Edit .env with real values

# Start everything
docker compose up -d

# App available at http://localhost
# API at http://localhost/api
```

---

*Last updated: 2026-03-16*
