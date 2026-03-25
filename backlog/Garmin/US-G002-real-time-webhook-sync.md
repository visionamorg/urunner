# User Story: US-G002 - Real-time Webhook Activity Sync

**As a** runner,
**I want** my Garmin runs to appear on Runhub immediately after I save them on my watch,
**So that** I can see my stats and share my achievements without manual refreshing.

## Acceptance Criteria
- [ ] Create an endpoint `/api/webhooks/garmin` visible to the public internet (for Garmin Connect API callbacks).
- [ ] Implement signature verification to validate that incoming webhooks are legitimately from Garmin.
- [ ] Handle "Activity Summary" notifications by triggering an asynchronous background job to fetch the full activity data.
- [ ] Support "De-authorization" and "Consent" webhooks to keep local connection status in sync with Garmin settings.
- [ ] Implement an idempotency check to prevent duplicate activity creation if multiple webhooks are sent for the same run.

## Technical Considerations
- **Backend**: Create `GarminWebhookController`. Use `@Async` or a task queue (Spring TaskExecutor) to process the payload to keep webhook response times under 2 seconds.
- **Security**: Garmin webhooks use a shared key for signature validation.
