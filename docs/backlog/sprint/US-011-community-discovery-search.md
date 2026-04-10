# US-011 — Community Discovery & Search

**Status:** [ ] Pending
**Priority:** 🟡 Medium

---

## Problem

The communities list shows all public communities but has no search, no filtering by category, and private communities are invisible. Users have no way to find niche running clubs or see which communities are trending.

---

## Story

As a **runner**, I want to search and browse communities so I can discover running clubs that match my location, running style, or interests.

---

## Acceptance Criteria

### Search & Filter Bar
- [ ] Search input on the communities list page (`/communities`)
- [ ] Filter chips: All | Road | Trail | Marathon | Ultra | Casual
- [ ] Sort options: Most Members | Newest | Most Active (posts this week)
- [ ] Search hits community name and description
- [ ] `GET /api/communities?search=&category=&sort=` endpoint

### Community Cards — Enriched
- [ ] Card shows: cover photo, name, member count, category tag, recent activity count
- [ ] "Private" lock icon for private communities
- [ ] "Joined" badge if the user is already a member
- [ ] For private communities the user isn't in: "Request to Join" button instead of "Join"

### Join Requests for Private Communities
- [ ] `POST /api/communities/{id}/request-join` — creates a pending join request
- [ ] Admin sees join requests in the existing Invites tab (new section: "Join Requests")
- [ ] Admin can approve (`POST /api/communities/{id}/requests/{requestId}/approve`) or decline
- [ ] User gets a notification when their request is approved/declined

### Featured / Trending Section
- [ ] Top 3 most active communities shown in a featured row above the list
- [ ] Activity = number of posts created in the last 7 days

---

## Technical Notes

### Backend
- `CommunityRepository`: add `findByNameContainingIgnoreCase`, `findByCategory`, sorted queries
- New table `community_join_requests` (`id`, `community_id`, `user_id`, `status`, `created_at`)
- `CommunityDto`: add `recentPostCount` (posts in last 7 days), `category`
- `Community` model: add `category VARCHAR(20)` column

### Frontend
- Communities list component: add search input, filter chips (use `FormsModule`)
- Debounce search with `debounceTime(300)` + `switchMap` using `RxJS`
- Join request handling in community detail component (existing Invites tab extended)

---

## Database Migration

```sql
ALTER TABLE communities ADD COLUMN category VARCHAR(20);

CREATE TABLE community_join_requests (
    id           BIGSERIAL PRIMARY KEY,
    community_id BIGINT NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (community_id, user_id)
);
```
