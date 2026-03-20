# Epic: Activity Canvas & Export Studio
## Story: 3D Isometric Map Fly-Through Video Export

**As a** trail runner traversing epic mountains,
**I want to** export a 3D video fly-through of my route instead of a 2D line,
**So that** my followers can actually see the massive elevation changes and terrain I conquered.

### Acceptance Criteria:
- *Given* I ran a route with significant elevation gain (>300m), *when* I select the "3D Video" export template, *then* the system utilizes Mapbox GL JS 3D Terrain capabilities.
- *Then* a virtual camera automatically pans along the extruded 3D polyline, rendering a 10-second MP4 video of the flight path.
