# Epic: Event & Race Management
## Story: QR Code Race Check-In System

**As a** Race Director,
**I want to** scan participants' dynamic QR codes at the starting line using the organizer app,
**So that** I can rapidly check in hundreds of runners without paper lists or long queues.

### Acceptance Criteria:
- *Given* a user successfully registers and pays for an event, *then* a unique QR code is generated and saved to their `event_registrations` profile.
- *When* an organizer scans the QR code using their mobile camera in the app, *then* the user's `status` immediately changes from `REGISTERED` to `CHECKED_IN` in the backend.
