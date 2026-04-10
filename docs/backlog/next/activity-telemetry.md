# Advanced Activity Telemetry (MVP3)

Deeper analysis of individual activities beyond the current total stats.

---

## Already Done (Do NOT re-implement)
- ✅ Activity tracking: distance, pace, duration, elevation, cadence, HR avg
- ✅ Activity detail page (basic stats)
- ✅ Performance chart (CTL/ATL/TSB)
- ✅ Health metrics dashboard

---

## US-A01 — AI Activity Summary & Insights

**Priority:** 🟡 Medium
**From:** `backlog/Activity/Activity-summrization_gpt.md`

**Problem:** After a run, the user sees raw stats but no interpretation. A 5:20/km average pace means nothing without context: "Was this a good run? Are you improving? Was the effort appropriate?"

**Scope:**
- After an activity is saved/synced, trigger an async AI analysis
- AI receives: pace, HR, distance, cadence, elevation gain, TSB at time of run, recent 30-day context
- AI returns a structured summary:
  - **Headline**: "Strong threshold effort — your best 10K pace in 3 weeks"
  - **Effort assessment**: Easy / Moderate / Hard / Max (based on HR zones)
  - **Recovery recommendation**: "Take tomorrow easy — your TSB is -18"
  - **Trend observation**: "Your cadence is up 4% this month — great form work"
- Summary displayed on activity detail page as an "AI Insight" card
- Stored in `running_activities.ai_summary JSONB`

**DB migration:**
```sql
ALTER TABLE running_activities ADD COLUMN ai_summary JSONB;
```

---

## US-A02 — Lap-Level Splits & HR Zones

**Priority:** 🟡 Medium
**From:** `backlog/Activity/epic6-story1-advanced-activity-telemetry.md`

**Problem:** Activities show only aggregate stats. Runners doing intervals need split-level data: How fast was each 1km? What was my HR in each zone?

**Scope:**
- When activity is synced from Garmin FIT file: parse lap data
- Store laps as JSONB: `[{lapNumber, distanceM, elapsedSec, avgPace, avgHR, avgCadence}]`
- Activity detail page: splits table with sortable columns
- HR Zones breakdown (% time in each zone: Z1 Recovery → Z5 Anaerobic) as a horizontal bar chart
- Personal best detection: "🏆 New best 1km split: 4:12"
- Pace chart: SVG line chart showing pace per lap/km over the activity duration

**DB migration:**
```sql
ALTER TABLE running_activities ADD COLUMN laps JSONB;
ALTER TABLE running_activities ADD COLUMN hr_zones JSONB;
```
