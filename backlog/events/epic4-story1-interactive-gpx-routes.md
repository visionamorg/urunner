# Epic 4: Advanced Event Management
## Story 1: Feature - Interactive GPX Routes & Elevation Profiles

**As an** Event Organizer
**I want to** upload a GPX file of the run route for my event
**So that** participants can view an interactive map and elevation profile, helping them prepare for the race.

### Description
Currently, community events only have a text-based "Location". To provide a premium runner experience, we need to show exactly where the run goes. Organizers should be able to upload a `.gpx` file. The frontend will then parse this file and render a beautiful, interactive Map (using Leaflet or Mapbox) along with an elevation chart right on the Event detail page.

### Acceptance Criteria
- [ ] Add `routeGpxUrl` and `elevationGainMeters` to the `Event` entity in the backend.
- [ ] Admins can upload a `.gpx` file when creating or editing an event (stored securely in an S3 bucket or local storage).
- [ ] Display an interactive map component on the Event details page rendering the GPX trace line.
- [ ] Display an elevation profile chart (e.g., using ApexCharts) extracted from the GPX data beneath the map.
- [ ] Provide members a "Download GPX" button so they can push the route to their Garmin or Coros watches.

### Technical Notes for Claude
- Backend: Use a library like `jenetics.jpx` to parse the GPX file on upload to automatically calculate and store `elevationGainMeters` and total `distanceKm`.
- Frontend: Use `leaflet` and `leaflet-gpx` for mapping.
- Ensure the map UI is sleek, full-width, and mobile-friendly.
