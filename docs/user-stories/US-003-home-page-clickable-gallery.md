# US-003 — Home Page Clickable Cards & Community Photo Gallery

## Status
[x] Done

## Story
As a visitor on the landing page, I want every card, feature tile, and stat widget to be clickable and take me to the relevant action (sign up / sign in), and I want to see real community photos that showcase the running experience to inspire me to join.

## Acceptance Criteria

### Clickable Landing Page Cards

#### Stats Row (Hero section — 4 cards)
- [x] Total Runners → `/register`
- [x] Communities → `/register`
- [x] KM Tracked → `/register`
- [x] Events → `/register`
- [x] Hover: scale up, border highlight, icon scale

#### Feature Cards (8 cards)
- [x] All 8 feature cards clickable → `/register`
- [x] Hover: card lifts (-translate-y-1), border highlight, title turns primary color
- [x] "Get started →" arrow appears on hover

#### How It Works Steps (3 cards)
- [x] All 3 step cards clickable → `/register`
- [x] Hover: card lifts, icon scales, title color change

#### Integration Cards (Strava + Garmin)
- [x] Strava integration card → `/register`
- [x] Garmin integration card → `/register`

### Community Moments Photo Gallery

- [x] New "Community in Motion" section between Features and How It Works
- [x] Masonry photo grid (CSS columns, 2–4 columns responsive)
- [x] 8 photos from real Adidas Runners Casablanca events
- [x] On hover: photo scales (zoom-in), overlay shows caption + "Join the community →"
- [x] Orange arrow badge appears top-right on hover
- [x] Each photo card links to `/register`
- [x] Graceful fallback: if photo file missing, shows a placeholder icon
- [x] "Join the Community" CTA button below gallery

### Navigation
- [x] "Community" anchor link added to desktop and mobile navs → `#community`
- [x] Footer links updated to include "Community" section

## Photo Files
Place the 8 community photos in:
```
frontend/public/community/
  photo1.jpg  — Runner with bib #442 after the race
  photo2.jpg  — "Never Give Up" sign (Adidas Runners Casablanca)
  photo3.jpg  — Runner #184 high-fiving on palm-lined street
  photo4.jpg  — DJ overlooking marathon start crowd
  photo5.jpg  — Adidas Lightstrike Pro shoes "To run is to live"
  photo6.jpg  — Runner making heart shape, AR Casablanca jersey
  photo7.jpg  — Runner in yellow shirt (Adidas shoes)
  photo8.jpg  — "Tu es au top!" supporter sign
```

## Navigation Map

| Element | Destination |
|---|---|
| Stats cards (×4) | `/register` |
| Feature cards (×8) | `/register` |
| How it works steps (×3) | `/register` |
| Strava integration row | `/register` |
| Garmin integration row | `/register` |
| Community photo cards (×8) | `/register` |
| "Join the Community" CTA | `/register` |
