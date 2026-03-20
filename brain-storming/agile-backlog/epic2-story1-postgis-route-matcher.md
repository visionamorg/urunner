# Epic: Advanced Performance & Analytics
## Story: PostGIS Route Matcher

**As a** data-driven runner,
**I want to** automatically match my run to established community "Segments" (e.g., "Main Street Hill"),
**So that** I can see how I rank against others on specific stretches of a route.

### Acceptance Criteria:
- *Given* I upload a new run, *then* the backend uses PostGIS `ST_Intersects` to check if my route overlaps any defined segments.
- *When* a match is found, *then* my split time for that segment is added to the Segment Leaderboard.
