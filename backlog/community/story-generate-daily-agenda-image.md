# Epic: Community Management & Growth
## Story: Feature - Generate Daily Event Image for Social Media
### Status: DONE (2026-03-25)

**As a** Community Admin
**I want to** automatically generate a "Daily Agenda" or "Next Session" image for a specific upcoming event
**So that** I can easily share tomorrow's run details on Instagram Stories without needing to manually design it.

### Description
Communities often post reminders the day before a specific running session. Admins should be able to select a single upcoming event and export a beautifully formatted vertical image (1080x1920) highlighting the exact details for that day (e.g., "TOMORROW'S RUN", the pace groups, meeting spot, and time). 

### Acceptance Criteria
- [x] On the Event Detail page or Community Calendar, admins see an "Export Daily Image" button next to a specific event.
- [x] Clicking the button opens a modal allowing the admin to:
    - Verify or edit the short details (Meeting point, Time, Distance, Pace Groups).
    - Upload or select a background image showcasing the route or community (defaults to the event's cover photo).
- [x] The feature generates a high-quality (PNG/JPG 9:16 aspect ratio) image incorporating:
    - Bold, stylized header text like "NEXT SESSION" or "TOMORROW'S AGENDA".
    - The date and specific time of the event prominently displayed.
    - Event specifics: Location/Meeting Point, Distance (e.g., "10K Loop"), and available Pace Groups (e.g., "5:00/km | 6:00/km").
    - The community's Instagram handle and hashtags at the bottom.
- [x] The generated image is instantly downloaded to the admin's device, ready to be posted.

### Technical Notes for Claude
- Similar to the Weekly Agenda export, use `html2canvas` or a comparable library to generate this client-side.
- Create a specific layout component designed for *single-event focus*. The typography should make the Time and Location the largest elements after the main header.
- Apply a dark gradient overlay via CSS over the background image to ensure the white text pops perfectly.
- Expose an option to overlay community custom tags (like "Pacer Available" or "Beginner Friendly") into stylistic badges on the generated image.
