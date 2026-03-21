# Epic: Personal Branding & Growth Features
## Story: Dynamic Open Graph (OG) Images for Link Sharing

**As a** user sharing my run profile link on Twitter or LinkedIn,
**I want the** link preview card to automatically display a stunning, dynamically generated image of my recent stats or route map,
**So that** my social media post stands out and drives massive click-through-rates to the app.

### Acceptance Criteria:
- *Given* a user pastes a link to their public RunHub profile (`runhub.io/u/macbook`), *when* Twitter/iMessage scrapes the `<meta property="og:image">` tag, *then* it returns a dynamically generated server-side image.
- *Then* the generated image visually summarizes the user's total distance this week and their profile picture, acting as a mini-billboard for the app.
