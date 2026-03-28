# Story: Streak Safeguard (Motivation & Consistency)

## 🎯 Goal
Protect the user's "Running Streak" from being broken by unforeseen circumstances (illness, travel, injury), maintaining long-term psychological motivation.

## 👤 User Story
`As a Runner, I want to use a 'Streak Freeze' when I am sick so that I don't lose my 100-day running progress.`

## 🛠️ Acceptance Criteria
- [ ] Model: Add `streak_count` and `last_run_date` to the `users` table.
- [ ] Logic: A "Running Day" is any day with an activity > 1km.
- [ ] Feature: "Streak Freeze" items earned every 30 days of consistent running.
- [ ] Notification: Alert the user at 8:00 PM if they haven't run yet and their streak is at risk.

## 🚀 Powerful Addition: "The Community Recovery Window"
If a runner is injured (e.g., "Injury" tag on activity), the streak automatically pauses until their next activity, instead of resetting to zero.

## 💡 Technical Strategy
1. Background Job: Calculate daily streaks at midnight for all active users.
2. Use a PostgreSQL `JSONB` column to store "Active Boosts/Freezes".
3. UI: A "Flame" icon on the dashboard that glows brighter as the streak increases.
