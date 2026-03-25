# User Story: US-G001 - Garmin OAuth 1.0a Stabilization

**As a** runner,
**I want** to securely and reliably connect my Garmin account to Runhub,
**So that** I don't experience synchronization interruptions or login failures.

## Acceptance Criteria
- [ ] Implement robust token refresh logic (if applicable for long-lived tokens).
- [ ] Add graceful error handling for 401/403 responses from Garmin APIs with proactive user notification.
- [ ] Securely store OAuth secrets in the database using encryption at rest.
- [ ] Implement a "Disconnect & Reconnect" flow in the user profile.
- [ ] Log OAuth failures in a centralized monitoring system without leaking sensitive tokens.

## Technical Considerations
- **Backend**: Update `GarminOAuthService.java` to handle `ScribeJava` exceptions more specifically.
- **Security**: Ensure `providerTokenSecret` is encrypted using a vault or AES-256 before storage in PostgreSQL.
- **UX**: Frontend should provide a clear "Status" indicator for the Garmin connection.
