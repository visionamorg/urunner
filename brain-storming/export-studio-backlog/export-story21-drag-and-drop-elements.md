# Epic: Activity Canvas & Export Studio
## Story: Free-Form Drag and Drop Builder

**As an** advanced user who feels constrained by rigid templates,
**I want to** tap, hold, and drag individual stat blocks (Distance, Pace, Polyline) anywhere on the canvas,
**So that** I can create a 100% custom layout that perfectly frames the subject in my background photo.

### Acceptance Criteria:
- *Given* I am in the Export Studio builder, *when* I toggle "Free-Form Mode", *then* the template containers unlock.
- *When* I drag the "Distance" text block with my finger/mouse, *then* it moves freely across the X/Y axes of the 1080x1920 canvas.
- *Then* the final `html2canvas` export perfectly captures the exact custom X/Y coordinates of all moved elements.
