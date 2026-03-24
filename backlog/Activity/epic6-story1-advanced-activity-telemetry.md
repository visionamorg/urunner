# Epic 6: Activity Telemetry & Strava/Garmin Sync
## Story 1: Feature - Deep Dive Activity Stats & Interactive Map

**As a** Dedicated Runner
**I want to** see detailed telemetry data (Heart Rate, Cadence, Elevation) and an interactive map for my runs synced from Strava or Garmin
**So that** my Activity Details page isn't just basic text, but rather a rich dashboard displaying my true performance alongside my AI insights.

### Description
Currently, the `RunningActivity` model only stores basic scalar values (`distanceKm`, `durationMinutes`, `pace`, `location`). While the AI assistant provides great summaries, the visual UI is barren compared to what runners expect from Strava or Garmin. When we sync an activity (via the `externalId` source), we should pull the rich data points: the GPS map polyline, the total elevation gain, average/max heart rate, average cadence/power, and the kilometer-by-kilometer splits (laps). 

The Activity Details page on the frontend should be heavily upgraded to render this.

### Acceptance Criteria
- [ ] Add new fields to `RunningActivity.java`: `Integer elevationGainMeters`, `Integer avgHeartRate`, `Integer maxHeartRate`, `Integer avgCadence`, and `String mapPolyline` (to store the encoded Google polyline string from Strava).
- [ ] Create a new entity `ActivitySplit` mapped `@ManyToOne` to `RunningActivity`, storing `splitKm` (e.g., 1, 2, 3...), `splitPace`, `splitElevation`, and `splitHeartRate`.
- [ ] Update the Strava/Garmin Sync Service to fetch and map these fields from their respective Activity Detail APIs instead of just the summary API.
- [ ] Upgrade the Angular Activity Details Frontend:
   - **Interactive Route Base:** Add a Map component (e.g., `leaflet` or `mapbox-gl`) that decodes and plots the `mapPolyline` string directly on the screen.
   - **Telemetry Charts:** Add an `ApexCharts` graph below the map plotting Pace/Elevation/Heart Rate.
   - **Splits Table:** Render a clean data table showing the runner's stats per kilometer.
- [ ] The existing AI Chatbot (`ActivityAIController`) UI should be positioned gracefully next to these new rich widgets, allowing the AI to actually "see" the raw data if asked questions about specific splits.

### Technical Notes for Claude
- **Strava API:** The Strava `GET /activities/{id}` endpoint returns a `map` object containing `polyline` or `summary_polyline`. It also returns a `splits_metric` array. Use these fields to populate our new entities.
- **Frontend Mapping:** To decode the polyline string in Angular/Typescript without heavy libraries, you can use the open-source `polyline` algorithm (or `@mapbox/polyline` npm package).
- **Database:** Ensure the `mapPolyline` column is set to `TEXT` or `VARCHAR(MAX)` as complex GPS tracks can be very long strings.
