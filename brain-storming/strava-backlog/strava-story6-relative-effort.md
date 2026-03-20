# Epic: Strava Baseline Features
## Story: Relative Effort (Suffer Score)

**As an** athlete training across different sports and intensities,
**I want a** unified "Relative Effort" score derived from my Heart Rate data,
**So that** I can objectively compare a 30-minute grueling track session to a 2-hour easy bike ride.

### Acceptance Criteria:
- *Given* an activity contains HR data, *when* calculating metrics, *then* an algorithm assesses time spent in HR zones relative to my max HR, yielding a single "Relative Effort" integer score (e.g., 85).
