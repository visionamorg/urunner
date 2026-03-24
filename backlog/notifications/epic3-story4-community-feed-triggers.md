# Epic 3: Global Notification System
## Story 4: Hooking Up Application Triggers (Social & Community)

**As a** System Architect
**I want** all major user interactions in Community and Feed to trigger the Notification Service
**So that** the entire notification system is actively populated with real data.

### Description
With the core system (Backend, UI, Emails, Push) ready, it's time to "wire it up" to the actual business logic. Whenever someone likes a post, mentions someone in chat, invites someone to a community, or AI tags someone from a Google Drive photo, the `NotificationService` must be invoked.

### Acceptance Criteria
- [ ] **Community Invites**: Refactor `CommunityService.inviteUser` to trigger `notificationService.create(targetUserId, "INVITE", "You've been invited!", "/communities/" + id)`.
- [ ] **Feed Likes/Comments**: Refactor `FeedService` so that when a user likes or comments on another user's post, a `LIKE`/`COMMENT` notification is fired. (Skip if the user likes their own post).
- [ ] **Smart Event Galleries (OCR)**: Refactor the `syncDrivePhotos`/Bib Recognition logic so when a runner is identified in a photo, they receive a `TAGGED` notification: "You were spotted in 3 new photos!".
- [ ] **Chat Mentions (@Username)**: When someone uses `@username` inside a Community Chat Room, regex parse the message and fire a `MENTION` notification to that specific user.

### Technical Notes for Claude
- Instead of tightly coupling everything, heavily consider using Spring's `@EventListener` or `@TransactionalEventListener`. E.g., `applicationEventPublisher.publishEvent(new PostLikedEvent(post, liker))`.
- The `NotificationService` then just listens for these events asynchronously and handles routing them to DB, WebSockets, and Email. This keeps `CommunityService` and `FeedService` very clean.
