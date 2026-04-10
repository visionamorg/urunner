# Sprint — MVP2 Complete ✓

**Goal:** Transform RunHub from solo tracker → social platform
**Started:** 2026-04-10
**Completed:** 2026-04-10
**Progress:** 10/10 stories done — all moved to `../done/`

---

## Story Index — ALL SHIPPED

| Priority | ID | Title | Status |
|----------|----|-------|--------|
| 🔴 High | US-010 | Direct Photo Upload in Posts | ✅ Done |
| 🔴 High | US-007 | Public Profiles & Follow System | ✅ Done |
| 🔴 High | US-008 | Share Activity to Feed | ✅ Done |
| 🔴 High | US-009 | AI Coach Chat UI | ✅ Done |
| 🟡 Medium | US-011 | Community Discovery & Search | ✅ Done |
| 🟡 Medium | US-012 | Real-Time Notifications (WebSocket) | ✅ Done |
| 🟡 Medium | US-013 | City Segments & Segment Leaderboards | ✅ Done |
| 🟢 Low | US-014 | Shoe Mileage Alerts & Retirement | ✅ Done |
| 🟢 Low | US-015 | Weather-Adaptive Training Suggestions | ✅ Done |
| 🟢 Low | US-016 | Events Map & Route Integration | ✅ Done |

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
