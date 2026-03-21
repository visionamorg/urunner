# Epic: Personal Branding & Growth Features
## Story: In-App Creator Referral Dashboard

**As a** creator pushing the app to my 2,300 Instagram followers,
**I want a** hidden dashboard page in the app,
**So that** I can see exactly how many accounts were created using my specific tracking URL (e.g. `runhub.io/?ref=insta_bio`).

### Acceptance Criteria:
- *Given* I open the Creator tools in settings, *when* it loads, *then* the UI fetches referral counts from the database where `referred_by` equals my user ID.
- *Then* it displays my conversion metrics: "Link Clicks: 500", "App Installs: 150", "Accounts Created: 120", proving my marketing efforts are working.
