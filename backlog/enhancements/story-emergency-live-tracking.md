# Story: Emergency Live Tracking (Safety Buddy)

## 🎯 Goal
Provide peace of mind for runners (especially solo runners at night) by allowing a trusted friend to monitor their location in real-time until they return safely.

## 👤 User Story
`As a Solo Runner, I want to send a 'Safety Link' to my husband so that he can see my location during my 10km night run.`

## 🛠️ Acceptance Criteria
- [ ] UI: A "Start Live Tracking" toggle on the start run screen.
- [ ] Link Service: Generate a unique, time-limited URL that expires when the activity is finished.
- [ ] Permissions: The "Safety Buddy" does not need a RunHub account to view the tracking page.
- [ ] Alerting: If the runner stops moving for more than 5 minutes without reason, send a "No Activity Detected" notification to the Buddy.

## 🚀 Powerful Addition: "The Auto-SOS"
If the phone detects a sudden impact (accelerometer) or the runner's heart rate spikes and then drops flat, automatically trigger an SMS alert to the emergency contact with the exact coordinates.

## 💡 Technical Strategy
1. Frontend: Use `navigator.geolocation.watchPosition` to stream coordinates every 30 seconds.
2. Backend: A generic `LiveTrackingController` that handles public-facing high-read-frequency requests.
3. Use a lightweight In-Memory cache (Redis/Hazelcast) for storing current location of active runners instead of DB.
