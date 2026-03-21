# Epic: Mobile Application (iOS & Android)
## Story: Background GPS & Location Tracking

**As a** runner with my phone in my pocket,
**I want the** app to accurately track my GPS coordinates even when my screen is locked,
**So that** my distance, pace, and polyline route are recorded perfectly without battery drain.

### Acceptance Criteria:
- *Given* I start a run on the app, *when* I lock the phone screen, *then* the native OS background location services (CoreLocation on iOS, FusedLocationProvider on Android) continue to ping coordinates every 3-5 seconds.
- *Then* the app requests the correct "Always Allow" location permissions natively upon first launch.
