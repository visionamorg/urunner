# Payments — Stripe Infrastructure (MVP3)

All payment features depend on this foundation being built first.

---

## US-P01 — Core Stripe Checkout Infrastructure

**Priority:** 🔴 High (prerequisite for all other payment stories)
**From:** `backlog/payments/epic5-story1-stripe-checkout-infrastructure.md`

**Scope:**
- Add `stripe-java` dependency to Spring Boot backend
- `PaymentService.createCheckoutSession(user, itemType, itemId)` → returns `stripeCheckoutUrl`
  - `itemType`: `EVENT_TICKET` | `PROGRAMME` | `COMMUNITY_SUBSCRIPTION`
- Webhook controller `POST /api/webhooks/stripe` (publicly accessible, validate `Stripe-Signature` header)
- On `checkout.session.completed`: read `metadata`, publish `PaymentSuccessfulEvent`
- `transactions` table for audit trail

```sql
CREATE TABLE transactions (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id),
    amount           DECIMAL(10,2) NOT NULL,
    currency         VARCHAR(3) NOT NULL DEFAULT 'MAD',
    status           VARCHAR(20) NOT NULL,
    stripe_session_id VARCHAR(255),
    item_type        VARCHAR(30),
    item_id          BIGINT,
    created_at       TIMESTAMP NOT NULL DEFAULT now()
);
```

---

## US-P02 — Paid Event Ticketing

**Priority:** 🟡 Medium
**From:** `backlog/payments/epic5-story2-event-ticketing-flow.md`

**Scope:**
- Events can have `ticketPrice` (nullable — null = free)
- "Register" button on paid events → `PaymentService.createCheckoutSession()` → redirect to Stripe
- On payment success webhook: mark `EventRegistration.status = CONFIRMED`
- Ticket confirmation email sent via `EmailService`
- Event detail shows "Paid ticket" badge for confirmed registrations
- Admin: export attendee list as CSV with payment status

---

## US-P03 — Moroccan CMI Payment Gateway

**Priority:** 🟢 Low
**From:** `backlog/payments/epic5-story4-moroccan-cmi-payment-gateway.md`

**Problem:** Most Casablanca runners use Moroccan bank cards which don't work on Stripe. The Centre Monétique Interbancaire (CMI) is the local payment gateway used by Moroccan e-commerce.

**Scope:**
- Integrate CMI payment gateway alongside Stripe
- User can choose at checkout: "International card (Stripe)" or "Moroccan card (CMI)"
- CMI uses a redirect-based flow similar to PayPal — redirect to CMI hosted page
- CMI webhook / return URL handling
- Note: CMI requires a business account in Morocco — document the onboarding requirements
