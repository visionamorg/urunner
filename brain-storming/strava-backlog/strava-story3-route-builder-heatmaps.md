# Epic: Strava Baseline Features
## Story: Route Builder & Personal Heatmaps

**As an** explorer,
**I want to** view a heatmap of every route I have ever run, and use it to build new routes,
**So that** I can visualize my territorial coverage and plan runs on streets I haven't visited yet.

### Acceptance Criteria:
- *Given* I navigate to the Maps tab, *when* I toggle "Personal Heatmap", *then* every polyline from all my historical activities is rendered on the map with glowing intensity proportional to the frequency of visits.
- *When* I use the "Build Route" tool to click points on the map, *then* it snaps to the most popular roads using the Global Heatmap data.
