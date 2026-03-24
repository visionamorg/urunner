# Epic: Community Management & Growth
## Story: Feature - Community Sponsorships & Monetization (Premium Tiers)

**As a** Community Creator/Admin
**I want to** feature sponsors and offer premium subscription tiers to my community
**So that** I can monetize my group's engagement, reward partners, and fund community events.

### Description
Many large running communities have brand partners (e.g., local shoe stores, nutrition brands) or want to charge a small monthly fee for accessing "VIP" training plans and private chat rooms. Admins should be able to upload a "Sponsored By" logo to be featured at the top of the community page.
Additionally, admins can toggle a "Premium Membership Model", generating a Stripe Checkout Link that restricts non-paying members from joining specific segments of the community.

### Acceptance Criteria
- [ ] App settings allow the admin to upload 1-3 "Sponsor Logos" with clickable hyperlinks.
- [ ] Sponsor logos appear elegantly at the top of the Community Dashboard (e.g., a "Supported By" horizontal scroll row).
- [ ] Admins can configure the community as "Premium" by pasting a Stripe Product URL.
- [ ] "Premium" communities show a locked screen to non-members with a "Subscribe via Stripe" button.
- [ ] Implementing a basic webhook listener: when a Stripe successful payment event is fired with a user's email, they are auto-invited/approved into the community.

### Technical Notes for Claude
- For Sponsor Logos, add `List<Sponsor>` to the `Community` entity. A sponsor requires an image URL and a target web link.
- For Monetization, add a `stripePaymentUrl` and `isPremium` boolean to the `Community` model.
- Add a new `stripe-webhook` controller endpoint. The payload will require parsing the customer email, then invoking `communityService.joinCommunity()` automatically.
- Ensure UI accurately blocks members and routes them to Stripe for premium groups.
