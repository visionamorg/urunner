# Story: Privacy 'Stealth Zones' (Safety & Security)

## 🎯 Goal
Protect the user's privacy and physical safety by allowing them to hide sensitive locations (Home, Work, School) on public activity maps.

## 👤 User Story
`As a Runner, I want to define a 500m 'Stealth Zone' around my home so that my public activity map doesn't show exactly where I live.`

## 🛠️ Acceptance Criteria
- [ ] Config: Allow users to define multiple "Stealth Zones" by address + radius.
- [ ] Processing: When an activity is synced, the **GPS Polyline** must be "Trunkated" or "Puzzled" within these zones.
- [ ] Display: The public map starts outside the zone, but the total distance/time stats remain accurate.
- [ ] Privacy: Privacy zones are NEVER shared with third-party APIs (unless explicitly opted-in).

## 🚀 Powerful Addition: "The Dynamic Privacy Guard"
Automatically detect "Frequent Start/End points" and suggest them as potential Stealth Zones to the user if they haven't set any yet.

## 💡 Technical Strategy
1. Backend: Implement a geometric intersection check using **PostGIS** if available, or a simple distance-based filter on the polyline points.
2. Ensure the "Activity Feed" component in Angular masks the map snippet before it's rendered for non-friends.
3. Update the `RunningActivity` model to track `masked_geometry`.
