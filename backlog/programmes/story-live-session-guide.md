# Story: Live Session Guide (Taking the Run)

### Status: DONE
Completed: 2026-03-28

## 🎯 Goal
A "Distraction-Free" session view for runners to follow their structured workout in real-time.

## 👤 User Story
`As a Runner, I want to see my current interval (Warmup, Interval 1/8, Cooldown) on my phone so that I stay on track with my AI plan.`

## 🛠️ Acceptance Criteria
- [x] UI: Full-screen "Workout Mode" with high-contrast text and a progress bar.
- [x] Pace Monitoring: A green/red indicator showing if the runner is in the "Target Pace Range".
- [x] Audio Cues: Text-to-speech for interval transitions (e.g. "Start 400m at 4:00 pace").
- [x] Completion: Automatically marks the session as `COMPLETED` in `user_program_progress`.
- [x] Analysis: Compare "Target Pace" vs "Actual Pace" immediately after the run.

## 🚀 Powerful Addition: "The Ghost Runner"
Visualize a "Ghost Runner" on a map or a progress bar. If the goal is a 5:00/km pace, see a ghost icon representing that pace. The runner can "race" their training goal.

## 💡 Technical Strategy
1.  Read `program_sessions` for the current user/date.
2.  Use `Geolocation.watchPosition` to track real-time pace.
3.  Calculate 'Instantaneous Pace' vs 'Average Segment Pace'.
4.  Store data for 'Session Post-Mortem' (where did they slow down?).
