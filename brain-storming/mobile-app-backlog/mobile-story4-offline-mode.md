# Epic: Mobile Application (iOS & Android)
## Story: Offline Mode & Training Plan Caching

**As a** trail runner who often loses cell service in the mountains,
**I want my** daily training plan and route to be cached locally on my phone,
**So that** I can still follow my workout instructions and navigate the map without an internet connection.

### Acceptance Criteria:
- *Given* I open the app on Wi-Fi, *when* my Training Plan is loaded, *then* the mobile app caches the current week's schedule to AsyncStorage or SQLite.
- *Given* I lose network connectivity, *when* I open the app, *then* the UI indicates "Offline Mode Active" but still allows me to view my downloaded route and start logging an activity locally (to sync later).
