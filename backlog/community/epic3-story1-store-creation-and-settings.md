# Epic: Community Store
## Story: Feature - Store Creation & Stripe Connect Onboarding

**As a** Community Admin
**I want to** activate a store for my community and link a payout account
**So that** I can legally and securely accept payments for community merchandise and digital products.

### Description
Before a community can start selling items, they must opt-in to the Store feature and set up a connected Stripe account (Stripe Connect). This ensures funds are routed directly to the community's bank account, while the platform can optionally take an application fee.

### Acceptance Criteria
- [ ] In the Community Settings, there is a new "Store" tab containing a "Set up Store" button.
- [ ] Clicking "Set up Store" creates a new Stripe Connect Custom/Express account via API and redirects the admin to the Stripe hosted onboarding flow.
- [ ] The app handles the Stripe return URL (return_url/refresh_url) and updates the community's `stripeAccountId` and `storeOnboardingComplete` status.
- [ ] Once onboarded, the admin sees their Store Dashboard with a "Store is Live/Offline" toggle switch.

### Technical Notes for Claude
- Add `stripeAccountId`, `storeOnboardingComplete` (boolean), and `isStoreActive` (boolean) to the `Community` entity.
- Create a `StripeConnectService` to handle `accounts.create` and `accountLinks.create`.
- Provide a webhook or return endpoint to verify that 'details_submitted' is true on the connected Stripe account before setting `storeOnboardingComplete = true`.
