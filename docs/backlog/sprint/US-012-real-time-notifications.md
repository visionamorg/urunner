# US-012 — Real-Time Notifications (WebSocket)

**Status:** [ ] Pending
**Priority:** 🟡 Medium

---

## Problem

Notifications are currently fetched on page load only. Users get no live alerts when someone likes their post, comments, sends an invite, or when a community event is posted. The notification bell on the sidebar has a static badge count that doesn't update.

---

## Story

As a **runner**, I want to receive real-time notifications when things happen (likes, comments, invites, event reminders), so I don't have to refresh the page to stay informed.

---

## Acceptance Criteria

### Notification Bell (Sidebar)
- [ ] Unread notification count shown as a badge on the bell icon in the sidebar
- [ ] Badge count updates in real time without page refresh
- [ ] Clicking the bell navigates to `/notifications`
- [ ] Marking all as read clears the badge

### Notification Types
| Event | Notification |
|-------|-------------|
| Someone likes your post | "alice_runner liked your post" |
| Someone comments on your post | "bob_trails commented: Great run!" |
| Community invite received | "You've been invited to City Marathon Club" |
| Community join request approved | "Your request to join Trail Blazers was approved" |
| New event in a community you're in | "City Marathon Club posted a new event: Corniche 10K" |
| Upcoming event reminder (24h) | "Reminder: Corniche 10K is tomorrow at 7am" |
| Garmin push succeeded | "Workout pushed to your Garmin successfully" |

### Real-Time Delivery
- [ ] Backend sends notifications via WebSocket (Spring WebSocket + STOMP)
- [ ] Frontend subscribes to `/user/queue/notifications` on login
- [ ] Notification appears as a toast (top-right, 4s auto-dismiss) AND increments badge count
- [ ] Toast shows icon + message + optional action button ("View post", "Accept")

### Notifications Page
- [ ] Unread notifications shown at top, highlighted
- [ ] "Mark all as read" button
- [ ] Relative timestamps ("2 minutes ago", "Yesterday")
- [ ] Each notification is clickable and navigates to the relevant resource
- [ ] Infinite scroll or "Load more" for older notifications

---

## Technical Notes

### Backend
- Add `spring-boot-starter-websocket` dependency
- `WebSocketConfig`: enable STOMP broker, `/ws` endpoint, `/user` destination prefix
- `NotificationService.push(userId, notification)`: save to DB + send via `SimpMessagingTemplate`
- Call `NotificationService.push()` from:
  - `FeedService` when a like/comment is added
  - `CommunityService` when an invite is sent or join request approved
  - `EventService` when a new event is created in a community
  - Scheduled job for 24h event reminders
- `Notification` entity: already exists — add `read BOOLEAN DEFAULT FALSE`, `targetUrl VARCHAR`

### Frontend
- `WebSocketService` in `core/services/websocket.service.ts`:
  - Connects on login, disconnects on logout
  - Uses `@stomp/stompjs` (install: `npm install @stomp/stompjs sockjs-client`)
- `NotificationService`: subscribe to WS messages, maintain `unreadCount$` observable
- `LayoutComponent`: subscribe to `unreadCount$` for badge
- `ToastService`: already exists — integrate with WS notification stream

---

## Database Migration

```sql
ALTER TABLE notifications ADD COLUMN read BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE notifications ADD COLUMN target_url VARCHAR(255);
```
