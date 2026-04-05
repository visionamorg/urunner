# User Story: US-GC-006 - Coach-Athlete Garmin Feedback Hub

### Status: DONE

**As a** coach,
**I want** to provide comments and direct feedback on my athletes' Garmin activity laps and data summaries,
**So that** I can suggest adjustments to their pacing, form, or heart rate zones directly where the data is.

## Acceptance Criteria
- [ ] Add a "Coach Feedback" section at the bottom of every activity summary in Runhub.
- [ ] Allow coaches to comment on specific "Laps" or "Segments" within the activity detail page.
- [ ] Implement an "In-App Notification" for the athlete when the coach leaves feedback on their run.
- [ ] Provide an "Activity Score" or "Coach's Rating" (e.g., 1-10) for each session to help the athlete track their adherence to the plan.
- [ ] Add the ability for coaches to "Pin" specific activities to the athlete's dashboard for review.

## Technical Considerations
- **Backend Service**: `CoachingCommentService`. Should support threaded comments and rich text (Markdown) for better instruction.
- **Frontend**: Real-time comment updates using WebSockets or a real-time database (e.g., Firebase, Supabase).
- **Notifications**: Tie into the existing notification system (`backlog/notifications`).
