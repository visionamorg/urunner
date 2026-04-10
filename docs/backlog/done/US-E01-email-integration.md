# Epic 3 – Story 3: Email Integration (JavaMailSender)

**Status:** Done

## What was built

- Added `spring-boot-starter-mail` dependency to `pom.xml`
- Added SMTP config to `application.yml` using `${MAIL_HOST}`, `${MAIL_PORT}`, `${MAIL_USERNAME}`, `${MAIL_PASSWORD}` env vars (defaults: Gmail/587)
- Added `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`, `MAIL_FROM_NAME` placeholders to `.env`
- Created `EmailService.java` in `notifications/service/` with:
  - `@Async` `sendCommunityInviteEmail(toEmail, invitedName, inviterName, communityName, inviteToken)` method
  - Styled HTML email template (dark theme matching RunHub UI) with CTA button and invite link
  - Graceful error handling — email failures are logged as WARN, never crash the request
- Added `email_invites BOOLEAN NOT NULL DEFAULT TRUE` field to `User` model and `database/schema.sql`
- Created `NotificationPreferenceDto` in `users/dto/`
- Added `getNotificationPreferences` and `updateNotificationPreferences` methods to `UserService`
- Added endpoints to `UserController`:
  - `GET /api/users/me/notification-preferences`
  - `PATCH /api/users/me/notification-preferences`

## DB migration
The `email_invites` column is added automatically by Hibernate (`ddl-auto: update`) on next container start.
