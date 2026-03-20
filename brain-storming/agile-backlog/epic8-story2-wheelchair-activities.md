# Epic: Accessibility & Inclusivity
## Story: Wheelchair Activity Types & Routing

**As a** wheelchair athlete,
**I want to** log my activities as "Wheelchair Push" rather than "Run",
**So that** my metrics are tracked accurately and I am provided with routes that avoid stairs and extreme unpaved inclines.

### Acceptance Criteria:
- *Given* I am setting up my profile or logging an activity, *when* I select activity type, *then* "Wheelchair" is a primary option.
- *Given* I search for nearby community routes, *when* the PostGIS database queries for paths, *then* it filters out routes containing known stairs or obstacles using OpenStreetMap accessibility tags.
