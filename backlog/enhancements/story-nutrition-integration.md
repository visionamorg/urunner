# Story: Nutrition & Hydration Integration

## 🎯 Goal
Help runners optimize their performance and recovery by tracking fueling strategies during and after their training sessions.

## 👤 User Story
`As a Marathon Runner, I want to log how many 'Gels' I took during my long run so that I can refine my fueling strategy for race day.`

## 🛠️ Acceptance Criteria
- [ ] UI: Add a "Fueling" section to the post-activity summary.
- [ ] Items: Support tracking Water, Electrolytes, Gels, and Caffeine.
- [ ] Analysis: Correlate "Pace Drop" at the end of a run with "Low Fueling" records.
- [ ] Program Link: Suggest hydration amounts based on current local weather (OpenWeatherMap) and duration.

## 🚀 Powerful Addition: "The Sweat Rate Calculator"
Ask the user for their weight before and after a hot summer run in Casablanca. Calculate their **Sweat Rate (L/hr)** and use this to give personalized hydration advice for future sessions.

## 💡 Technical Strategy
1. Backend: Update `running_activities` with a `nutrition_data` JSONB field.
2. Frontend: A modular UI that only shows "Fueling" for runs > 60 minutes.
3. Integrate with the AI Coach to warn about "Bonking Risk" based on low fueling patterns.
