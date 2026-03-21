# Epic: Activity Canvas & Export Studio
## Story: HTML2Canvas Export Engine State Management

**As a** social media user,
**I want to** hit the export button and receive a high-quality 9:16 aspect ratio image downloaded directly to my device,
**So that** I can easily post it to my Instagram Stories.

### Acceptance Criteria:
- *Given* the user finishes configuring their template, *when* they press "Export to Image", *then* the system utilizes `html2canvas` (or a native `<canvas>` implementation) to capture the DOM element.
- *Then* the final file is generated strictly in a 9:16 (vertical mobile) aspect ratio with high resolution (e.g., 1080x1920) and saved as a PNG or JPG.
- *Then* the state correctly populates all the HTML variables with the JSON run data *before* the canvas capture fires, ensuring no empty `{distance}` placeholder brackets are rendered.

### Status: ✅ Done
**Implemented:** html2canvas-based export engine captures the 1080x1920 canvas DOM element and triggers a PNG download. All activity data (distance, pace, duration, date, username, location) is populated from the API response before canvas capture. Export button in the studio header with loading state.
