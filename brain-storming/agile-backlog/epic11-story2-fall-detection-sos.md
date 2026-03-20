# Epic: Safety & Emergency Features
## Story: Fall Detection SOS via Wearable

**As a** trail runner traversing difficult terrain,
**I want my** smartwatch to detect sudden falls and automatically trigger an SOS protocol,
**So that** emergency contacts are notified immediately if I am incapacitated.

### Acceptance Criteria:
- *Given* a user has their Apple/Garmin watch connected, *when* internal accelerometer data indicates a severe fall, *then* a 30-second countdown prompt appears on the watch.
- *Given* the user does not dismiss the countdown, *then* the backend automatically triggers an SMS to their pre-defined Emergency Contacts containing their last known GPS coordinates and a distress message.
