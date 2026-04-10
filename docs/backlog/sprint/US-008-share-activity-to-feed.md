# US-008 — Share Activity to Feed

**Status:** [ ] Pending
**Priority:** 🔴 High

---

## Problem

Activities are tracked but completely private. There is no way to share a run as a post on the global feed or a community feed. This is the core social action on Strava — RunHub needs it.

---

## Story

As a **runner**, I want to share a completed activity to the global feed or a community feed, so that my running community can react, comment, and celebrate my progress with me.

---

## Acceptance Criteria

### Share Button on Activity
- [ ] Activity detail page (`/activities/:id`) has a "Share to Feed" button
- [ ] Clicking opens a share dialog with:
  - Caption text area (optional, pre-filled with activity title)
  - Target selector: "Global Feed" or a community the user belongs to (dropdown)
  - Privacy toggle: Public / Followers only
  - Preview card showing activity stats (distance, pace, time, route map thumbnail if available)
- [ ] Submitting creates a post of type `ACTIVITY_SHARE` on the selected feed

### Activity Post Card in Feed
- [ ] Posts of type `ACTIVITY_SHARE` render a dedicated card design:
  - Activity stat strip: Distance | Pace | Time | Elevation
  - Map thumbnail if GPS data exists (static map image via OSM or Mapbox)
  - User caption below stats
  - "View full activity →" link to `/activities/:id`
- [ ] Like and comment work the same as regular posts
- [ ] Activity posts show in the global feed and in community feeds

### Post Model Update
- [ ] New post type: `ACTIVITY_SHARE` (alongside existing `TEXT` and `PHOTO_ALBUM`)
- [ ] New column on `posts`: `activity_id BIGINT REFERENCES running_activities(id)`
- [ ] `PostDto` includes `activityId`, `activityStats` (distance, pace, duration, elevation) when type is `ACTIVITY_SHARE`

### Feed Page Update
- [ ] Activities list page shows "Share" icon/button on each activity row
- [ ] Already-shared activities show "Shared" badge (greyed) so users know they already posted it

---

## Technical Notes

### Backend
- `FeedService.createActivityPost(activityId, caption, communityId, userId)`:
  - Validate user owns the activity
  - Validate communityId (if given) — user must be a member
  - Create `Post` with `postType = ACTIVITY_SHARE`, `activityId` set
- `PostMapper`: when mapping `ACTIVITY_SHARE`, join `running_activities` and fill `activityStats` DTO
- New `ActivityShareRequest` DTO: `{ activityId, caption, communityId? }`
- Endpoint: `POST /api/feed/share-activity`

### Frontend
- `ShareActivityDialogComponent` — modal with caption + target selector
- `ActivityService.getMyJoinedCommunities()` — reuse CommunityService for the dropdown
- Feed card uses `@if (post.postType === 'ACTIVITY_SHARE')` to switch template

---

## Database Migration

```sql
ALTER TABLE posts ADD COLUMN activity_id BIGINT REFERENCES running_activities(id);
-- post_type already VARCHAR — just add 'ACTIVITY_SHARE' as a valid value (no migration needed)
```
