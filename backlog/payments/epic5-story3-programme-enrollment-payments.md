# Epic 5: Payments & Enrollment
## Story 3: Premium Programme Enrollment (Unlock Content)

**As a** Community Admin
**I want to** charge members a fee to access my specialized 12-week marathon training programme
**So that** I can monetize my coaching expertise directly within the community.

### Description
In Epic 2, we built Training Programmes. But currently, anyone can click "Enroll". We need to add a "Paywall" logic. If a Programme has a price attached, the "Enroll" button acts exactly like the Event Ticketing flow, securely withholding the workouts from the user's personal calendar until Stripe clears the payment.

### Acceptance Criteria
- [ ] Add a `price` (BigDecimal) field to the `Programme` model. If `price == 0` or null, it's free.
- [ ] If `price > 0`, the Programme Details page shows "Unlock Programme for $X.XX" instead of "Enroll".
- [ ] Clicking the button routes to Stripe Checkout using the `PROGRAMME` entity type in metadata.
- [ ] The backend must block `GET /api/programmes/{id}/workouts` for users who are not explicitly enrolled.
- [ ] When the Stripe webhook fires and publishes `PaymentSuccessfulEvent(PROGRAMME, id)`, the user's `ProgrammeEnrollment` status flips from `PENDING` to `ACTIVE`.
- [ ] Upon returning to the app, the 12 weeks of workouts instantly populate on their personal calendar.

### Technical Notes for Claude
- This uses the exact same `PaymentService` built in Story 1, simply passing a different metadata `entityType`.
- Ensure strict security annotations/guards on the API so cunning users cannot scrape the hidden workouts unless their `ProgrammeEnrollment` actually exists and is paid.
