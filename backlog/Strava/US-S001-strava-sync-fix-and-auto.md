# User Story: US-S001 - The Ultimate Strava Sync (Fix & Auto) 🚴‍♂️⚡️

### Status: DONE

**As a** runner using Strava,
**I want** to connect my account once and have my activities sync automatically without duplicates,
**So that** my Runhub dashboard is always up-to-date and reliable without any manual maintenance.

## 🔴 The Problem (Bug Report)
- Users are reporting that clicking "Sync Data" multiple times results in **duplicated exercises**. This ruins the total mileage statistics and community rankings.
- Currently, there is no **Automatic Synchronization**. Users are forced to manually trigger a sync every time they finish a run.

## ✅ Acceptance Criteria (The Fix)

### 1. Robust Duplication Prevention
- [ ] **Database Integrity**: The `running_activities` table in `schema.sql` MUST have a `UNIQUE` constraint on the `external_id` column.
- [ ] **Model Enforcement**: Update the `RunningActivity` JPA entity to include `unique = true` on the `external_id` field.
- [ ] **Graceful Conflict Resolution**: In `StravaSyncService`, wrap the `save()` call in a catch block for `DataIntegrityViolationException`. If a duplicate is detected, it should simply be logged and skipped rather than failing the entire sync process.

### 2. Set-and-Forget Auto-Sync
- [ ] **Spring Scheduler**: Implement a periodic background task in `StravaSyncService` annotated with `@Scheduled(fixedRate = 3600000)` (every 60 minutes).
- [ ] **Batch Processing**: The scheduler must:
    - Query the `UserRepository` for all users with a non-null `provider_access_token` and `auth_provider = 'STRAVA'`.
    - Iteratively call the `syncActivities(user)` method for each.
- [ ] **Token Management**: Ensure the `stravaOAuthService.refreshAccessTokenIfNeeded(user)` logic is called at the start of every background sync to prevent 401 Unauthorized errors.

## 🛠 Technical Notes for Claude Code
1. **Repository Update**: Add `findAllByAuthProviderAndProviderAccessTokenIsNotNull(AuthProvider provider)` to `UserRepository`.
2. **Idempotency**: Use the `strava_{activity_id}` format consistently to identify activities across different platforms.
3. **Optimism vs. Pessimism**: The database unique constraint is the final safety net; the code should still check `existsByExternalId` to avoid unnecessary API calls for transformation logic.

---
**Status**: Ready for Implementation
**Priority**: High (Functional Bug Fix + UX Enhancement)
