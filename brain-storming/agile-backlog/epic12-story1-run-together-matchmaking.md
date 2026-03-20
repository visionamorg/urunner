# Epic: Advanced Social Dynamics
## Story: "Run Together" Pace Matchmaking

**As a** runner moving to a new city,
**I want to** find nearby runners who share my average 5k pace and schedule,
**So that** I can organically match with new running buddies.

### Acceptance Criteria:
- *Given* I open the "Find Runners" tab, *then* the app queries the database for users within a 5km radius whose average `pace_min_per_km` is within ±15 seconds of mine.
- *When* I swipe right to match, and they do the same, *then* a DM channel is automatically created between us to coordinate a run.
