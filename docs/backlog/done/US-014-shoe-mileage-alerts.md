# US-014 — Shoe Mileage Alerts & Retirement

**Status: DONE** — Completed 2026-04-10


**Status:** [ ] Pending
**Priority:** 🟢 Low

---

## Problem

Shoe tracking exists (model, brand, max distance) but there are no alerts when a shoe approaches its mileage limit. The strategy doc specifically calls out shoe lifecycle tracking as a health differentiator — runners can develop knee injuries from worn-out shoes.

---

## Story

As a **runner**, I want to be alerted when my running shoes approach their mileage limit so I can replace them before they cause injury.

---

## Acceptance Criteria

### Mileage Progress in Profile
- [x] Gear tab on profile shows each shoe with a progress bar: current KM / max KM
- [x] Color: green (< 70%), yellow (70–90%), red (> 90%)
- [x] Progress bar shows percentage and "X km remaining"

### Alert Thresholds
- [x] At 80%: orange warning badge on the shoe card — "Getting worn — consider replacing soon"
- [x] At 100%: red "Retire" badge — "This shoe has exceeded its recommended distance"
- [x] Retired shoes can be marked as "Retired" — they disappear from the active gear list but stay in history

### Notification
- [x] When a new activity is synced and it pushes a shoe over the 80% threshold, a notification is sent:
  - "Your Nike Vaporfly (442km) has reached 80% of its recommended life. Consider replacing soon."
- [x] Notification also appears as a real-time toast (if US-012 is implemented)

### Default Shoe Auto-Assignment
- [x] When a shoe is set as "default", all new activities auto-assign to that shoe unless overridden
- [x] Activity detail page shows which shoe was used (if assigned), with a "Change shoe" button
- [x] `PUT /api/shoes/{shoeId}/assign-activity/{activityId}` endpoint

### Retire Flow
- [x] Shoe card in profile has "Retire" button (visible when > 90% or manually)
- [x] Retired shoes are hidden from activity assignment but shown in "Archived Gear" section

---

## Technical Notes

### Backend
- `ShoeService.assignActivityToShoe(activityId, shoeId)` — update `running_activities.shoe_id`
- `ShoeService.checkMilestoneAndNotify(userId, shoe)` — called after activity sync
- `Shoe` model: add `retired BOOLEAN DEFAULT FALSE`
- `ActivityController` or `SyncController`: call `ShoeService` after activity save
- `GET /api/shoes` already exists — add `currentKm`, `percentUsed`, `retired` to response DTO

### Frontend
- Profile gear tab: replace static mileage text with progress bar component
- `ShoeCardComponent`: reusable card with progress bar + action buttons
- Activity detail: shoe assignment dropdown

---

## Database Migration

```sql
ALTER TABLE shoes ADD COLUMN IF NOT EXISTS retired BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE running_activities ADD COLUMN IF NOT EXISTS shoe_id BIGINT REFERENCES shoes(id);
```
