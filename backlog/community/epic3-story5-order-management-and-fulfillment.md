# Epic: Community Store
## Story: Feature - Order Management & Fulfillment

**As a** Community Admin
**I want to** view and manage incoming orders from my store
**So that** I can fulfill physical merchandise shipments or track digital sales.

### Description
Once members purchase products, admins need visibility into order history to know what needs to be shipped, where to ship it, and whether the order is completed.

### Acceptance Criteria
- [ ] A dedicated "Orders" dashboard for community admins showing a list of all purchases made in their store.
- [ ] Each order displays: Order ID, Buyer Info, Total Amount, Date, Status (Pending, Shipped, Delivered, Canceled), and shipping address (if physical).
- [ ] Admins can update the status of an order (e.g., mark as "Shipped" and optionally provide a tracking number).
- [ ] When an item marked as physical is purchased, the buyer is required to input a shipping address during Stripe checkout, which is synced back via the webhook.
- [ ] Order details link back to the specific products sold.
- [ ] (Optional feature) Add a summary widget showing total store revenue for a selected time period.

### Technical Notes for Claude
- Create an `Order` entity and an `OrderItem` entity.
- The `Order` entity should link `communityId`, `buyerId` (if logged in), `stripeSessionId`, `amountTotal`, `shippingAddress`, and `status`.
- Provide endpoints to update the status: `PATCH /api/communities/{id}/orders/{orderId}/status`.
- Ensure buyers and admins get basic email notifications when an order is created (using Postmark/SendGrid integration if available).
