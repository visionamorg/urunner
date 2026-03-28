# Story: AI Adaptive Recovery (Injury Prevention)

### Status: DONE
Completed: 2026-03-28

## 🎯 Goal
Protect the runner from overtraining and injury by dynamically adjusting the training plan based on psychological and physiological data.

## 👤 User Story
`As a Runner, I want the AI to tell me when to REST or change my workout if I am showing signs of fatigue or high heart rate variability.`

## 🛠️ Acceptance Criteria
- [x] Integration: Analysis of "Efficiency Factor" (Pace vs Heart Rate) over the last 7 days.
- [x] UI: A "Daily Readiness Score" (0-100) shown on the dashboard.
- [x] AI Logic: If readiness is < 40, the AI suggests "Swap with Rest Day" or "Convert to Recovery Pace".
- [x] Notification: Push notification in the morning if a "High Stress" state is detected.
- [x] Data: Store `readiness_score` and `fatigue_indicators` in `user_program_progress`.

## 🚀 Powerful Addition: "The 10% Rule Guard"
The AI automatically blocks any training plan modification that increases weekly volume by more than 10%, flagging it as a "High Injury Risk" adjustment.

## 💡 Technical Strategy
1.  Fetch latest Garmin/Strava activities and sync HRV (if available).
2.  Calculate rolling average of "Pace per Beat" (Distance / Total Beats).
3.  If current session performance is 2 standard deviations below the mean, trigger "Adaptive Recovery" prompt.
4.  Optionally: Ask the user a "How do you feel?" 1-5 survey after chaque session to refine the AI model.
