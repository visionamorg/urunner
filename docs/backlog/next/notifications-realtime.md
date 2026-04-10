# Real-Time Notification System (MVP3 additions)

The backend notification infrastructure and in-app notification center are already built (US-E02, epic3-story1 & story2 — done). These are the remaining stories.

---

## Already Done (Do NOT re-implement)
- ✅ `Notification` entity + `NotificationService` (backend)
- ✅ Bell icon + dropdown notification center (frontend)
- ✅ Community invite notifications

---

## US-N01 — Push & Email Notification Preferences

**Priority:** 🟡 Medium
**From:** `backlog/notifications/epic3-story3-push-email-integration.md`

**Problem:** Users get notifications in-app but can't configure which ones they receive by email or push. Email for invites exists but the preference UI is incomplete.

**Scope:**
- Profile settings: "Notification Preferences" section
- Toggles per notification type: LIKE, COMMENT, INVITE, EVENT_REMINDER, COMMUNITY_POST, BADGE_EARNED
- Two channels per type: In-App | Email
- `user_notification_preferences` table or JSONB column on users
- Backend respects preferences before sending each notification
- Browser push notifications via Web Push API (VAPID keys) — optional, show "Enable notifications" prompt

---

## ✅ US-N02 — Community Feed Notification Triggers — **DONE 2026-04-10**

**Priority:** 🟡 Medium
**From:** `backlog/notifications/epic3-story4-community-feed-triggers.md`

**Problem:** Admins post important updates (new event, pinned post) but members only see them if they happen to visit. There's no active alert.

**Scope:**
- When an admin **pins a post** in a community: notify all members
- When an admin creates a **new Event** in a community: notify all members who have "Event reminders" on
- When a user is **mentioned** (`@username`) in a comment: notify that user
- Mention detection: parse comment text for `@username` pattern, look up user, send notification
- All triggers call existing `NotificationService.push()` — just wire up the events

---

## ✅ US-N03 — 24h Event Reminder — **DONE 2026-04-10**

**Priority:** 🟢 Low

**Problem:** Users register for events but forget. A day-before reminder would improve attendance.

**Scope:**
- `@Scheduled` job runs daily at 8am
- Queries all events starting in the next 24–26 hours
- For each registered user: send in-app notification + email (if opted in)
- Notification: "Reminder: [Event Name] starts tomorrow at [time] — [location]"
