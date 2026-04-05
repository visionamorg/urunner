# User Story: US-GC-003 - Team Readiness Dashboard (HRV, Sleep, Stress)

### Status: DONE

**As a** coach,
**I want** to see an overview of my team's morning readiness and recovery metrics from their Garmin devices,
**So that** I can adjust or cancel hard training sessions for an athlete showing signs of overtraining or illness.

## Acceptance Criteria
- [ ] Implement a "Health & Readiness" table with athletes as rows and metrics as columns:
    - **HRV (Status + Trend)**.
    - **Sleep Score**.
    - **Body Battery**.
    - **Resting Heart Rate (RHR)**.
- [ ] Implement a color-coded "Risk" system:
    - 🟢 Green: Ready for high-intensity work.
    - 🟡 Yellow: Low recovery.
    - 🔴 Red: Potential overtraining or sickness.
- [ ] Add a visual timeline of HRV for an athlete for the past 7 days.
- [ ] Provide a "Coach Note" field specifically for recovery suggestions to the athlete.

## Technical Considerations
- **API**: Fetching `dailyData` and `hrvData` from Garmin API (`garmin-health-api`).
- **Database**: New table `athlete_daily_readiness` to cache these metrics locally for fast dashboard rendering.
- **Privacy**: High sensitivity around "Stress" and "Weight" data. Athletes must explicitly opt-in to sharing health data with their coach.
