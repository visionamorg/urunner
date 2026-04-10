# Epic 1 – Story 7: Community Invite Email + In-App Notifications

**Status:** Done

## What was built

Updated `CommunityService.inviteUser(...)` so that when a `PENDING` invite is created:

1. **In-app notification** — `NotificationService.create(...)` is called with:
   - Type: `INVITE`
   - Title: `Community Invite`
   - Message: `"<inviter> invited you to join <community>"`
   - Link: `/invites?token=<UUID>` (was previously `/notifications`, now points directly to the invite accept/decline page)

2. **HTML email** — `EmailService.sendCommunityInviteEmail(...)` is called if the invited user has `email_invites = true`:
   - To: invited user's email
   - Subject: `"<inviter> invited you to join <community> on RunHub"`
   - Body: styled HTML with community name, inviter's username, and a "View Invite →" button linking to `<FRONTEND_URL>/invites?token=<UUID>`
   - Sent asynchronously — never blocks the HTTP response

## Files changed
- `communities/service/CommunityService.java` — injected `EmailService`, updated `inviteUser`
- `notifications/service/EmailService.java` — new file
- `users/model/User.java` — added `emailInvites` field
