# Epic: Activity Canvas & Export Studio
## Story: Template Selection UI Carousel

**As a** runner wanting to share my activity,
**I want to** browse a visual carousel of different export templates,
**So that** I can preview how my data looks on different layouts before exporting.

### Acceptance Criteria:
- *Given* I click "Share Activity", *when* the Export Studio opens, *then* a bottom-sheet (mobile) or sidebar (desktop) displays at least 4 predefined template thumbnails.
- *When* I tap a thumbnail, *then* the main canvas immediately updates to reflect the selected layout using my actual run data.

### Status: ✅ Done
**Implemented:** Export Studio component with 4 template thumbnails in a grid carousel (sidebar on desktop, stacked on mobile). Clicking a template instantly updates the 1080x1920 canvas preview with the user's actual run data. Activity picker allows switching between activities. Route: `/export-studio`, accessible from sidebar nav and "Share" button on each activity card.
