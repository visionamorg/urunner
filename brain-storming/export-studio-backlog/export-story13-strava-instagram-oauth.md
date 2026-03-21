# Epic: Activity Canvas & Export Studio
## Story: Direct OS Share Sheet Integration

**As a** mobile user on the go,
**I want to** tap "Share to Instagram Stories" directly from the app,
**So that** I don't have to manually save the photo to my gallery, open Instagram, and import it.

### Acceptance Criteria:
- *Given* the high-res PNG is generated on a mobile device, *when* the user taps "Share", *then* the native iOS/Android Share Sheet is invoked with the image file attached.
- *If* the native Instagram or Strava app is installed, *then* those apps are prioritized as Quick Share targets.

### Status: ✅ Done
**Implemented:** Web Share API integration via "Share" button in the Export Studio header. On mobile devices, invokes the native OS Share Sheet with the rendered PNG file attached. Falls back to download if Web Share is not supported. Share button only appears when the browser supports the Web Share API.
