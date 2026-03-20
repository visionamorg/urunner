# Epic: Accessibility & Inclusivity
## Story: Turn-by-Turn Voice Navigation

**As a** visually impaired runner or a runner who avoids looking at their screen,
**I want to** hear voice prompts while following a discovered community route,
**So that** I safely know when to turn left or right without losing my pace or breaking visual focus on the road.

### Acceptance Criteria:
- *Given* I am actively running a downloaded route, *when* I approach an intersection within 30 meters, *then* the mobile app uses the native Text-to-Speech API to say "Turn right in 30 meters onto Main Street".
- *Given* I deviate from the route polyline by more than 50 meters, *then* the voice prompt says "Off route, recalculating" and provides corrective directions.
