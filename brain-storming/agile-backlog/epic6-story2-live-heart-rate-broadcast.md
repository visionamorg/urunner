# Epic: Wearable Device Integrations
## Story: Live Heart Rate Broadcast to Followers

**As a** competitive runner during a live virtual race,
**I want to** broadcast my live heart rate from my wearable to spectators,
**So that** my coaches and friends can see my actual physical effort in real-time on the event leaderboard.

### Acceptance Criteria:
- *Given* I am broadcasting my run, *when* my Garmin or Apple Watch records a new HR value, *then* it is sent via WebSocket to the live Map component.
- *When* my HR enters Zone 5 (Max effort), *then* a visual flame icon appears next to my name on the spectator dashboard.
