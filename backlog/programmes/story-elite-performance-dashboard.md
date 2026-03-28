# Story: The Elite Performance Dashboard (Fitness vs. Fatigue)

### Status: DONE
Completed: 2026-03-28

## 🎯 Goal
Provide elite runners with a scientific view of their current training load, fitness, and readiness for race day.

## 👤 User Story
`As a Competitive Runner, I want to see a chart of my CTL (Fitness) and ATL (Fatigue) so that I can taper perfectly for my next race without overtraining.`

## 🛠️ Acceptance Criteria
- [x] Backend: Calculate **TSS (Training Stress Score)** for every activity based on `(Duration * Intensity)`.
- [x] Math Engine: Implement the **EWMA (Exponentially Weighted Moving Average)** for:
    - `CTL` (Chronic Training Load - 42 days).
    - `ATL` (Acute Training Load - 7 days).
    - `TSB` (Training Stress Balance).
- [x] UI: A synchronized line chart showing these three metrics over time.
- [x] Indicator: A "Training Zones" indicator (e.g., "Optimal Training", "Overreaching", "Recovery").

## 🚀 Powerful Addition: "The Taper Predictor"
Allow the user to "simulate" a rest week. If they stop running tomorrow, what will their **TSB (Form)** be on Sunday? This helps them decide exactly when to stop their hard training blocks before a race.

## 💡 Technical Strategy
1.  Store `daily_tss` in the `running_activities` table.
2.  Run a background job to re-calculate the `user_fitness_stats` table every time a new activity is synced from Strava/Garmin.
3.  Frontend: Use a multi-axis chart (e.g., ApexCharts) to overlay Fitness and Fatigue.
