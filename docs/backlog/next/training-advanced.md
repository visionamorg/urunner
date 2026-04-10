# Advanced Training Features (MVP3)

Extensions to the training programs system beyond core MVP1.

---

## Already Done (Do NOT re-implement)
- ✅ Training programs + sessions (core)
- ✅ Live session guide with pace monitoring (story-live-session-guide — done 2026-03-28)
- ✅ AI training plan generator (story-ai-plan-generator — done 2026-03-28)

---

## US-T01 — AI Adaptive Recovery

**Priority:** 🟡 Medium
**From:** `backlog/programmes/story-ai-adaptive-recovery.md`

**Problem:** Training plans are static. If a runner reports fatigue, gets sick, or misses a session, the plan doesn't adapt. The AI generated a plan once and never updates it.

**Scope:**
- After each completed session, user rates effort (1–5) and fatigue level
- AI service re-evaluates upcoming 2 weeks based on: effort ratings, TSB from Performance chart, missed sessions
- If TSB drops into "Overreaching" zone: AI inserts a recovery week automatically
- Push notification: "Your plan was adjusted — you have a recovery session tomorrow"
- User can accept or reject the AI's proposed changes
- Backend: `PlanAdaptationService` runs after each `UserProgramProgress` update

---

## US-T02 — Community-Led Training Programs

**Priority:** 🟡 Medium
**From:** `backlog/programmes/story-community-led-programmes.md`

**Problem:** Training plans are only for individual users. Run clubs want to offer their own structured programs to members (e.g., "UR Casablanca 10-Week Marathon Prep").

**Scope:**
- Community admins can create a `Program` and publish it to their community
- Members can enroll in a community program (same as individual programs)
- Admin sees enrollment count per session
- Admin can push a session's workout to all enrolled Garmin-connected members (reuse US-006 bulk push)
- Community Programs tab in community detail

---

## US-T03 — Elite Performance Dashboard

**Priority:** 🟢 Low
**From:** `backlog/programmes/story-elite-performance-dashboard.md`

**Problem:** The Performance chart shows CTL/ATL/TSB but serious athletes want a deeper view: VO2Max trend, Efficiency Factor (EF = pace/HR), training monotony, strain index.

**Scope:**
- New metrics on Performance page:
  - **Efficiency Factor (EF)**: avg pace / avg HR per run — trending up = getting fitter at same pace
  - **Training Monotony**: standard deviation of daily load — high monotony = injury risk
  - **Strain Index**: CTL × monotony — shows total stress load
  - **VO2Max trend**: from Garmin health sync
- 90-day trend charts for each metric
- "Insight cards": AI-generated one-line observation per metric ("Your EF improved 8% this month")

---

## US-T04 — Premium Program Monetization

**Priority:** 🟢 Low
**From:** `backlog/programmes/story-premium-programme-monetization.md`

**Problem:** Coaches have no way to charge for training plans. A coach who creates a quality 16-week marathon program should be able to sell it.

**Scope:**
- Coach can mark a program as "Premium" and set a price
- Non-enrolled users see a locked preview — teaser of first 2 weeks
- "Enroll for €29" → Stripe checkout → access granted
- Coach earnings dashboard: revenue, enrollments, completion rate
- Requires `payments-stripe.md` infrastructure first
