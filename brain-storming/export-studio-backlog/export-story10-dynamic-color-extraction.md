# Epic: Activity Canvas & Export Studio
## Story: Dynamic Color Palette Extraction

**As an** aesthetically-focused user,
**I want the** template fonts and geometric shapes to tint automatically based on the dominant colors of my uploaded background photo,
**So that** the final graphic looks professionally color-graded and cohesive.

### Acceptance Criteria:
- *Given* I upload a background photo, *then* a client-side color extraction algorithm (e.g., ColorThief) immediately identifies the dominant primary and secondary colors.
- *Then* the state updates the UI so that the template's accent colors (like the polyline trail or the large background numbers) shift to match the photo's extracted palette.
