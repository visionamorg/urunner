# User Story: US-G007 - LiveTrack Integration & Mirroring

### Status: DONE

**As a** runner training in remote areas,
**I want** my friends on Runhub to see my live location via my Garmin LiveTrack,
**So that** they can cheer for me and ensure I am safe.

## Acceptance Criteria
- [ ] Capture the "LiveTrack" URL from a running activity webhook or metadata.
- [ ] Embed the LiveTrack view or a custom map view in the community activity feed for active runs.
- [ ] Automatically mark an activity as "Live" in the UI when a session is active.

## Technical Considerations
- **Backend**: Parse the `liveTrackingUrl` if provided in the Garmin Wellness API metadata.
- **Frontend**: Implement a "Live Now" banner for active community members.
