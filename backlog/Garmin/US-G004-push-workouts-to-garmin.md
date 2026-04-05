# User Story: US-G004 - Push Structured Workouts to Garmin Connect

### Status: DONE

**As a** runner following a training plan,
**I want** to see my today's workout directly on my Garmin watch,
**So that** I can follow the prescribed intervals and segments without memorizing them.

## Acceptance Criteria
- [ ] Integrate with the Garmin Connect Training API.
- [ ] Map Runhub's internal workout structure (Warmup, Intervals, Cooldown) to Garmin's JSON workout format.
- [ ] Provide a "Send to Device" button on the workout detail page.
- [ ] Handle API rate limits and structured workout validation errors gracefully.

## Technical Considerations
- **Backend**: `GarminTrainingService`. Implement the POST request to `https://apis.garmin.com/training-api/rest/workout`.
- **Logic**: Convert Metric units (Runhub) to Garmin's expected units (meters/km/min-per-km).
