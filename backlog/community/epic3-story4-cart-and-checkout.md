# Epic: Community Store
## Story: Feature - Shopping Cart & Checkout

**As a** Community Member
**I want to** add products to a cart and check out securely
**So that** I can purchase merchandise or digital goods from my community.

### Description
Members browsing the community store can add items to their shopping cart and proceed to checkout. The checkout process is handled via Stripe Checkout Sessions to ensure PCI compliance and support multiple payment methods (cards, Apple Pay, Google Pay).

### Acceptance Criteria
- [ ] Users can add multiple products (and variants) to a temporary cart stored in their session/local state.
- [ ] A cart icon or drawer shows the summary of items, subtotal, and any applicable shipping/taxes.
- [ ] Clicking "Checkout" creates a Stripe Checkout Session via the backend and redirects the user to the Stripe hosted payment page.
- [ ] The checkout session must specify the `stripeAccountId` of the community to route the funds to them securely.
- [ ] Successful payments redirect the user back to an "Order Success" page with their receipt.
- [ ] Webhooks listen for `checkout.session.completed` to fulfill the order in the database and adjust inventory.

### Technical Notes for Claude
- Cart state can be client-side only (Redux/Signals) until checkout initiation.
- Endpoint: `POST /api/store/checkout` requiring an array of `{ productId, variantId, quantity }` and `communityId`.
- The backend creates the checkout session on behalf of the connected account. Ensure `payment_intent_data.application_fee_amount` is calculated if the platform takes a cut, otherwise set `transfer_data.destination = community.stripeAccountId`.
- Handle out-of-stock validation *before* creating the checkout session.
