# User Story: US-G006 - Sync Core Health & Readiness Metrics

**As a** serious runner,
**I want** Runhub to track my daily recovery and health stats (HRV, Sleep, VO2 Max),
**So that** I can receive better AI coaching advice based on my physical readiness.

## Acceptance Criteria
- [ ] Integrate with the Garmin Health API (Vitals, Sleep, Daily Summary).
- [ ] Extract "VO2 Max" and "Fitness Age" updates whenever they change.
- [ ] Fetch daily "Resting Heart Rate" and "Sleep Score".
- [ ] Store these metrics in a new `HealthMetrics` table related to the user.
- [ ] Visualize health trends in the user dashboard.

## Technical Considerations
- **Backend**: `GarminHealthSyncService`.
- **Database**: New table for `health_metrics` with daily granularity.
