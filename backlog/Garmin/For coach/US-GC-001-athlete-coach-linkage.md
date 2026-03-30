# User Story: US-GC-001 - Athlete-Coach Linkage & Permission Hub

**As a** coach,
**I want** to invite my athletes to share their Garmin data with me,
**So that** I can see their activities, heart rate, and health metrics directly in my Runhub dashboard.

## Acceptance Criteria
- [ ] Implement an "Invite Athlete" flow via email or QR code.
- [ ] Create a specific "Garmin Permissions" opt-in for the athlete (Activity only, vs. Full Health/HRV).
- [ ] Implement a "Coached By" section in the user profile to manage active coaching relationships.
- [ ] Add a visual indicator in the coach dashboard for "Waiting for Approval" on Garmin data.
- [ ] Ensure that disconnecting the coach instantly revokes their access to private health metrics.

## Technical Considerations
- **Backend**: New table `coaching_connections` with `athlete_id`, `coach_id`, and `garmin_access_level` (enum: BASIC, FULL).
- **Security**: Coaches should only see athlete data if a signed record in `coaching_connections` exists with `status='ACTIVE'`.
- **API**: Check Garmin's developer terms for sharing user data with a third party (the coach) and ensure it's compliant.
