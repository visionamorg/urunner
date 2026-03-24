# Epic 3: Global Notification System
## Story 3: Third-Party Email & Mobile Push Integration

**As a** Community Admin / User Engagement Manager
**I want** critical notifications to route to users via Email or Push Notifications (iOS/Android)
**So that** users who are currently offline or haven't opened the app in days still receive important alerts (like Community Invites).

### Description
In-app notifications are great while you are browsing, but dormant users need emails and push notifications. We need the backend `NotificationService` to optionally delegate critical high-priority alerts to external SaaS providers.

### Acceptance Criteria
- [ ] Integrate an `EmailService` (e.g., using SendGrid API or AWS SES) to fire out templated HTML emails when `type = INVITE` or `type = URGENT`.
- [ ] Integrate Firebase Cloud Messaging (FCM) Admin SDK into the Spring Boot backend.
- [ ] Generate mobile push tokens on the frontend (if wrapped in Capacitor/Cordova or as a PWA) and store them in the `users` table (`fcm_token`).
- [ ] When sending a notification, check if the recipient is offline. If offline and they have an FCM token, dispatch a push alert.
- [ ] Provide users a "Global Notification Settings" page where they can toggle Preferences: "Receive Emails for Invites" (Yes/No), "Push for Likes" (Yes/No).

### Technical Notes for Claude
- Use `spring-boot-starter-mail` and configure SMTP credentials in `.env`.
- Use the official `firebase-admin` Java SDK to authenticate via a service account `JSON`.
- When constructing the FCM payload, ensure `data` fields container the `actionUrl` for deep-linking into the app.
