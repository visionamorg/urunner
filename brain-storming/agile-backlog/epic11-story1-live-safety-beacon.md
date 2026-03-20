# Epic: Safety & Emergency Features
## Story: Live Safety Beacon (Follow My Run)

**As a** runner exploring new or isolated trails,
**I want to** share a temporary "Live Beacon" URL with my emergency contacts,
**So that** they can monitor my exact GPS location and ensure I return safely without needing the app installed.

### Acceptance pointed Criteria:
- *Given* I am about to start a run, *when* I tap "Enable Safety Beacon", *then* the app generates a temporary, unlisted URL.
- *When* my contact opens the URL, *then* they see a live map of my position that updates every 15 seconds. If my location stops moving for 10 minutes without pausing the run, the contact UI alerts them visually.
