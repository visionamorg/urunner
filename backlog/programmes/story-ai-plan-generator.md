# Story: AI Training Plan Architect

### Status: DONE
Completed: 2026-03-28

## 🎯 Goal
Allow users to generate a high-quality, scientifically-backed training plan based on their personal goals, current fitness, and weekly schedule.

## 👤 User Story
`As a Runner, I want to tell the AI my goal (e.g. Marathon in 3.45hrs in 16 weeks) so that I can have a professional plan without hiring a coach.`

## 🛠️ Acceptance Criteria
- [x] UI: A multi-step form for Goal, Current Volume (km/week), Days available to train, and Past results.
- [x] Backend: An LLM-powered service that takes user data and generates a JSON structure for the `programs` and `program_sessions` tables.
- [x] Intelligence: The plan must include distinct phases: BASE, BUILD, PEAK, and TAPER.
- [x] Integration: Generated programs are automatically added to the user's dashboard.

## 🚀 Powerful Addition: "The Strava Baseline"
Don't just ask the user for their pace. **Auto-analyze the last 5 activities** to set their "Critical Power" or "Threshold Pace". If they've been running at 5:30/km for their easy runs, the AI should NOT suggest a 4:30/km marathon goal yet, it should suggest a "Base Building" phase first.

## 💡 Technical Strategy
1. Fetch latest activities via Strava/Garmin API.
2. Send prompt to Gemini with user profile + activity history.
3. Validate JSON output to ensure no "Rest Days" have workouts assigned.
4. Scale workouts progressively (no more than 10% volume increase per week).
