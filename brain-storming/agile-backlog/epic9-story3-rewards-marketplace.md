# Epic: Brand Collaborations & Reward Points
## Story: Rewards Marketplace

**As a** loyal user with accumulated RunPoints,
**I want to** visit an in-app "Rewards Marketplace",
**So that** I can spend my hard-earned points on real-world running gear, nutrition products, or race entries provided by partner brands.

### Acceptance Criteria:
- *Given* I navigate to the Marketplace, *then* I see a list of brand-provided rewards (e.g., "$10 off Maurten Gels - Cost: 5,000 Points").
- *When* I spend my points to claim a reward, *then* my RunPoints balance decreases, and the app generates a unique, one-time-use alphanumeric promo code.
- *Given* the promo code is generated, *then* the brand's API is notified or the code is verified against a pre-loaded batch to ensure validity.
