# User Story: US-GC-005 - Coach Performance Analysis (Team CTL/ATL)

**As a** coach,
**I want** to see an overlaid chart of my team's Fitness (CTL) and Fatigue (ATL) over any date range,
**So that** I can detect which athlete is peaking at the right time and who needs an extra recovery week.

## Acceptance Criteria
- [ ] Implement a "Performance Hub" dashboard for coaches.
- [ ] Visualize the **CTL / ATL / TSB** (Fitness vs. Fatigue) for every athlete in the coach's team.
- [ ] Add a "Compare Athletes" feature where the coach can overlay 2-3 athletes' training load trends.
- [ ] Include Garmin's proprietary "Training Status" (e.g., Peaking, Productive, Unproductive) for each athlete in the team summary table.
- [ ] Provide filters for viewing by "Training Program" or "Elite Group".

## Technical Considerations
- **Math Engine**: The data for `CTL` and `ATL` should already be calculated (per US-G004/US-G005/etc.), so the coach dashboard is a high-level view of these pre-calculated stats.
- **Charts**: Use a flexible multi-line chart library (e.g., Chart.js, ApexCharts) to handle high-density team data without performance lag.
- **Caching**: Pre-render the daily team fitness averages every night to allow for fast loading of the coach's dashboard.
