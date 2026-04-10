# Advanced Event Features (MVP3)

Extensions beyond basic event listing and registration.

---

## Already Done (Do NOT re-implement)
- ✅ Events list, registration, event detail page (core MVP1)
- ✅ GPX route upload + interactive map on event detail (epic4-story1 — done 2026-03-25)

---

## US-EV01 — Event Ticketing & QR Check-In

**Priority:** 🟡 Medium
**From:** `backlog/payments/epic5-story2` + `backlog/events/epic4-story2-ticketing-and-volunteers.md`

**Scope:**
- After registration (paid or free), user receives a unique QR code via email
- QR code encodes `{eventId, userId, token}` — verifiable by organizer
- Organizer app (or web page on mobile): scan QR code → confirm attendance
- `EventRegistration`: add `qrToken` column, `checkedIn BOOLEAN`, `checkedInAt TIMESTAMP`
- Attendance summary for admin: registered vs checked-in count, real-time during event

**DB migration:**
```sql
ALTER TABLE event_registrations
    ADD COLUMN qr_token     VARCHAR(64) UNIQUE,
    ADD COLUMN checked_in   BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN checked_in_at TIMESTAMP;
```

---

## US-EV02 — Volunteer Management

**Priority:** 🟢 Low
**From:** `backlog/events/epic4-story2-ticketing-and-volunteers.md`

**Problem:** Large events (marathons, trail races) need water station volunteers, pacers, and finish line marshals. There's no way to recruit or assign them.

**Scope:**
- Event has "Volunteer Roles" (e.g., Water Station km5, Pacer 5:00/km, Finish Line Marshal)
- Each role has a capacity (e.g., 3 people per water station)
- "Volunteer for this event" button → user selects a role → registered as volunteer
- Volunteer list visible to event organizer
- Volunteers receive a separate confirmation email with their assignment details
- "Volunteer" badge on their event registration card

**DB migration:**
```sql
CREATE TABLE event_volunteer_roles (
    id           BIGSERIAL PRIMARY KEY,
    event_id     BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    title        VARCHAR(100) NOT NULL,
    description  TEXT,
    capacity     INT NOT NULL DEFAULT 1
);
CREATE TABLE event_volunteers (
    id        BIGSERIAL PRIMARY KEY,
    role_id   BIGINT NOT NULL REFERENCES event_volunteer_roles(id),
    user_id   BIGINT NOT NULL REFERENCES users(id),
    UNIQUE (role_id, user_id)
);
```
