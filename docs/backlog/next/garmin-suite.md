# Garmin Integration Suite (MVP3)

Complete the Garmin integration beyond what was shipped in US-006 (workout builder).

---

## US-G001 — OAuth 1.0a Stabilization

**Priority:** 🔴 High — blocks all other Garmin stories

**Problem:** The Garmin OAuth 1.0a token flow can silently expire or fail without clear user feedback. The current `GarminApi` is a custom implementation that may have edge cases under token refresh.

**Scope:**
- Token expiry detection and automatic silent refresh
- Clear "Reconnect Garmin" prompt when refresh fails
- Store `accessToken` + `accessTokenSecret` with expiry tracking
- Test against real Garmin accounts (token TTL is ~1 year but webhooks need active tokens)

---

## US-G002 — Real-Time Webhook Activity Sync

**Priority:** 🔴 High

**Problem:** Activities are only synced when the user manually triggers a sync. Garmin sends webhook events when a new activity is recorded — we must consume them.

**Scope:**
- Garmin Health API webhook registration (one-time setup)
- `GarminWebhookController` already exists — complete the activity parsing logic
- Parse Garmin activity summary payload → create `RunningActivity`
- Handle deduplication (activity already synced via manual push)
- Add Garmin signature verification (B-041 bug fix)
- Notify user via WebSocket toast: "New Garmin activity synced: 12km run"

---

## US-G003 — Manual .FIT File Import

**Priority:** 🟡 Medium

**Problem:** Users without webhook access (e.g., older Garmin models) can't sync activities. A .FIT file upload is the universal fallback.

**Scope:**
- File upload endpoint: `POST /api/garmin/fit-import` (multipart)
- Parse .FIT file using `garmin-fit-sdk` or `java-fit` library
- Extract: distance, duration, pace, heart rate, GPS polyline, cadence, elevation
- Create `RunningActivity` from parsed data
- Frontend: "Import .FIT file" button on the Activities page

---

## US-G004 — Push Training Programs to Garmin Calendar

**Priority:** 🟡 Medium

**Problem:** US-006 pushes individual workouts. Training programs (multi-week plans) can't be bulk-pushed to the Garmin calendar.

**Scope:**
- `POST /api/garmin/programs/{programId}/push-calendar` — push all remaining sessions to Garmin
- Each session maps to a scheduled workout on the Garmin Training API
- Show per-session push result (success/skip/error)
- Frontend: "Push to Garmin" button on the training program detail page

---

## US-G005 — Health & Readiness Data Sync

**Priority:** 🟡 Medium

**Problem:** The `HealthMetric` entity exists but is populated manually or via sparse sync. Garmin Health API provides daily `dailies` summaries (sleep, stress, body battery, HRV).

**Scope:**
- Scheduled daily pull: `GET /api/garmin/health/daily-summary` from Garmin Health API
- Populate `health_metrics` table: resting HR, sleep score, VO2Max, HRV status, body battery
- Dashboard widget shows "Yesterday's readiness" based on health data
- Performance chart (CTL/ATL/TSB) uses HRV status for color coding

---

## US-G006 — LiveTrack Mirroring

**Priority:** 🟢 Low

**Problem:** Garmin's LiveTrack shares a URL. RunHub's live tracking exists but is not linked to Garmin's own live stream. They should be unified.

**Scope:**
- When a user starts a Garmin LiveTrack session, Garmin sends a webhook with the LiveTrack URL
- Store `garminLiveTrackUrl` on the active session (already in `LiveSession` model)
- Feed page shows a "🔴 Live on Garmin" badge with a link to the Garmin share URL
- When LiveTrack ends, mark session as complete

---

## US-G007 — Advanced Workout Metrics (Laps & Splits)

**Priority:** 🟢 Low

**Problem:** Activities show total stats only. Serious runners want lap-by-lap splits, heart rate zones per lap, and cadence trends — data Garmin FIT files contain.

**Scope:**
- Parse lap data from FIT webhook payload
- Store laps as JSONB in `running_activities.laps`
- Activity detail page: splits table (lap, distance, pace, HR, cadence)
- Pace trend SVG chart per lap
