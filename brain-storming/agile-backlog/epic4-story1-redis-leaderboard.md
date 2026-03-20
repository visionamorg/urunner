# Epic: Infrastructure & Developer Experience (DX)
## Story: Redis Leaderboard Caching

**As a** systems engineer,
**I want to** cache the global and community-level leaderboards using Redis Sorted Sets (`ZSET`),
**So that** the PostgreSQL database isn't overwhelmed by heavy ranking queries during high-traffic periods (like Sunday evenings).

### Acceptance Criteria:
- *Given* a user asks for the Top 100 runners ranking, *when* the API is hit, *then* it fetches the list from Redis in <10ms instead of doing a full SQL `ORDER BY distance DESC` query.
