# US-013 — City Segments & Segment Leaderboards

**Status:** [ ] Pending
**Priority:** 🟡 Medium

---

## Problem

As identified in the strategy doc, RunHub must leverage **hyper-local context** to compete with Strava. There are no segments yet — no way to compare who ran the Corniche fastest or climbed Anfa Hill best. This is a key differentiator for the Casablanca running community.

---

## Story

As a **runner in Casablanca**, I want to see official "UR Casablanca" running segments so I can compare my time on local routes against the community and track my personal records on each segment.

---

## Acceptance Criteria

### Segment Data
- [ ] Admin (global admin role) can create segments:
  - Name, description, distance (km), start GPS coordinate, end GPS coordinate, difficulty
  - Example: "The Corniche Sprint", "Anfa Hill Climb", "Hassan II Loop"
- [ ] Segments are displayed on a dedicated `/segments` page
- [ ] Each segment card shows: name, distance, difficulty badge, KOM (King of the Mountain = fastest time), your personal best

### Segment Detection on Activities
- [ ] When a Garmin activity is synced (via webhook), the backend checks if the route passes through any known segments
- [ ] If detected, a `SegmentEffort` record is created: `(activity_id, segment_id, elapsed_time, rank)`
- [ ] Detection logic: GPS polyline from Garmin FIT file intersects within 50m of segment start/end points

### Segment Leaderboard
- [ ] Each segment has a leaderboard: top 10 by elapsed time (all time, this month)
- [ ] Current user's rank is highlighted
- [ ] "KOM" crown icon on #1 entry
- [ ] Paginated full leaderboard: `GET /api/segments/{id}/leaderboard?period=alltime`

### My Segments
- [ ] Profile page shows "My Segments" tab: segments the user has run with their best time and rank
- [ ] Personal best trend chart for a segment (if user ran it multiple times)

---

## Technical Notes

### Backend
- `Segment` entity: `id`, `name`, `description`, `distanceKm`, `startLat`, `startLng`, `endLat`, `endLng`, `difficulty`, `createdBy`
- `SegmentEffort` entity: `id`, `segmentId`, `activityId`, `userId`, `elapsedSeconds`, `recordedAt`
- `SegmentService.detectSegments(activity)`: called from `GarminWebhookController` after activity sync
- GPS matching: simple Haversine distance calculation — no complex spatial queries needed for MVP2
- `SegmentController`:
  - `GET /api/segments` — list all
  - `GET /api/segments/{id}/leaderboard?period=` — leaderboard
  - `POST /api/segments` (admin only) — create
  - `GET /api/segments/my-efforts` — current user's best efforts

### Frontend
- New page `features/segments/segments.component.ts` — route `/segments`
- Segment card component with leaderboard modal
- Add "Segments" link to sidebar nav

---

## Database Migration

```sql
CREATE TABLE segments (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    distance_km DECIMAL(6,3) NOT NULL,
    start_lat   DECIMAL(10,7) NOT NULL,
    start_lng   DECIMAL(10,7) NOT NULL,
    end_lat     DECIMAL(10,7) NOT NULL,
    end_lng     DECIMAL(10,7) NOT NULL,
    difficulty  VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    created_by  BIGINT REFERENCES users(id),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE segment_efforts (
    id              BIGSERIAL PRIMARY KEY,
    segment_id      BIGINT NOT NULL REFERENCES segments(id) ON DELETE CASCADE,
    activity_id     BIGINT NOT NULL REFERENCES running_activities(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    elapsed_seconds INT NOT NULL,
    recorded_at     TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_segment_efforts_seg_time ON segment_efforts(segment_id, elapsed_seconds);
```

### Seed Data (insert after migration)

```sql
INSERT INTO segments (name, description, distance_km, start_lat, start_lng, end_lat, end_lng, difficulty) VALUES
('The Corniche Sprint', 'Classic beachfront sprint along Casablanca Corniche', 2.5, 33.5965, -7.6335, 33.5820, -7.6560, 'EASY'),
('Anfa Hill Climb', 'Challenging uphill through the Anfa residential hills', 1.8, 33.5891, -7.6445, 33.5800, -7.6520, 'HARD'),
('Hassan II Loop', 'Full loop around the Hassan II Mosque waterfront', 5.0, 33.6087, -7.6325, 33.6087, -7.6325, 'MEDIUM');
```
