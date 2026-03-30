# Garmin Integration — Implementation Guide

## Stories Summary

### G001 — OAuth 1.0a Stabilization
`GarminOAuthService` now exposes `disconnectGarmin(user)` (clears tokens, resets authProvider to LOCAL) and `getGarminStatus(user)` (returns `{connected, garminUserId}`). Two new endpoints were added to `OAuthController`: `POST /api/oauth/garmin/disconnect` and `GET /api/oauth/garmin/status`. The `exchangeTokenAndUpsertUser` method wraps the ScribeJava token exchange in a try-catch to surface clean error messages. The profile page now includes a "Connected Accounts" card showing Garmin connection status with Connect/Disconnect buttons.

### G002 — Real-time Webhook Activity Sync
`AsyncConfig.java` enables `@EnableAsync` so that webhook processing runs off the request thread. `GarminWebhookController` at `POST /api/webhooks/garmin` accepts Garmin push notifications, verifies the HMAC-SHA1 signature (when `GARMIN_WEBHOOK_SECRET` is set), and delegates to `GarminSyncService.processWebhookActivity()` asynchronously. The webhook handler also processes `deRegistrations` (clears tokens) and `userConsentStatus` events. `/api/webhooks/**` is added to SecurityConfig's permit-all list.

### G003 — Manual .FIT File Import
`GarminFitService` validates the `.FIT` magic header (bytes 8–11 = `.FIT`), computes a SHA-256 hash of the raw bytes for deduplication (`externalId = "fit_" + hash`), and parses session data using an inline pure-Java FIT binary parser (`FitParser` inner class). The parser reads definition messages to learn field layouts and extracts session message fields (distance, elapsed time, avg/max HR, cadence, ascent, start time). `GarminFitController` at `POST /api/fit/upload` exposes the endpoint. The activities page has a "Upload .FIT" label-button that triggers a hidden file input.

### G004 — Push Structured Workouts to Garmin
`GarminTrainingService.pushWorkoutToGarmin(user, sessionId)` fetches a `ProgramSession`, builds a Garmin workout JSON (warmup / main interval / cooldown steps with distance or time duration), and POSTs it to `https://apis.garmin.com/training-api/rest/workout` using a signed OAuth1 request. `GarminTrainingController` exposes `POST /api/garmin/training/workout/{sessionId}`.

### G005 — Push Training Programs to Calendar
`GarminTrainingService.syncProgramToGarmin(user, progressId, nextWeekOnly)` iterates over all sessions of the user's active `UserProgramProgress`, calculates scheduled dates from `startedAt + (weekNumber-1)*7 + (dayNumber-1)` days, pushes each as a Garmin workout, and schedules it via `POST https://apis.garmin.com/training-api/rest/schedule`. Batch processing sleeps 100 ms every 20 requests. Endpoints: `POST /api/garmin/training/program/{progressId}/sync` (all) and `POST /api/garmin/training/program/{progressId}/sync-week` (next 7 days only).

### G006 — Sync Core Health & Readiness Metrics
`HealthMetric` entity (table `health_metrics`, unique on `user_id + date`) stores resting HR, sleep score, VO2 max, fitness age, HRV status, body battery, and stress level. `GarminHealthSyncService.syncHealthData(user)` fetches the last 7 days from three Garmin Wellness API endpoints (dailies, sleeps, userMetrics), merges them into `HealthMetric` rows, and skips already-stored dates. `HealthMetricController` exposes `POST /api/garmin/health/sync` and `GET /api/garmin/health/metrics`. The dashboard shows the last 7 days as three SVG line charts (Resting HR, Sleep Score, Body Battery) with a Sync button.

### G007 — LiveTrack Integration & Mirroring
`LiveTrackingSession` gained a `garminLiveTrackUrl` VARCHAR(500) column (auto-migrated by Hibernate). When `GarminWebhookController` processes an activity summary that contains a `liveTrackingUrl` field, it finds or creates a `LiveTrackingSession` for that Garmin user and saves the URL. `GET /api/live-tracking/active` returns all active sessions including `garminLiveTrackUrl`. The feed page shows a pulsing "LIVE NOW" section at the top with clickable runner avatars that open the Garmin LiveTrack URL or RunHub tracking URL.

---

## Configuration / Environment Variables

| Variable | Purpose | Default |
|---|---|---|
| `GARMIN_CONSUMER_KEY` | Garmin OAuth 1.0a consumer key | required |
| `GARMIN_CONSUMER_SECRET` | Garmin OAuth 1.0a consumer secret | required |
| `GARMIN_WEBHOOK_SECRET` | HMAC-SHA1 secret for webhook signature verification | `""` (verification skipped if empty) |

Add `GARMIN_WEBHOOK_SECRET` to your `.env` file and register your webhook URL (`https://your-domain/api/webhooks/garmin`) in the Garmin Developer Portal under your application's webhook configuration.

---

## Known Limitations

- **FIT parser**: The inline binary parser implements a basic subset of the FIT protocol. Complex FIT files with developer data fields, nested messages, or compressed timestamp records may not parse fully; in that case the activity is still stored with whatever fields were extracted (or 0 distance as fallback).
- **Garmin Training API**: The workout push and calendar scheduling endpoints require your Garmin Connect IQ application to have the **Training** permission scope approved. This is a separate approval process from the Wellness API.
- **OAuth 1.0a token store**: Request tokens are stored in a `ConcurrentHashMap` in memory. In a multi-instance deployment, the callback must reach the same instance that initiated the OAuth flow (use sticky sessions or replace with Redis).
- **Health sync window**: `syncHealthData` currently fetches only the last 7 days. Extend `startEpoch` in `GarminHealthSyncService` to backfill longer history.
- **Webhook deduplication**: Garmin may deliver the same activity summary multiple times. The existing `existsByExternalId` check in `GarminSyncService` handles this correctly.

---

## For Coach Stories (GC-001 to GC-006)

### GC-001 — Athlete-Coach Linkage & Permission Hub

`CoachingConnection` entity (table `coaching_connections`) links a coach `User` to an athlete `User` with `garminAccessLevel` (BASIC/FULL), `status` (PENDING/ACTIVE/REVOKED), and an `inviteToken` UUID. `CoachingService` handles invite, accept, and revoke flows. The profile page shows a "My Coaches" card with an Unlink button for each active coaching connection.

New API endpoints:
- `POST /api/coaching/invite` — coach sends invite by username or email
- `POST /api/coaching/accept/{token}` — athlete accepts via invite token
- `DELETE /api/coaching/{id}/revoke` — either party revokes
- `GET /api/coaching/my-athletes` — coach lists active athletes
- `GET /api/coaching/my-coaches` — athlete lists active coaches
- `GET /api/coaching/pending-invites` — athlete lists pending invites

### GC-002 — Unified Team Performance Feed

`GET /api/coaching/team/feed?from=YYYY-MM-DD&to=YYYY-MM-DD` aggregates all activities for the coach's active athletes, sorts by date descending, and returns the latest 50 entries with fields: `activityId, athleteId, athleteUsername, athleteProfileImageUrl, title, distanceKm, durationMinutes, paceMinPerKm, avgHeartRate, activityDate, source`. Rendered in the Feed tab of the Coach Hub page.

### GC-003 — Team Readiness Dashboard

`GET /api/coaching/team/readiness` fetches the most recent `HealthMetric` record for each active athlete (via `findTopByUserIdOrderByDateDesc`) and computes a `risk` level: GREEN (sleep ≥ 75 AND battery ≥ 70 AND RHR ≤ 65), YELLOW (sleep ≥ 50 OR battery ≥ 40), or RED otherwise. Displayed as a sortable table in the Readiness tab with color-coded risk dots.

### GC-004 — Bulk Workout Push to Garmin Calendars

`BulkPushRequest` carries a `sessionId` and list of `athleteIds`. `GarminClipboardService.bulkPushWorkout` verifies an ACTIVE coaching connection exists for each athlete, then calls the existing `GarminTrainingService.pushWorkoutToGarmin` per athlete and collects per-athlete success/error results into `BulkPushResultDto`. Exposed at `POST /api/garmin/clipboard/bulk-push`. The Bulk Push tab in Coach Hub shows athlete checkboxes, a session ID input, and the per-athlete result cards.

### GC-005 — Coach Performance Analytics (CTL/ATL/TSB)

`PerformanceAnalyticsService.getAthleteLoadMetrics` loads the last 90 days of activities for a user, groups by date to sum daily TSS (simplified: distance×10 + avgHR×0.1×duration/60), and applies exponential moving averages with factors 2/43 (CTL/fitness) and 2/8 (ATL/fatigue) to produce TSB (form = CTL − ATL). Returns current values plus a 30-day trend array for charting. `GET /api/coaching/team/analytics` calls this for all active athletes and enriches with username. The Analytics tab renders per-athlete CTL/ATL/TSB cards with an SVG line chart.

New API endpoint:
- `GET /api/coaching/team/analytics`

### GC-006 — Coach-Athlete Feedback Hub

`CoachingComment` entity (table `coaching_comments`) stores per-activity feedback from a coach with optional `rating` (1–10), `lapNumber`, and `pinnedToAthleteDashboard` flag. `CoachingCommentService.addComment` verifies the coach has an ACTIVE connection with the activity owner before saving. The activity detail page includes a "Coach Feedback" section listing all comments with star ratings and a form for coaches to post new feedback.

New API endpoints:
- `POST /api/coaching/comments` — add coaching comment to an activity
- `GET /api/coaching/activities/{id}/comments` — list comments for an activity
- `PUT /api/coaching/comments/{id}/pin` — pin a comment to the athlete's dashboard
