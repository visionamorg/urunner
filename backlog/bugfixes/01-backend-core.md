# Bug Fixes: 01 - Backend Core (Items 1-20)

This document tracks the first batch of 20 architectural and logical bug fixes for the Spring Boot backend.

| ID | Title | File / Location | Description |
|---|---|---|---|
| B-001 | Network I/O inside Transaction | `StravaSyncService.java` | External RestTemplate calls for activity detail are made inside a `@Transactional` block, potentially exhausting DB connections. |
| B-002 | Double Sync Race Condition | `StravaSyncService.java` | Lack of a distributed lock in `syncActivities` allows two simultaneous syncs for the same user, leading to 409 errors or partial data. |
| B-003 | Magic Number: Cadence Scaling | `StravaSyncService.java` | Cadence is multiplied by 2 (hardcoded) to convert half-cadence to RPM. Use a documented constant or configuration. |
| B-004 | Magic Number: Move Time Threshold | `StravaSyncService.java` | Hardcoded 0.1km minimum distance for activity import prevents syncing short recovery intervals or sprints. |
| B-005 | Silent Failure: Date Parsing | `StravaSyncService.java` | `parseDate` falls back to `LocalDate.now()` on failure, which corrupts the activity timeline. Should throw an exception or log a critical error. |
| B-006 | Magic String: Auth Provider | `StravaSyncService.java` | Hardcoded "Strava Run" and "strava_" prefix for external IDs. Use an ID strategy class. |
| B-007 | Missing Resource Mapping | `GlobalExceptionHandler.java` | No specific handler for `DataIntegrityViolationException` (DB errors). Users get generic 500s instead of 409 Conflict. |
| B-008 | Generic Catch-All Exception | `StravaSyncService.java` | `catch (Exception e)` in the auto-sync loop swallows critical runtime errors without proper categorization. |
| B-009 | Blocking Scheduled Task | `StravaSyncService.java` | `@Scheduled` task runs sequentially. If one user's sync hangs, all subsequent users are delayed. Use a TaskExecutor. |
| B-010 | Missing DTO Validation | `RunningActivityController.java` | Input DTOs are not annotated with `@Valid`, allowing activities with negative distance or null dates. |
| B-011 | Inefficient Username Check | `StravaOAuthService.java` | `while(existsByUsername)` results in N database calls. Should use a single query for matching patterns. |
| B-012 | Hardcoded OAuth Prefix | `StravaOAuthService.java` | `oauth_` password prefix is guessable. Should use a strong, fully random secure hash for OAuth stubs. |
| B-013 | Missing Pace Bounds | `RunningActivity.java` | Pace is calculated as `minutes / distance`. If distance is slightly > 0 but extremely small, pace becomes infinite. |
| B-014 | Timezone Mismatch | `User.java` | `LocalDateTime.now()` uses server local time. Use `Instant` or `ZonedDateTime` for global consistency. |
| B-015 | Missing @PreUpdate on Events | `Event.java` | `updated_at` might not be updating automatically for events if the `@EntityListeners` or `@PreUpdate` is missing. |
| B-016 | Hardcoded JWT Secret | `JwtService.java` | Fallback secret hardcoded in code if environment variable is missing. Should fail fast in production. |
| B-017 | Insecure CORS Policy | `SecurityConfig.java` | `allowedOrigins("*")` might be active in certain dev profiles, leading to CSRF risks. |
| B-018 | Missing Rate Limiting | `AuthController.java` | No limit on login/register attempts, making the app vulnerable to brute force. |
| B-019 | JPA N+1 Query on Feed | `PostRepository.java` | Fetching posts doesn't join `author` or `likes`, causing N+1 queries during feed scroll. |
| B-020 | Large Response Body | `RankingController.java` | Returning the entire leaderboard without pagination. Will crash as community grows. |
