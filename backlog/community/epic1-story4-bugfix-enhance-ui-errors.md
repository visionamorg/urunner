# Epic: Community MVP & Enhancements
## Story: BugFix & Enhancement - UI Polish & Error Handling

**As a** Community Admin
**I want** robust error handling and polished UI across all Community features
**So that** users don't encounter confusing errors and my community looks professional.

### Description
Once the functional blocks are complete, we need to address any lingering bugs and enhance the frontend before full MVP1 launch.
Specifically, making sure error messages are graceful and UI components scale correctly on smaller devices.

### Acceptance Criteria
- [ ] Add loading skeletons or spinners while data loads in all 6 main tabs.
- [ ] Implement friendly error toasts/alerts on backend failures (e.g. "Failed to kick member" instead of an unhandled exception).
- [ ] Fix any layout breaking issues when viewing Community details on mobile devices (e.g. Chat input area not sticky or hidden).
- [ ] Enhance Calendar UI color scheme to better match the RunHub unified styling tokens (use orange gradient for active dates, dark surface context).
- [ ] If chat connection fails midway, auto-reconnect or show an "offline" status bar.

### Technical Notes for Claude
- Use core `ToastService` or similar pattern for error handling.
- Review CSS Grid/Flexbox in `community-calendar.component.scss` and `community-rooms.component.scss` to make them fully responsive.
