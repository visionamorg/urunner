# Epic 5: Payments & Enrollment
## Story 2: Event Ticketing Pipeline (Buying a Ticket)

**As a** Runner
**I want to** easily purchase a ticket for a community run
**So that** I can guarantee my spot and receive an official entry pass (QR code).

### Description
With the backend ready, we need the frontend User Experience to be seamless. When a user looks at an Event inside a Community, they should see "Enroll - $25.00". Clicking this routes them to Stripe. Upon returning, they should see their official ticket and a QR Code they can show on race day.

### Acceptance Criteria
- [ ] The "Event Details" page in Angular displays the price prominently. If `price > 0`, the RSVP button says "Buy Ticket".
- [ ] Clicking "Buy Ticket" calls the backend `PaymentService` to generate a Stripe Checkout URL, and instantly redirects the user's browser.
- [ ] Upon successful payment (redirected back to a `/payment/success` route), display a celebratory animation and standard receipt.
- [ ] Generate a unique `ticket_token` (UUID) in the `event_rsvps` table when the payment webhook confirms the order.
- [ ] Add a new "My Tickets" view in the user's profile displaying a QR Code (encoding the `ticket_token`) for each paid event.
- [ ] Admins get a basic generic scanner view (or simply a button next to the runner's name in the "Registrants Roster" to mark them as "Checked-In" on race morning).

### Technical Notes for Claude
- Use `ngx-qrcode2` or an equivalent Angular library to render the QR codes securely on the frontend. 
- Ensure the RSVP button enters a "Processing..." loading state so users don't double-click.
- Rely strictly on the Stripe Webhook (not the success URL redirect) to upgrade the RSVP from `PENDING_PAYMENT` to `CONFIRMED`.
