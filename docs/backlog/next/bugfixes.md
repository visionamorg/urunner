# Bug Fixes Backlog

Actionable bugs identified across the codebase. Fix before or alongside new features.

---

## 🔴 Backend Core (B-001 – B-020)

| ID | Location | Bug |
|----|----------|-----|
| B-001 | `StravaSyncService.java` | Network I/O (RestTemplate calls) inside `@Transactional` block — exhausts DB connections |
| B-002 | `StravaSyncService.java` | Race condition: no distributed lock → duplicate sync on concurrent requests → 409 or partial data |
| B-003 | `StravaSyncService.java` | Magic number: cadence `× 2` hardcoded — use named constant |
| B-004 | `StravaSyncService.java` | Magic number: 0.1km minimum distance blocks syncing short intervals/sprints |
| B-005 | `StravaSyncService.java` | Silent failure: `parseDate` falls back to `LocalDate.now()` — corrupts activity timeline |
| B-006 | `StravaSyncService.java` | Magic string: `"strava_"` prefix hardcoded — use an ID strategy class |
| ~~B-007~~ | `GlobalExceptionHandler.java` | No handler for `DataIntegrityViolationException` — users get 500 instead of 409  ✅ **Fixed 2026-04-10** |
| B-008 | `StravaSyncService.java` | `catch (Exception e)` swallows critical errors in the auto-sync loop |
| B-009 | `StravaSyncService.java` | `@Scheduled` task is sequential — one hung user blocks all others. Use TaskExecutor |
| B-010 | `RunningActivityController.java` | DTOs not annotated `@Valid` — allows negative distance or null dates |
| B-011 | `StravaOAuthService.java` | `while(existsByUsername)` loop = N DB calls — use a single pattern-match query |
| B-012 | `StravaOAuthService.java` | `oauth_` password prefix is guessable — use strong random hash |
| B-013 | `RunningActivity.java` | Pace = minutes / distance — if distance near-zero, pace becomes infinite (divide-by-zero) |
| B-014 | `User.java` | `LocalDateTime.now()` uses server local time — use `Instant` for global consistency |
| B-015 | `Event.java` | `updated_at` may not auto-update if `@PreUpdate` / `@EntityListeners` missing |
| ~~B-016~~ | `JwtService.java` | Fallback JWT secret hardcoded in code — must fail fast if env var missing  ✅ **Fixed 2026-04-10** |
| B-017 | `SecurityConfig.java` | `allowedOrigins("*")` may be active in dev profiles — CSRF risk |
| B-018 | `AuthController.java` | No rate limiting on login/register — brute force vulnerability |
| ~~B-019~~ | `PostRepository.java` | N+1 query: feed fetch doesn't join `author` or `likes` — causes N+1 on every scroll  ✅ **Fixed 2026-04-10** |
| B-020 | `RankingController.java` | Full leaderboard returned without pagination — will break at scale |

---

## 🟡 Frontend & UI (B-021 – B-040)

| ID | Location | Bug |
|----|----------|-----|
| B-021 | `DashboardComponent.ts` | No loading states on charts — app feels frozen on slow connections |
| B-022 | `ActivityDetailComponent.ts` | RxJS observables not unsubscribed `OnDestroy` — memory leak |
| B-023 | `LoginComponent.html` | Validation errors not shown — user sees button pulse but no error message |
| B-024 | `environment.ts` | `/api/` hardcoded in some services instead of a configurable constant |
| ~~B-025~~ | `LayoutComponent.css` | Bottom nav covers last list item on iPhone — safe-area inset missing  ✅ **Fixed 2026-04-10** |
| B-026 | `ActivityCardComponent.html` | Dates show as `2024-03-28` not localized (`Mar 28`) |
| ~~B-027~~ | `ActivityDetailComponent.ts` | Delete button has no confirmation dialog — immediate destructive action  ✅ **Fixed 2026-04-10** |
| ~~B-028~~ | `AuthInterceptor.ts` | 401 on JWT expiry not handled → loop of failed requests  ✅ **Fixed 2026-04-10** |
| B-029 | `index.html` | White flash on startup before dark theme JS loads |
| B-030 | `src/assets` | Default Angular favicon, not RunHub branded |
| B-031 | `MapComponent.ts` | Marathon-length polyline crashes low-end devices — needs point simplification |
| ~~B-032~~ | `CommunitiesComponent.html` | No "empty state" when user has no communities — blank screen shown  ✅ **Fixed 2026-04-10** |
| ~~B-033~~ | `AvatarComponent.html` | Broken profile images show 404 icon instead of initials placeholder  ✅ **Fixed 2026-04-10** |
| B-034 | `CommunitySearch.ts` | Every keystroke fires an API call — needs `debounceTime(300)` |
| B-035 | `ExportStudioComponent.html` | Some buttons have hardcoded English strings (not i18n-ready) |
| B-036 | `RegisterComponent.ts` | No password strength validation — allows `123456` |
| B-037 | `AppRoutes.ts` | Nested community routes produce overlong breadcrumbs on mobile |
| B-038 | `AppRoutingModule.ts` | Scroll position lost on back navigation — needs `scrollPositionRestoration: 'enabled'` |
| B-039 | `PostComponent.html` | Activity photos missing `alt` tags — accessibility issue |
| B-040 | `ToastService.ts` | Double-click triggers duplicate toast stacking |

---

## 🟢 Data, Sync & Infrastructure (B-041 – B-060)

| ID | Location | Bug |
|----|----------|-----|
| B-041 | `GarminWebhookController.java` | No signature verification on Garmin webhooks — open to spoofed payloads |
| B-042 | `application.yml` | Garmin token refresh logic not fully implemented — tokens expire silently |
| B-043 | Database | `running_activities` has no index on `user_id + start_date` — slow leaderboard queries |
| B-044 | `CommunityService.java` | `memberCount` can go negative if a non-member tries to leave — no guard |
| B-045 | `FeedService.java` | Deleted posts (`deleted=true`) still appear in the global feed — filter missing |
| B-046 | Docker | Backend container restarts not graceful — in-flight requests lost |
| B-047 | `docker-compose.yml` | No health check on Postgres before backend starts — timing-dependent boot failures |
| B-048 | `SecurityConfig.java` | Some `/api/garmin/**` endpoints not consistently whitelisted for webhook receiver |
| B-049 | `ExportTemplateController.java` | No file size validation on template uploads — server can be overloaded |
| B-050 | `seed.sql` | Seed data timestamps are hardcoded to 2024 — leaderboards show 0 for current week/month |

---

## How to Fix

Pick any bug, open the file listed, fix and test with:
```bash
docker compose build backend && docker compose up -d
docker compose logs backend --tail=30
```
For frontend bugs:
```bash
docker compose build frontend && docker compose up -d
```
