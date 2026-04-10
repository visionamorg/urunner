# US-001 — Modern Home Page (Landing Page)

**Status:** [x] Done

## Story
As a **visitor**, I want a modern, attractive home page that showcases RunHub's features and functionality, so that I understand the platform's value and can navigate to login or register.

## Acceptance Criteria
- [ ] Landing page is the first thing unauthenticated users see
- [ ] Page showcases all platform features: activity tracking, communities, events, rankings, training programs, chat, badges, AI coach
- [ ] Clear CTAs to login and register
- [ ] Modern, responsive design (mobile-first)
- [ ] Uses the existing design system (dark/light theme support, amber primary, Tailwind)
- [ ] Smooth animations and attractive UX/UI
- [ ] Navbar with logo and login button
- [ ] Hero section with bold headline and CTA
- [ ] Feature grid section
- [ ] Stats/social proof section
- [ ] How it works section
- [ ] Integration showcase (Strava, Garmin)
- [ ] Footer with branding

## Technical Notes
- Standalone Angular component at `features/home/home.component.ts`
- Route: `/home` (public, no auth guard)
- Root path `/` redirects to `/home` for unauthenticated users
- Uses existing design tokens from `styles.scss`
- Material Icons for all iconography
