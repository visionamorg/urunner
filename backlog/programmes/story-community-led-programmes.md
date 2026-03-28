# Epic: Community Management & Growth
## Story: Feature - Community Training Programmes (Plans)

### Status: DONE
Completed: 2026-03-28

**As a** Community Admin
**I want to** create structured Training Programmes within my community
**So that** my members can easily follow a unified running plan (e.g., "Couch to 5k", "Sub-4 Marathon") together, boosting engagement and accountability.

### Description
Communities aren't just for chatting; they're for achieving goals *together*. We need a feature where admins can build a block of workouts (a "Programme" or "Plan") spanning multiple weeks. Members can click "Enroll", and these workouts will automatically sync to their personal training calendars. 
Furthermore, a leaderboard should track who is completing the programme workouts diligently versus who is skipping them, inciting friendly competition.

### Acceptance Criteria
- [x] Admins see a new "Programmes" tab in the Community dashboard.
- [x] Admin can create a Programme: Title, Description, Duration (e.g., 4 weeks), and Skill Level.
- [x] Admin can add daily "Workouts" to the Programme (e.g., Week 1, Day 1: 5km Easy Run).
- [x] Community members can view active programmes and click "Enroll". 
- [x] Enrolled users have the programme's workouts injected into their personal running calendar.
- [x] A progress bar/completion tracker is visible inside the Programme detail page showing enrolled members' compliance (e.g., "John Doe - 80% completed").

### Technical Notes for Claude
- You'll need to create new entities: `Programme`, `ProgrammeWorkout`, and `ProgrammeEnrollment`.
- Relate these to `Community` and `User`.
- For the frontend, build a kanban-style or timeline UI in `frontend/src/app/features/communities/` where the admin can drag-and-drop workouts into weeks/days.
- When querying a User's personal calendar, the backend should aggregate `ProgrammeWorkout` items based on their enrollments.
