# Epic: Platform Internationalization (i18n)
## Story: Feature - Backend Localization & Emails

**As a** Global User
**I want to** receive application emails, notifications, and API error messages in my preferred language
**So that** I can understand system communications outside of the browser UI.

### Description
The frontend is translated, but the backend still generates generic error messages (like validation failures) and sends automated emails (like welcome emails, password resets, or order confirmations) in English. The backend needs to become language-aware based on the user's profile and incoming request headers.

### Acceptance Criteria
- [ ] The backend API parses the `Accept-Language` HTTP header to determine the client's language for unauthenticated requests.
- [ ] Validation framework errors (e.g., Spring Boot Hibernate Validator) return localized error messages based on the determined locale.
- [ ] Outgoing emails (e.g., Welcome Email, Password Reset, Community Invites) are sent in the user's `preferredLanguage` retrieved from the database.
- [ ] Push Notifications and In-App Notifications are generated and stored in the localized language of the recipient user.
- [ ] For fallback, if the user's language is missing or unsupported, it defaults to English.

### Technical Notes for Claude
- Configure an `AcceptHeaderLocaleResolver` in Spring Boot (if using Java) or the equivalent middleware in NodeJS to parse the `Accept-Language` header automatically.
- Store message bundles on the backend (`messages.properties`, `messages_fr.properties`).
- When triggering email notifications, lookup the target `User`'s `preferredLanguage`. Pass this locale to the template engine (like Thymeleaf, Handlebars, or to the third-party email service like Postmark/SendGrid via template aliases).
- For an Angular frontend, ensure the HTTP Interceptor appends the `Accept-Language` header to all outgoing API requests dynamically based on the current translation language.
