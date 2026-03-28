# Story: Smart Pacing Coach (Weather-Adaptive)

## 🎯 Goal
Provide runners with realistic pace targets by automatically adjusting their training goals for environmental conditions (Heat, Humidity, Altitude).

## 👤 User Story
`As a Runner, I want my 'Sub-4 Marathon' session to adjust its target pace from 5:30 to 5:45 if the temperature is above 30°C on race day.`

## 🛠️ Acceptance Criteria
- [ ] Integration: Connect to a **Weather API** (e.g., OpenWeatherMap) to fetch real-time conditions for the user's location.
- [ ] Science: Implement a "Heat Adjustment Algorithm" based on the **Dew Point** and **Temperature** (similar to the Jack Daniels / VDOT logic).
- [ ] UI: Show a "Weather Impact" icon next to the session goal (e.g., "🔥 +15s/km due to heat").
- [ ] Logic: Update the `program_sessions` target pace in real-time when the runner starts their session.

## 🚀 Powerful Addition: "The Acclimatization Tracker"
Track how many runs a user has done in the heat. If they've been training in Casablanca's summer for 2 weeks, reduce the "Heat Penalty" as their body has adapted to the conditions.

## 💡 Technical Strategy
1. Use the browser's `Geolocation API` to find user's current city.
2. Fetch current weather data before starting the "Live Session Guide".
3. Apply a penalty multiplier to the `target_pace`: `new_pace = base_pace * (1 + heat_penalty)`.
4. Log the weather condition in the `running_activities` table for future performance analysis.
