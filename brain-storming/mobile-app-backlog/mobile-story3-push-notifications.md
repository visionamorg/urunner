# Epic: Mobile Application (iOS & Android)
## Story: Native Push Notifications

**As a** community member,
**I want to** receive native push notifications on my phone's lock screen,
**So that** I know instantly when a friend likes my run, sends me a chat message, or drops a live cheer during an event.

### Acceptance Criteria:
- *Given* the Spring Boot backend triggers an event, *when* it fires a request via Firebase Cloud Messaging (FCM) or Apple Push Notification service (APNs), *then* a banner notification appears on my device.
- *When* I tap the notification, *then* Deep Linking routes me directly to the relevant screen (e.g., the specific Chat Room or Activity Detail page).
