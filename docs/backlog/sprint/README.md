# Sprint — Active Phase (MVP2)

**Goal:** Transform RunHub from solo tracker → social platform
**Started:** 2026-04-10
**Progress:** 0/10 stories done

---

## Story Index

| Priority | ID | Title | Status |
|----------|----|-------|--------|
| 🔴 High | [US-010](US-010-photo-upload-posts.md) | Direct Photo Upload in Posts | [ ] Pending |
| 🔴 High | [US-007](US-007-public-profiles-and-follow.md) | Public Profiles & Follow System | [ ] Pending |
| 🔴 High | [US-008](US-008-share-activity-to-feed.md) | Share Activity to Feed | [ ] Pending |
| 🔴 High | [US-009](US-009-ai-coach-chat-ui.md) | AI Coach Chat UI | [ ] Pending |
| 🟡 Medium | [US-011](US-011-community-discovery-search.md) | Community Discovery & Search | [ ] Pending |
| 🟡 Medium | [US-012](US-012-real-time-notifications.md) | Real-Time Notifications (WebSocket) | [ ] Pending |
| 🟡 Medium | [US-013](US-013-city-segments.md) | City Segments & Segment Leaderboards | [ ] Pending |
| 🟢 Low | [US-014](US-014-shoe-mileage-alerts.md) | Shoe Mileage Alerts & Retirement | [ ] Pending |
| 🟢 Low | [US-015](US-015-weather-adaptive-coaching.md) | Weather-Adaptive Training Suggestions | [ ] Pending |
| 🟢 Low | [US-016](US-016-events-map-location.md) | Events Map & Route Integration | [ ] Pending |

---

## Recommended Build Order

1. **US-010** — photo upload: backend only, no dependencies, high user-visible impact
2. **US-007** — follow system: enables all other social features
3. **US-008** — share activity: depends on US-007
4. **US-011** — community search: standalone, improves retention
5. **US-009** — AI coach UI: backend already done, frontend only
6. **US-012** — real-time notifications: infrastructure story, unblocks future work
7. **US-013** — segments: new domain, needs Garmin GPS data
8. **US-014** — shoe alerts: quick win
9. **US-015** — weather coaching: no DB changes, external API only
10. **US-016** — events map: Leaflet integration, standalone

---

## When a story is done

1. Check all `[x]` AC boxes in the story file
2. Move the story file to `../done/`
3. Update the table above (remove the row)
4. Update `../done/README.md`
5. Update `../README.md` Quick Status table
