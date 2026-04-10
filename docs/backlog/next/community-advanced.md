# Community Advanced Features (MVP3)

Stories that extend communities beyond the core MVP1 feature set.

---

## US-C01 — Custom Member Roles & Tags

**Priority:** 🟡 Medium
**From:** `backlog/community/epic2-story2-advanced-member-management.md`

**Problem:** The three fixed roles (ADMIN / MODERATOR / MEMBER) are not enough for large run clubs. A community like "UR Casablanca" needs "Pacer", "Coach", "Elite", "VIP" tags with custom colors.

**Scope:**
- Admin can create custom tags: name + hex color (stored as JSONB in `communities.custom_roles`)
- Admin assigns tags to members from the Members tab
- Tags appear as colored badges beside usernames in: Feed, Chat Rooms, Leaderboards
- Members tab: filter members by tag
- Activity monitoring view: show each member's "Last Run Logged" and "Messages Sent (30d)"

---

## US-C02 — Community Challenges

**Priority:** 🟡 Medium
**From:** `backlog/enhancements/story-community-challenges.md`

**Problem:** No collective goals. Communities can't rally members around a shared target (e.g., "Casablanca 1000km total in May").

**Scope:**
- Admin creates a challenge: name, start date, end date, target type (total distance / total runs / elevation), target value
- "Challenges" tab in community view with live progress bar
- Aggregate `running_activities` for all members within the challenge timeline
- Auto-award "Challenge Finisher" badge when goal is met
- Inter-community challenge: two communities compete on average distance per member (leaderboard trophy)

**DB migration:**
```sql
CREATE TABLE community_challenges (
    id           BIGSERIAL PRIMARY KEY,
    community_id BIGINT NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    title        VARCHAR(200) NOT NULL,
    target_type  VARCHAR(20) NOT NULL, -- DISTANCE, RUNS, ELEVATION
    target_value DECIMAL(10,2) NOT NULL,
    start_date   DATE NOT NULL,
    end_date     DATE NOT NULL,
    created_by   BIGINT REFERENCES users(id)
);
```

---

## US-C03 — Daily & Weekly Agenda Image Generation

**Priority:** 🟡 Medium
**From:** `backlog/community/story-generate-daily-agenda-image.md`

**Problem:** Community admins manually create social media graphics for their weekly run schedule. This should be automated.

**Scope:**
- Admin triggers "Generate Agenda Image" from community settings
- Backend compiles upcoming events for the next 7 days into a styled image (using html2canvas or a server-side renderer)
- Image is available to download as PNG — ready to post on WhatsApp / Instagram
- Template: RunHub brand style with community name, dates, event titles, location

---

## US-C04 — Community Store (Stripe Connect)

**Priority:** 🟢 Low
**From:** `backlog/community/epic3-story1 through epic3-story5`

**Problem:** Run clubs sell branded gear (t-shirts, bibs, race kits) through WhatsApp or external links. They need an integrated store.

**Scope (5 sub-stories):**
1. **Store Setup**: "Set up Store" in Community Settings → Stripe Connect Express onboarding
2. **Product Management**: Admin adds products (name, price, images, stock, variants: size/color)
3. **Storefront UI**: Public store tab on community page — product cards, filters
4. **Cart & Checkout**: Add to cart → Stripe Checkout Session → payment
5. **Order Management**: Admin dashboard for orders, fulfillment status, CSV export

**Note:** This is a significant feature. Requires Stripe integration (see `payments-stripe.md`) first.

---

## US-C05 — Community Monetization (Premium Memberships)

**Priority:** 🟢 Low
**From:** `backlog/community/epic2-story3-community-monetization.md`

**Problem:** Community admins have no revenue model beyond event fees.

**Scope:**
- Admin can set a monthly/annual membership fee for a community
- Free tier vs paid tier features configurable per community
- Members pay via Stripe to unlock premium content (exclusive posts, training plans)
- Admin dashboard: monthly revenue, active subscriber count
