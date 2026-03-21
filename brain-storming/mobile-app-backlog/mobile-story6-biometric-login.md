# Epic: Mobile Application (iOS & Android)
## Story: Biometric Login (FaceID / TouchID)

**As a** frequent app user,
**I want to** log in securely using my phone's facial or fingerprint recognition,
**So that** I don't have to manually type my email and password every time my session expires.

### Acceptance Criteria:
- *Given* my device supports biometrics, *when* I successfully log in once, *then* I am prompted to enable FaceID/TouchID for future logins.
- *When* my session expires and I open the app, *then* the native biometric prompt appears. Upon success, a securely stored token is used to seamlessly authenticate me with the backend.
