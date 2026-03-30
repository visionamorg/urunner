# User Story: US-GC-002 - The Unified Team Performance Feed

**As a** coach,
**I want** to see a single activity feed of all my connected athletes,
**So that** I don't have to check each member's profile individually to see their morning run.

## Acceptance Criteria
- [ ] Implement a "Performance Hub" feed for coaches showing:
    - Athlete name.
    - Activity type (Run, Trail, Swim).
    - Distance, Pace, and Average Heart Rate.
    - Garmin Device used (e.g., Fenix 7X, Forerunner 955).
- [ ] Add filters for "Athlete Group", "Activity Type", and "Date Range".
- [ ] Display Garmin's "Training Effect" (Aerobic/Anaerobic) for each activity in the feed.
- [ ] Enable one-click navigation from the feed to a detailed workout analysis page.

## Technical Considerations
- **Math Engine**: Efficiently fetching activities for multiple athletes at once. Use optimized indexed queries on `user_id` and `start_time`.
- **API**: Ensure that full Garmin specific metrics (Power, Vertical Ratio, etc.) are available for coach inspection.
- **Cache**: Implement a Redis or local cache for the team feed to ensure sub-100ms load times for large teams (50+ athletes).
