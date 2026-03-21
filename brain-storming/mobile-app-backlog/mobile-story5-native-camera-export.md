# Epic: Mobile Application (iOS & Android)
## Story: Native Camera & Export Share Sheet

**As a** user who just finished a sweaty run,
**I want to** open the camera directly within the app, snap a selfie, and instantly push the Export Studio graphic to my Instagram app,
**So that** sharing my run to social media feels frictionless and uses native device features.

### Acceptance Criteria:
- *Given* I finish a run and tap "Add Photo", *when* I select "Camera", *then* the native device camera module opens in-app.
- *When* I finalize my Activity Canvas layout and hit "Share", *then* the app invokes the `expo-sharing` native Share Dialog, passing the final image directly to other installed apps (WhatsApp, Instagram, Twitter).
