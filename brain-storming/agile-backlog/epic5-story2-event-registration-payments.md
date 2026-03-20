# Epic: E-Commerce & Monetization
## Story: Event Registration Payments

**As an** event organizer,
**I want to** charge a registration fee for my community races,
**So that** I can fund medals, timing chips, and logistics directly through the platform.

### Acceptance Criteria:
- *Given* I am creating an event, *when* I set the `price` > $0.00, *then* a Stripe onboarding flow is triggered if I haven't connected a payout bank account.
- *Given* a user tries to join a paid event, *then* they are redirected to a Stripe checkout session before their `event_registrations.status` is marked as `REGISTERED`.
