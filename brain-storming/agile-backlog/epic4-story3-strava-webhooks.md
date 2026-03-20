# Epic: Infrastructure & Developer Experience (DX)
## Story: Strava Webhook Integration

**As a** user who tracks runs on Strava,
**I want my** runs to appear on RunHub within seconds of me saving them on Strava,
**So that** I don't have to manually click a "Sync Now" button.

### Acceptance Criteria:
- *Given* a user connects Strava, *when* they finish a run, *then* Strava fires a webhook to the Spring Boot backend, and the run is instantly processed and inserted via an asynchronous message queue (e.g., RabbitMQ).
