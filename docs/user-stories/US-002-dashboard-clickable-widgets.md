# US-002 — Dashboard Clickable Widgets & Calendar

## Status
[x] Done

## Story
As a user of the dashboard, I want every widget card to be clickable and navigate me to the relevant detail page, so I can quickly drill into any section of the platform from the home screen.

## Acceptance Criteria

### Stats Cards (top row — 6 cards)
- [x] All 6 stat cards (Total KM, This Week KM, This Month KM, Total Runs, Avg Pace, Total Time) are clickable and navigate to `/activities`
- [x] Cards have hover animation (scale + border highlight)

### This Week Widget
- [x] Clicking the entire "This Week" card navigates to `/calendar`
- [x] Clicking an individual day circle navigates to `/calendar?date=YYYY-MM-DD` (opens that specific day)
- [x] "View calendar →" hint appears on hover

### Today's Schedule Widget
- [x] Clicking the card header navigates to `/calendar`
- [x] Each schedule item (activity or event) is clickable and navigates to its detail page
- [x] For `alice_runner` with no real activities today: shows example demo schedule items (Marathon Prep tempo run + evening stretching) linking to `/programs`
- [x] Real events link to `/events/:id`

### Recent Activities
- [x] Each activity row is clickable and navigates to `/activities`
- [x] Hover shows border highlight + title color change

### Upcoming Events
- [x] Each event card navigates to `/events/:id` (was already working — kept)

### Weekly Leaders
- [x] Each leaderboard row is clickable and navigates to `/rankings`

### Active Challenges
- [x] Each challenge row is clickable and navigates to `/activities`

### Active Programs
- [x] Each program card is clickable and navigates to `/programs`

## New Feature: Calendar Page (`/calendar`)
- [x] Monthly calendar grid showing Mon–Sun weeks
- [x] Orange dot = activity logged that day
- [x] Blue dot = event on that day
- [x] Click any day to see detail panel (activities + events for that day)
- [x] Detail panel items are clickable: activities → `/activities`, events → `/events/:id`
- [x] Month navigation (prev/next) + "Today" button
- [x] Supports `?date=` query param so "This Week" day clicks deep-link to correct month
- [x] Quick stats: activities and events count for the displayed month

## Navigation Map

| Widget | Destination |
|---|---|
| Stat cards (×6) | `/activities` |
| This Week card | `/calendar` |
| This Week day circle | `/calendar?date=YYYY-MM-DD` |
| Today's Schedule (header) | `/calendar` |
| Today's Schedule item | `/activities` or `/events/:id` or `/programs` |
| Activity row | `/activities` |
| Event card | `/events/:id` |
| Leaderboard row | `/rankings` |
| Challenge row | `/activities` |
| Program card | `/programs` |
