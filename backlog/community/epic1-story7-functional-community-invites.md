# Epic: Community MVP & Enhancements
## Story: Feature - Functional Community Invites (Notifications & Emails)

**As a** Community Admin
**I want** invited users to receive an email or in-app notification when I invite them
**So that** they actually know they've been invited and can promptly join my community.

### Description
Currently, the admin can input a username to generate a `CommunityInvite` in the database, but the invited user has no idea this happened unless they manually stumble upon an invites page. This creates a terrible user experience.
We need to close this loop by broadcasting the invite to the user via two methods:
1. **Email Notification:** Send an attractive HTML email saying "You've been invited to join [Community Name]" with a magic link to Accept/Decline.
2. **In-App Notification:** If the user is active in the app, display a red notification badge on their "Communities" tab or "Notifications" bell.

### Acceptance Criteria
- [ ] When `CommunityService.inviteUser(...)` successfully creates a `PENDING` invite, it should publish an `InviteCreatedEvent` (or call a Notification/Email service directly).
- [ ] An email is dispatched to the invited user's registered email address containing the community name, the inviter's name, and an embedded link (e.g., `https://urunner.com/invites?token=XYZ`).
- [ ] An in-app push notification or database notification record is created so the user sees a "🔔 1" badge next time they open the app.
- [ ] Clicking the notification or email link takes the user to an "Accept / Decline Invite" confirmation screen.
- [ ] The admin's "Invites" tab automatically updates the invite status from "PENDING" to "ACCEPTED" / "DECLINED" once the user responds.

### Technical Notes for Claude
- Look at `CommunityService.java` at the `inviteUser` method. Currently, it just saves the entity and returns an `InviteDto`.
- Hook up your existing `EmailService` (e.g., JavaMailSender or SendGrid) to send an HTML template. You may want to use Spring `ApplicationEventPublisher` to make this asynchronous.
- Review `InviteDto` and the `respondToInvite(token, accept, user)` method to build the frontend accept/decline landing page.
