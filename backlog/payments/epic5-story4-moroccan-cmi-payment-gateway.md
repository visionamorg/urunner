# Epic 5: Payments & Enrollment
## Story 4: Moroccan Local Payment Gateway Integration (CMI / PayZone)

**As a** Moroccan Runner & Community Member
**I want to** pay for my Event Tickets and Programmes using my local Moroccan bank card (in MAD)
**So that** I don't face international transaction blocks, high currency conversion fees, or the need for an international credit card.

### Description
While Stripe is fantastic for international users, it does not support merchants based in Morocco natively and can reject many local Moroccan bank cards. For a platform operating in or targeting Morocco, we must integrate a local payment gateway—most commonly **CMI (Centre Monétique Interbancaire)** or an aggregator like **PayZone / NAPS / CMI via a wrapper**. 

When a user clicks "Buy Ticket", they should be redirected to the secure CMI payment page where they can enter their local "Carte Bancaire Marocaine" details. Once the payment succeeds, CMI will trigger a Server-to-Server callback (Webhook) to our backend to confirm the transaction.

### Acceptance Criteria
- [ ] Implement a new `MoroccanPaymentService` alongside the existing Stripe service.
- [ ] Add a checkout UI step allowing the user to select their payment method: "International Card (Stripe)" or "Local Moroccan Card (CMI)".
- [ ] Generate the CMI form payload payload including: `clientid`, `amount` (in MAD), `oid` (Order ID - mapped to our `TicketToken` or `EnrollmentId`), `okUrl`, `failUrl`, and the cryptographic `hash` signature requested by CMI.
- [ ] The frontend submits an auto-posting hidden HTML form that redirects the user to the CMI gateway.
- [ ] Implement the CMI Server-to-Server Callback Endpoint (`POST /api/webhooks/cmi`). This endpoint MUST verify the hash signature from CMI before trusting the payload.
- [ ] If the CMI callback indicates `Response=Approved`, the backend upgrades the RSVP to `CONFIRMED` or the Programme to `ACTIVE` and publishes the `PaymentSuccessfulEvent`.

### Technical Notes for Claude
- **Security Check:** The CMI hash is typically generated using SHA-256 / HMAC or MD5 depending on their latest spec. You will need to implement a dedicated `CmiEncryptionUtil.java` class using the StoreKey provided by the CMI dashboard.
- Update the `transactions` database table to track `gatewayName` (e.g., explicitly labeling transactions as `STRIPE` or `CMI`).
- Ensure the amount is strictly formatted for CMI requirements (some local gateways require amounts to be passed without decimals or in a specific string format).
- The `okUrl` and `failUrl` should point the user's browser back to the Angular frontend (e.g., `/payment/success`), while the actual confirmation happens invisibly via the callback URL.
