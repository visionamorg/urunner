# US-006 — Garmin Workout Builder & Clipboard

## Status: [x] Done — 2026-03-30

## Problem
The current Garmin push system (`GarminClipboardController`, `GarminTrainingService`) only supports
pushing an existing `ProgramSession` (title + distance/duration) as a flat 3-step workout
(warmup → main → cooldown).  There is no UI for it and no way for users or coaches to build
structured, step-by-step workouts comparable to **Garmin Connect's workout builder**.

Coaches need to:
- Build a reusable workout with full interval structure (warmup, repeats, recoveries, cooldown).
- Push that workout to one or all of their connected athletes in one click.

Any Garmin-connected user needs to:
- Build a personal workout using the same builder.
- Push it directly to their own Garmin device / calendar.

---

## Personas
| Persona | Capability |
|---------|-----------|
| **Any user** (Garmin connected) | Build workouts, push to own Garmin |
| **Coach** (active coaching connections) | Build workouts, push to one / all athletes |

---

## Feature breakdown

### 1 — Structured Workout Model (backend)

New entity `GarminWorkout` (stored in `garmin_workouts` table):

| Column | Type | Notes |
|--------|------|-------|
| `id` | BIGINT PK | |
| `owner_id` | FK → users | creator |
| `title` | VARCHAR(200) | workout name |
| `sport` | VARCHAR(20) | `RUNNING` (default), `CYCLING`, `SWIMMING` |
| `description` | TEXT | optional |
| `steps` | JSONB | ordered list of `WorkoutStep` objects |
| `is_template` | BOOLEAN | true = visible in coach's template library |
| `created_at` | TIMESTAMP | |
| `updated_at` | TIMESTAMP | |

**`WorkoutStep` JSON shape** (matches Garmin Training API schema):

```json
{
  "order": 1,
  "stepType": "WARMUP | INTERVAL | RECOVERY | REST | COOLDOWN | REPEAT",
  "durationUnit": "TIME | DISTANCE | OPEN | LAP_BUTTON",
  "durationValue": 300000,
  "targetType": "NO_TARGET | PACE | HEART_RATE | CADENCE | POWER | SPEED",
  "targetLow": null,
  "targetHigh": null,
  "notes": "Easy jog to get moving",
  "repeatCount": null,
  "children": []
}
```

`REPEAT` steps have `repeatCount` (e.g. 5) and a `children` array with the steps inside the repeat group.

---

### 2 — Backend endpoints

All under `/api/garmin/workouts`:

| Method | Path | Who | Description |
|--------|------|-----|-------------|
| `GET` | `/api/garmin/workouts` | Auth user | List own workouts (+ templates) |
| `POST` | `/api/garmin/workouts` | Auth user | Create new workout |
| `PUT` | `/api/garmin/workouts/{id}` | Owner | Update workout |
| `DELETE` | `/api/garmin/workouts/{id}` | Owner | Delete workout |
| `POST` | `/api/garmin/workouts/{id}/push-self` | Auth user (Garmin connected) | Push to own Garmin + schedule on date |
| `POST` | `/api/garmin/workouts/{id}/push-athletes` | Coach | Push to one / all connected athletes |

**`push-athletes` request body:**

```json
{
  "athleteIds": [12, 45, 67],
  "scheduledDate": "2026-04-07"
}
```

Omit `athleteIds` (or pass `[]`) → push to **all** active athletes of the coach.

**`push-self` request body:**

```json
{
  "scheduledDate": "2026-04-05"
}
```

---

### 3 — `GarminTrainingService` upgrade

Replace `buildWorkoutJson(ProgramSession)` with `buildWorkoutJson(GarminWorkout)` that:
- Recursively serialises all steps including `REPEAT` groups (Garmin Training API supports nested steps).
- Maps `durationUnit / durationValue` directly (no auto-inference).
- Maps `targetType / targetLow / targetHigh` to the Garmin `target` object with proper units
  (pace in mm:ss/km → mm/km in seconds, HR in bpm, cadence in rpm).

Keep the old `buildWorkoutJson(ProgramSession)` path working so existing program-push is not broken.

---

### 4 — Frontend: Garmin Clipboard page

Route: `/garmin-clipboard`
Sidebar entry: "Garmin" (icon: `watch` or `dumbbell`) — visible to all authenticated users.

#### 4a — Workout Library panel (left)
- Cards listing the user's saved workouts.
- "New Workout" button → opens builder.
- Coach users additionally see a "Templates" tab with workouts marked `is_template = true`.
- Each card shows: title, sport icon, step count, total distance / duration estimate.
- Actions: Edit | Push to Me | (coach only) Push to Athletes | Delete.

#### 4b — Workout Builder (right panel / modal)
Inspired by the Garmin Connect workout builder:

```
┌─────────────────────────────────────────────────────────┐
│  Workout title  [Running ▾]       [Save]  [Push to Me]  │
├─────────────────────────────────────────────────────────┤
│  STEP 1 — Warmup                          [✕ remove]    │
│  Duration:  [5 min ▾]       Target: [Open ▾]            │
│  Notes: Easy jog…                                       │
├─────────────────────────────────────────────────────────┤
│  STEP 2 — Repeat  ×5                      [✕ remove]    │
│  │ STEP 2a — Interval                                   │
│  │ Duration: [1 km ▾]   Target: [Pace ▾] 4:30 – 4:45  │
│  │ STEP 2b — Recovery                                   │
│  │ Duration: [90 sec ▾]  Target: [Open ▾]              │
├─────────────────────────────────────────────────────────┤
│  STEP 3 — Cooldown                        [✕ remove]    │
│  Duration:  [5 min ▾]       Target: [Open ▾]            │
├─────────────────────────────────────────────────────────┤
│  [+ Add Step]  [+ Add Repeat Group]                     │
│                                                         │
│  Estimated: 8.5 km · ~48 min                            │
└─────────────────────────────────────────────────────────┘
```

Controls per step:
- **Step type** selector: Warmup / Interval / Recovery / Rest / Cooldown / Repeat
- **Duration** selector: `Time` (mm:ss) | `Distance` (km) | `Open` | `Lap button`
- **Target** selector: Open | Pace (min/km range) | Heart Rate (bpm range) | Cadence (rpm range)
- **Notes** free text (shown on watch)
- Drag handle to reorder steps (CDK drag-drop)

Repeat group:
- **Repeat count** spinner (1–99)
- Nested step list (same controls, indented)

Live **estimated total distance / time** recalculated as steps change.

#### 4c — Push to Athletes dialog (coach only)
Triggered by "Push to Athletes" on a workout card.

```
┌────────────────────────────────┐
│  Push "5×1km Threshold" to...  │
│  Schedule date: [2026-04-07]   │
│                                │
│  ☑ Alice Martin    🟢 Garmin   │
│  ☑ Bob Durand      🟢 Garmin   │
│  ☐ Carl Dupont     ⚠ No Garmin │
│                                │
│  [Select All]  [Push]          │
└────────────────────────────────┘
```

- Athletes without Garmin linked are shown greyed-out with a warning icon.
- After push: per-athlete success/fail badges shown inline.

---

### 5 — Database migration

```sql
CREATE TABLE garmin_workouts (
    id            BIGSERIAL PRIMARY KEY,
    owner_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title         VARCHAR(200) NOT NULL,
    sport         VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    description   TEXT,
    steps         JSONB NOT NULL DEFAULT '[]',
    is_template   BOOLEAN NOT NULL DEFAULT false,
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_garmin_workouts_owner ON garmin_workouts(owner_id);
```

---

## Acceptance Criteria

### Builder
- [x] User can create a workout with any combination of: Warmup, Interval, Recovery, Rest, Cooldown, Repeat steps
- [x] Repeat groups support nested steps (any type except Repeat)
- [x] Duration can be set as time (mm:ss), distance (km), Open, or Lap button
- [x] Target can be set as Open, Pace range (min/km), HR range (bpm), or Cadence range (rpm)
- [x] Estimated total distance and time updates live as steps are edited
- [x] Steps can be reordered via drag-and-drop
- [x] Workout is saved to the database (JSONB steps column)

### Self-push (any Garmin user)
- [x] "Push to Me" button opens a date picker then pushes the workout to the user's Garmin
- [x] The workout appears on the user's Garmin Connect calendar on the chosen date
- [x] If the user has no Garmin token, a clear error with a "Connect Garmin" link is shown

### Coach push
- [x] Coach sees "Push to Athletes" on every workout card
- [x] Dialog lists all active coaching athletes, showing Garmin connection status
- [x] Coach can select a subset or all athletes
- [x] Coach picks a scheduled date
- [x] After push, per-athlete result shown (✓ / error message)
- [x] Athletes without Garmin are disabled in the list (cannot be selected)
- [x] Backend verifies ACTIVE coaching connection before pushing to each athlete

### Backward compatibility
- [x] Existing `/api/garmin/clipboard/bulk-push` (ProgramSession-based) still works

---

## Implementation notes

- `steps` JSONB is read/written via `@Type(JsonType.class)` (Hypersistence Utils) or manually with `ObjectMapper` — pick whichever is already in the project.
- Garmin Training API rate-limit: respect `BATCH_SIZE = 20` / `100ms` sleep already present in `GarminTrainingService`.
- Pace target → Garmin expects `PACE` target with `targetLow` and `targetHigh` in **seconds per meter** (e.g., 4:30/km = 270/1000 = 0.270 sec/m). Convert in service layer.
- HR target → Garmin accepts bpm directly when `targetType = "HEART_RATE"`.
- Frontend drag-drop → use Angular CDK `DragDropModule` already available via `@angular/cdk`.
