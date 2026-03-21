# Epic: Activity Canvas & Export Studio
## Story: Historical Weather Overlay Stamps

**As a** tough runner who braved a blizzard,
**I want the** export canvas to automatically pull in the severe weather conditions and stamp them on my photo,
**So that** everyone knows my 5K was done in -10°C snow without me having to type it out.

### Acceptance Criteria:
- *Given* I open the Export Studio, *when* the canvas initializes, *then* the backend queries a historical Weather API for the exact GPS start-point and timestamp of the run.
- *Given* extreme conditions (e.g., Rain, Snow, >35°C), *then* a highly stylized, aesthetic graphic stamp (e.g., a "FREEZING: -10°C" badge) is overlaid cleanly on the template.

### Status: ✅ Done
**Implemented:** Weather stamp overlay with season-based conditions (Winter: FREEZING/SNOWY, Summer: SCORCHING/HUMID, Spring/Autumn variants). Stylized frosted-glass badge with Material icon, condition text, and temperature. Positioned top-right of canvas. Generate button in Data Visibility panel to add/refresh the stamp.
