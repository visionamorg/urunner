# Epic 3: Global Notification System
## Story 2: In-App Notification Center (Frontend UI)

**As a** User of the RunHub platform
**I want to** see a "Bell" icon with an unread badge that opens a dropdown of my latest alerts
**So that** I know exactly when I've been tagged, invited, or replied to without checking every page.

### Description
Now that the backend stores notifications, the Angular frontend needs to consume them. The navbar needs a universal Bell icon. This icon should connect via WebSocket/SSE to receive real-time updates while the user is browsing, showing a pop-up toast and incrementing the unread counter. Clicking the bell shows a list of recent items, and clicking an item routes them to the relevant page (e.g., the Community Event).

### Acceptance Criteria
- [ ] Navbar includes a Bell icon displaying a red badge with the number of `isRead = false` notifications.
- [ ] Clicking the Bell opens a dropdown or side-panel listing the most recent 20 notifications.
- [ ] Implementing a `NotificationService` in Angular that connects to the Spring backend via WebSocket (using SockJS + STOMP) or SSE on application load.
- [ ] Receiving a live notification displays a dismissable global Toast message in the bottom-right corner.
- [ ] Muted styling is applied to items marked `isRead = true`.
- [ ] Clicking a notification triggers `PUT /api/notifications/{id}/read` and navigates the `Router` to the notification's `actionUrl`.

### Technical Notes for Claude
- Use `@stomp/ng2-stompjs` or basic WebSocket API for real-time pushing.
- The UI should incorporate RunHub's sleek gradient and dark-mode styling (e.g., an unread notification has a subtle orange left-border).
- Build a generic `NotificationItemComponent` to standardize how alerts look.
