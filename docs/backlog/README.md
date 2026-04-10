# RunHub — Backlog

Single source of truth for all product work. One folder, four states.

---

## How It Works

```
docs/backlog/
  done/     ← shipped & verified
  sprint/   ← being built right now (current phase)
  next/     ← concrete plans for the phase after current
  ideas/    ← unscheduled ideation pool (not being built yet)
```

**Rule:** a story moves forward through the states, never backwards.
`ideas/ → next/ → sprint/ → done/`

---

## Quick Status

| State | Phase | Stories | Done |
|-------|-------|---------|------|
| ✅ done | MVP1 | 8 | 8/8 |
| 🔨 sprint | MVP2 | 10 | 0/10 |
| 📋 next | MVP3 | ~30 | 0 |
| 💡 ideas | Future | 150+ | — |

---

## Active Sprint (MVP2)

> **Goal:** Social graph, activity sharing, AI coach UI, photo upload, real-time notifications, city segments.

| Priority | ID | Story | Status |
|----------|----|-------|--------|
| 🔴 | [US-010](sprint/US-010-photo-upload-posts.md) | Direct Photo Upload in Posts | [ ] |
| 🔴 | [US-007](sprint/US-007-public-profiles-and-follow.md) | Public Profiles & Follow System | [ ] |
| 🔴 | [US-008](sprint/US-008-share-activity-to-feed.md) | Share Activity to Feed | [ ] |
| 🔴 | [US-009](sprint/US-009-ai-coach-chat-ui.md) | AI Coach Chat UI | [ ] |
| 🟡 | [US-011](sprint/US-011-community-discovery-search.md) | Community Discovery & Search | [ ] |
| 🟡 | [US-012](sprint/US-012-real-time-notifications.md) | Real-Time Notifications (WebSocket) | [ ] |
| 🟡 | [US-013](sprint/US-013-city-segments.md) | City Segments & Segment Leaderboards | [ ] |
| 🟢 | [US-014](sprint/US-014-shoe-mileage-alerts.md) | Shoe Mileage Alerts & Retirement | [ ] |
| 🟢 | [US-015](sprint/US-015-weather-adaptive-coaching.md) | Weather-Adaptive Training Suggestions | [ ] |
| 🟢 | [US-016](sprint/US-016-events-map-location.md) | Events Map & Route Integration | [ ] |

Full sprint details → [`sprint/README.md`](sprint/README.md)

---

## Navigation

| Folder | What's inside |
|--------|--------------|
| [`done/`](done/README.md) | All shipped MVP1 work (US-001 → US-E02) |
| [`sprint/`](sprint/README.md) | MVP2 stories — current focus |
| [`next/`](next/README.md) | MVP3 — concrete plans, not yet started |
| [`ideas/`](ideas/README.md) | Future ideation pool — domain-organized |

---

## How to Add a Story

1. Copy `_TEMPLATE.md` to the right state folder
2. Name it `US-XXX-short-title.md` (next available number after US-016)
3. Fill in: Problem, Story sentence, Acceptance Criteria, Technical Notes
4. Add a row to the state's `README.md` table
5. Add a row to the **Active Sprint** table above if it's in `sprint/`

---

## How to Promote a Story

- **ideas → next**: add to a `next/` domain file, assign a US-XXX number
- **next → sprint**: create a full story file in `sprint/`, add to Active Sprint table
- **sprint → done**: move file to `done/`, check all AC boxes, update done/README.md
