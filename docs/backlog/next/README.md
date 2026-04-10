# Next — MVP3 Planned Work

Concrete stories for the phase after the current sprint.
These are not being built yet but are fully defined enough to start any time.

---

## Domain Index

| File | Domain | Stories |
|------|--------|---------|
| [bugfixes.md](bugfixes.md) | Bug Fixes (B-001 → B-060) | ~60 bugs |
| [community-advanced.md](community-advanced.md) | Community: Store, Challenges, Advanced Roles | 8 stories |
| [notifications-realtime.md](notifications-realtime.md) | Real-Time Notification System | 3 stories |
| [garmin-suite.md](garmin-suite.md) | Garmin Integration Suite (G001–G007) | 7 stories |
| [events-advanced.md](events-advanced.md) | Events: Ticketing, Volunteers | 2 stories |
| [training-advanced.md](training-advanced.md) | Training: AI Adaptive, Community Programs | 4 stories |
| [payments-stripe.md](payments-stripe.md) | Payments: Stripe, Event Ticketing, CMI | 3 stories |
| [activity-telemetry.md](activity-telemetry.md) | Advanced Activity Telemetry & Analysis | 2 stories |

---

## How to Promote a Story to Sprint

1. Pick a story from any domain file below
2. Create a full story file in `../sprint/US-XXX-title.md` (copy `../_TEMPLATE.md`)
3. Add it to `../sprint/README.md` and `../README.md` Active Sprint table
4. Remove or cross out the entry in the domain file here

---

## Priority Recommendation for MVP3

Start with bugs and garmin — they fix existing pain points without adding new complexity:

1. 🔴 **bugfixes.md** — fix the N+1 queries, auth interceptor, and mobile nav overlap (B-019, B-028, B-025)
2. 🔴 **garmin-suite.md** — G001 (OAuth stability) + G002 (webhook sync) are foundational
3. 🟡 **notifications-realtime.md** — story 3+4 (push notifications, community triggers)
4. 🟡 **training-advanced.md** — AI adaptive recovery + community programs
5. 🟢 **events-advanced.md** — ticketing + volunteer management
6. 🟢 **payments-stripe.md** — Stripe checkout infrastructure (required for event ticketing)
7. 🟢 **community-advanced.md** — store + monetization (complex, do last)
