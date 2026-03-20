# Epic: Event & Race Management
## Story: Live Timing Chip Integration

**As an** Event Organizer for physical races,
**I want to** integrate physical RFID timing chip data directly into the RunHub leaderboard,
**So that** participants get verified, official race times published natively in the app immediately after crossing the finish line.

### Acceptance Criteria:
- *Given* I am configuring a physical event, *then* I can input an API endpoint or Webhook from a hardware provider (e.g., MyLaps).
- *When* a runner crosses the finish mat, *then* the hardware Webhook pushes the exact timestamp to my Spring Boot backend, updating the official ranking board instantly.
