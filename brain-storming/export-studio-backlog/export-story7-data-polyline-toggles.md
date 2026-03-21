# Epic: Activity Canvas & Export Studio
## Story: Advanced Data & Polyline Toggles

**As a** privacy-conscious or minimalist user,
**I want to** selectively hide specific data points like my raw pace or heart rate from the graphic,
**So that** I only share the metrics I feel comfortable putting on social media.

### Acceptance Criteria:
- *Given* I am configuring my export canvas, *then* a panel provides toggle switches for "Heart Rate", "Spline Map (Polyline)", and "Pace".
- *When* I toggle a switch off, *then* the corresponding data element smoothly disappears from the HTML layout, and the remaining elements re-center or adjust automatically.

### Status: ✅ Done
**Implemented:** Data Visibility panel with toggle switches for Distance, Pace, Duration, and Location. Toggling off a switch removes the corresponding data from all 4 templates, and remaining elements adjust automatically via flexbox/grid reflow. Custom toggle switch styling with smooth transition.
