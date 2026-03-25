# Epic: Community Management & Growth
## Story: Feature - Generate Weekly Agenda Image for Social Media
### Status: DONE (2026-03-25)

**As a** Community Admin
**I want to** automatically generate an "Agenda of the Week" image based on my community's scheduled events and programmes
**So that** I can easily share our weekly schedule on Instagram and other social media without needing external design tools like Canva or Photoshop.

### Description
Many running communities post a weekly agenda on their Instagram stories (e.g., "AGENDA OF THE WEEK: FROM MARCH 23 TO 29"). Instead of typing this out manually every week, admins can click an "Export Weekly Agenda" button. The system will gather the upcoming week's events from the calendar, overlay them onto a default or custom background image, and produce a downloadable image perfectly formatted for an Instagram Story (9:16 ratio).

### Acceptance Criteria
- [x] On the Community Calendar or Events tab, admins see an "Export Agenda Image" button.
- [x] Clicking the button opens a modal allowing the admin to:
    - Select the Start and End Date for the week (defaults to upcoming Monday to Sunday).
    - Upload or select a background image (defaults to the community's cover photo).
    - Toggle which events/sessions to include in the visual list.
- [x] The feature generates a high-quality (PNG/JPG) image incorporating:
    - The word "AGENDA" in a bold, stylized font (e.g., serif/classic like the reference).
    - The date range (e.g., "OF THE WEEK FROM MARCH 23 TO 29").
    - A stylized list of the events (Date + Title + Time + Location).
    - The community's Instagram handle and hashtags (e.g., `@runnerscasablanca #URBANRUNNERSCASABLANCA`).
- [x] The generated image is instantly downloaded to the admin's device, ready to be posted.

### Technical Notes for Claude
- **Frontend-First Approach (Recommended):** Use a library like `html2canvas` or `dom-to-image-more`. 
    - Create a hidden (or visually scaled-down) Angular/React component that perfectly mimics the 1080x1920 (9:16) Instagram story layout.
    - Apply CSS filters (like greyscale or dark overlays) over the background image to ensure white text is highly readable, just like the reference photo.
    - When the user clicks "Download", use the library to screenshot this specific DOM node and trigger a browser download `<a>` tag with the base64 data URI.
- **Alternative Backend Approach:** If frontend generation is buggy across browsers, create a Node/Spring tool using `Puppeteer` (to screenshot a hidden URL) or a native image manipulation library to draw text on an image buffer and return the blob.
- Define a specialized CSS class for the generated text that mimics modern, sleek typography (e.g., "Playfair Display" for AGENDA, and a clean sans-serif for the dates and events list).
