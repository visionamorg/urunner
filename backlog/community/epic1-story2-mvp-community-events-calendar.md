# Epic: Community MVP & Enhancements
## Story: MVP2 - Make Community Events & Calendar Functional

**As a** Community Member or Admin
**I want** the Events and Calendar tabs to be fully functional
**So that** I can see upcoming community events and admins can manage them correctly.

### Description
According to the MVP checklist in `community-features.md`, the Events and Calendar must work before launch.
1. **Events Tab**: Lists community events. Members can view details, admins can create/edit/cancel events.
2. **Calendar Tab**: Displays a monthly grid. Admins can click a day to directly create an event.

### Acceptance Criteria
- [ ] Events tab shows active and cancelled events with correct badges.
- [ ] Admins can create new events using the UI form within the community.
- [ ] Canceling an event soft-deletes it (`isCancelled` flag) and updates the UI in real-time.
- [ ] Calendar renders a 6x7 grid, colored dots show events.
- [ ] Clicking a calendar day shows the event details below the grid.
- [ ] Admins clicking a calendar day get an "Add Event" option scoped to that date.

### Technical Notes for Claude
- Verify components in `community-calendar/` and `core/models/event.model.ts`.
- Ensure `EventService` and API calls (`POST`, `PUT`, `DELETE /api/communities/{id}/events`) are connected.
