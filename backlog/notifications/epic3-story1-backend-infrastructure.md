# Epic 3: Global Notification System
## Story 1: Core Backend Notification Infrastructure

**As a** System Architect / Backend Developer
**I want** a unified, scalable Notification infrastructure
**So that** any microservice or module (Community, Feed, Users) can easily trigger real-time alerts.

### Description
Before we build the UI, we need a solid foundation. The backend needs a generic `Notification` entity to store alerts in the database, ensuring users see them even if they are offline when the event occurs. We also need a real-time transport layer (Server-Sent Events or WebSockets) to push these alerts instantly if the user is currently online.

### Acceptance Criteria
- [ ] Create a `Notification` entity: `id`, `userId` (recipient), `type` (INVITE, LIKE, MENTION, SYSTEM), `title`, `message`, `actionUrl`, `isRead`, `createdAt`.
- [ ] Implement a `NotificationService` handles creating and persisting these notifications.
- [ ] Implement a real-time delivery mechanism. Configure Spring WebSockets (STOMP) or Server-Sent Events (SSE) at an endpoint like `/ws/notifications`.
- [ ] When a new notification is saved, the service immediately pushes the DTO through the WebSocket to the specific user's private channel (e.g., `/user/queue/notifications`).
- [ ] Provide REST endpoints: `GET /api/notifications` (paginated), `PUT /api/notifications/{id}/read` (mark as read), and `PUT /api/notifications/read-all`.

### Technical Notes for Claude
- Use Spring Data JPA for the repository. Ensure `GET /notifications` is heavily indexed on `userId` and `isRead` since it will be polled frequently.
- For WebSockets, configure `WebSocketMessageBrokerConfigurer`. Ensure authentication intercepts the handshake so only verified users subscribe to their own channels.
