# US-016 — Events Map & Route Integration

**Status: DONE** — Completed 2026-04-10


**Status:** [ ] Pending
**Priority:** 🟢 Low

---

## Problem

Events have a title, date, and description but no location or route data. Runners can't see where an event is being held, what the route looks like, or get directions. This is a basic feature for a running platform that organizes races and group runs.

---

## Story

As a **runner**, I want to see where an event takes place on a map and view the planned route so I can prepare for the race and find the start line.

---

## Acceptance Criteria

### Event Detail — Location & Map
- [x] Event detail page (`/events/:id`) shows a map with the start location marker
- [x] Map uses OpenStreetMap via Leaflet.js (free, no API key for basic use)
- [x] Clicking the marker opens directions in Google Maps (`https://maps.google.com?q=lat,lng`)
- [x] If no GPS data: shows a static placeholder with the location name text

### Event Creation — Location Fields
- [x] Event creation form (admin) adds:
  - Location name text field (e.g., "Corniche Ain Diab, Casablanca")
  - Latitude / Longitude fields (or a map picker)
  - Route GPX file upload (optional)
- [x] `Event` model: add `locationName`, `latitude`, `longitude`, `routeGpxUrl` columns

### Route Display
- [x] If a GPX file was uploaded, draw the route as a polyline on the map
- [x] Route stats shown beside map: Total distance, Elevation gain, Estimated time at avg pace
- [x] GPX file stored in `/uploads/routes/` (same upload volume as US-010)

### Events List — Location Badge
- [x] Event card on the list shows location name as a small badge with a pin icon
- [x] "Online" badge if no GPS location set

### Upcoming Events Widget on Dashboard
- [x] Dashboard upcoming events widget shows location name under each event title

---

## Technical Notes

### Backend
- `Event` model: add columns (see migration below)
- `EventController`: update `createEvent` and `updateEvent` to accept location fields + GPX upload
- Store GPX file via `StorageService` (same as US-010)
- `EventDto`: include `locationName`, `latitude`, `longitude`, `routeGpxUrl`

### Frontend
- Install Leaflet: `npm install leaflet @types/leaflet`
- `MapComponent` — reusable `shared/components/map/map.component.ts`:
  - Input: `lat`, `lng`, `zoom`, optional GPX polyline data
  - Uses `ngAfterViewInit` to init Leaflet map
- Event detail: add map section below existing details
- GPX parsing: use `leaflet-gpx` plugin or parse manually (simple XML)

---

## Database Migration

```sql
ALTER TABLE events ADD COLUMN location_name VARCHAR(255);
ALTER TABLE events ADD COLUMN latitude      DECIMAL(10,7);
ALTER TABLE events ADD COLUMN longitude     DECIMAL(10,7);
ALTER TABLE events ADD COLUMN route_gpx_url VARCHAR(500);
```
