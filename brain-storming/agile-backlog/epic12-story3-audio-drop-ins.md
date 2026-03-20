# Epic: Advanced Social Dynamics
## Story: Live Audio Drop-Ins

**As a** close friend of someone running a difficult uphill segment,
**I want to** tap into a live audio channel,
**So that** I can verbally coach and cheer them on directly through their headphones in real-time.

### Acceptance Criteria:
- *Given* a user allows "Audio Drops" in their privacy settings during a live run, *when* a friend clicks a microphone icon on the spectator map, *then* WebRTC establishes a temporary VoIP connection.
- *Given* the connection is live, *then* the runner can hear the friend's voice over their music (which is temporarily ducked in volume) for a maximum of 30 seconds before auto-disconnecting.
