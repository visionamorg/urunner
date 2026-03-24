# Epic 5: Payments & Enrollment
## Story 1: Core Stripe Checkout & Webhook Infrastructure

**As a** System Architect
**I want to** build the core payment processing infrastructure using Stripe
**So that** the entire RunHub platform can confidently process payments for Events, Programmes, and Community Subscriptions.

### Description
Before we can build UI buttons that say "Buy Ticket", we need a rock-solid, secure backend to process credit cards. We will integrate Stripe's API. Instead of manual custom elements, we will use Stripe Checkout Sessions for maximum security and conversion. The backend must listen to Stripe Webhooks asynchronously to fulfill orders only when the bank confirms the charge.

### Acceptance Criteria
- [ ] Add `stripe-java` dependency to the Spring Boot backend.
- [ ] Create a `PaymentService` that accepts a user, an item type (`EVENT_TICKET`, `PROGRAMME`, `SUBSCRIPTION`), and an `itemId`, and returns a `stripeCheckoutUrl`.
- [ ] Create a dedicated Webhook controller endpoint (e.g., `POST /api/webhooks/stripe`) that verifies Stripe signatures.
- [ ] When the webhook receives `checkout.session.completed`, extract the custom metadata (e.g., `eventId=10, userId=5`) and publish an internal Spring Event (e.g., `PaymentSuccessfulEvent`).
- [ ] Store every payment transaction in a new `transactions` table (id, userId, amount, currency, status, stripeSessionId) for audit purposes.

### Technical Notes for Claude
- Protect Webhook endpoints from CSRF and ensure they are publicly accessible (`.permitAll()`), but validate the `Stripe-Signature` header using the webhook secret from `.env`.
- Use Stripe's `metadata` field on the Checkout Session to pass our internal IDs (userId, entityId, entityType). When the webhook fires, read the `metadata` to know exactly what the user just bought.
