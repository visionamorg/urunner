# Epic 4: Advanced Event Management
## Story 2: Feature - Paid Ticketing, Waitlists & Volunteer Management

**As an** Event Organizer
**I want to** manage paid registrations, waitlists, and volunteer spots
**So that** I can handle the logistics of large-scale community events from start to finish directly inside RunHub.

### Description
Most running events require strict capacity management and often involve a ticket price (for bibs/t-shirts) or rely heavily on volunteers. We need to upgrade the basic RSVP system. If an event is at maximum capacity, users join a "Waitlist". If a user drops out, the system automatically pulls someone from the waitlist. 
Additionally, members who don't want to run should be able to RSVP as "Volunteers".

### Acceptance Criteria
- [ ] Integrate Stripe Checkout for events where `price > 0`. RSVP state remains "PENDING_PAYMENT" until Stripe fulfills the webhook.
- [ ] Implement a **Waitlist System**: If `currentParticipants == maxParticipants`, new RSVPs get a `WAITLISTED` status.
- [ ] Implement an automated cron job or trigger: If a confirmed runner cancels, automatically upgrade the first waitlisted user to `CONFIRMED` and notify them.
- [ ] Add a secondary RSVP button for "Sign up to Volunteer". Add `volunteersCount` and `maxVolunteers` to the Event entity.
- [ ] The organizer dashboard features a "Registrants Roster" table with tabs for Runners, Waitlisted, and Volunteers to easily export to CSV.

### Technical Notes for Claude
- Backend: Create a new `EventRsvp` entity if one doesn't exist, managing `userId`, `eventId`, `status` (CONFIRMED, WAITLISTED, CANCELLED), and `role` (RUNNER, VOLUNTEER).
- Ensure strict transactional locks are used when updating participant counts to avoid overbooking.
- Connect this to the new Notification Service (Epic 3) to email users who get bumped off the waitlist!
