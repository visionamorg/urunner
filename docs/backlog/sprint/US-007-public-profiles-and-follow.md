# US-007 — Public Profiles & Follow System

**Status:** [ ] Pending
**Priority:** 🔴 High

---

## Problem

Right now `/profile` only shows the logged-in user's own data. There is no way to view another runner's profile, no concept of following/followers, and no social graph. This makes RunHub a solo tracking app rather than a social platform.

---

## Story

As a **runner**, I want to view other users' public profiles and follow them so I can see their activities in my feed and build a running community around shared progress.

---

## Acceptance Criteria

### Public Profile Page
- [ ] New route `/profile/:username` shows a public profile for any user
- [ ] Shows: avatar, name, username, bio, location, running category, PBs
- [ ] Shows: total KM, total runs, badges earned (public badges only)
- [ ] Shows: recent public activities (last 10)
- [ ] Shows: follow/unfollow button (disabled for own profile)
- [ ] Shows: follower count + following count

### Follow System
- [ ] `POST /api/users/:username/follow` — follow a user
- [ ] `DELETE /api/users/:username/follow` — unfollow
- [ ] `GET /api/users/:username/followers` — list followers
- [ ] `GET /api/users/:username/following` — list following
- [ ] Following a user adds their public activities to the logged-in user's feed
- [ ] A user can see who follows them in their own profile

### Search Users
- [ ] Search bar on `/rankings` or a new `/discover` page to find users by username
- [ ] `GET /api/users/search?q=` endpoint returns matching users

### Own Profile Page Update
- [ ] `/profile` (no param) shows own profile with follower/following counts
- [ ] "My profile" link in sidebar goes to `/profile/:myUsername`

---

## Technical Notes

### Backend
- New table `user_follows` (`follower_id`, `following_id`, `created_at`) — composite PK
- `UserService`: `follow()`, `unfollow()`, `getFollowers()`, `getFollowing()`, `isFollowing()`
- `UserController`: new endpoints above
- `FeedService.getGlobalFeed()`: include activities of followed users in feed response
- `SecurityConfig`: whitelist `GET /api/users/{username}` as public

### Frontend
- New component `features/profile/public-profile/public-profile.component.ts`
- Route: `/profile/:username` (no auth guard for viewing; auth guard for follow button)
- Own `/profile` redirects to `/profile/:myUsername`
- Avatar component already exists — reuse it
- Follow button uses optimistic update (same pattern as like button)

---

## Database Migration

```sql
CREATE TABLE user_follows (
    follower_id  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    following_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (follower_id, following_id)
);
CREATE INDEX idx_user_follows_following ON user_follows(following_id);
```
