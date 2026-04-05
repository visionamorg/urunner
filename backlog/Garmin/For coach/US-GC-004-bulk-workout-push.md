# User Story: US-GC-004 - Bulk Workout Push to Garmin Calendars (Coach Tool)

### Status: DONE

**As a** coach,
**I want** to push a training session to an entire group of athletes' Garmin calendars at once,
**So that** I don't have to manually update each individual training schedule.

## Acceptance Criteria
- [ ] Implement a "Bulk Action" button on the Training Program scheduler.
- [ ] Allow selection of multiple athletes from the coach's "Team" list.
- [ ] Push the same workout (mapped to Garmin format via US-G004) to all selected users' Garmin Connect accounts.
- [ ] Provide a "Sync Status" dashboard for the coach to see who successfully received the workout.
- [ ] Distinguish between "Scheduled" (on the calendar) and "Completed" (as synced back from the activity).

## Technical Considerations
- **Backend Service**: `GarminClipboardService` that leverages existing `GarminTrainingService` (from US-G004) but executes in parallel for multiple users.
- **Queue**: Use a background job (e.g., Celery, Bull, or RabbitMQ) to handle bulk API calls into Garmin's system without blocking the coach's UI.
- **Persistence**: Store the `garmin_workout_id` for every unique athlete-workout combination for tracking and potential deletion/updates.
