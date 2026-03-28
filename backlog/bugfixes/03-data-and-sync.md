# Bug Fixes: 03 - Sync & Data Integrity (Items 41-60)

This document tracks the third batch of 20 bug fixes for external synchronization and data consistency.

| ID | Title | File / Location | Description |
|---|---|---|---|
| B-041 | Garmin OAuth 1.0a Drifts | `GarminOAuthService.java` | Token secrets are stored in memory in some sessions, causing 401s after server redeploy. |
| B-042 | Duplicate external_id | `schema.sql` | The unique constraint on `external_id` should encompass `user_id` as well for shared accounts. |
| B-043 | Strava "Private" runs | `StravaSyncService.java` | Does not correctly filter out private activities even if the user has opted-out of private sync. |
| B-044 | Ghost Activities | `ActivityRepository.java` | Activities deleted in Strava are not removed from the local database on subsequent syncs. |
| B-045 | Precision Loss: Km | `calculate_pace()` | Storing distance as `DECIMAL(8,2)` leads to small rounding errors appearing in the pace results. |
| B-046 | No Support: Laps | `GarminSyncService.java` | Laps aren't being parsed, only the total session summary is imported. |
| B-047 | Orphan Polyline data | `PostgreSQL` | Deleting a user doesn't immediately purge their associated large text polylines, leading to bloat. |
| B-048 | Incorrect Pace: Treadmill | `StravaSyncService.java` | "VirtualRun" type activities sometimes report duration as "elapsed_time" instead of "moving_time". |
| B-049 | Null Pointer on Stats | `DashboardService.java` | If a user has 0 activities, the "Weekly Progress" calculation returns a null pointer on the division. |
| B-050 | Missing "Source" badge | `ActivityCard.html` | Manual activities are indistinguishable from verified Strava/Garmin activities in the UI. |
| B-051 | Strava Auth Timeout | `OAuthController.java` | If a user waits too long on the Strava consent screen, the CSRF 'state' token expires on the callback. |
| B-052 | Incorrect Elevation | `RunningActivity.java` | Elevation gain stored as Integer. Some mountain runs have meters with high precision needed for VAM calculation. |
| B-053 | No Activity Heartrate | `ActivitySync.java` | If the initial sync fails for heartrate detail, it is never retried on subsequent syncs. |
| B-054 | Sync Loop: Concurrent | `StravaSyncService.java` | The `@Scheduled` task doesn't check if a manual sync for the same user is already in progress. |
| B-055 | Magic Number: HR Zones | `HeartRateService.java` | Hardcoded zones (e.g. 150-160 for Z3). Should be based on per-user Max HR/LTHR. |
| B-056 | Drive Sync Hangs | `GoogleDriveService.java` | Fetching photo albums from Drive hangs if a folder contains 1000+ images. Need pagination. |
| B-057 | Missing Post Sync | `SyncService.java` | Creating an activity manually doesn't automatically trigger a "Post" creation in the social feed. |
| B-058 | Broken Image URLs | `ActivityDetail.html` | Remote image URLs (not from Drive) are blocked by the Content Security Policy (CSP). |
| B-059 | Badge Over-award | `BadgeService.java` | A user can earn the "Marathon Finisher" badge multiple times for the same activity if they re-sync. |
| B-060 | UTC Offsets missing | `RunningActivity.java` | Activity date is stored as `DATE`, losing the time of day and timezone for synchronization. |
