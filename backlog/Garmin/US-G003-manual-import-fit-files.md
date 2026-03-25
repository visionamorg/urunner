# User Story: US-G003 - Manual .FIT File Import

**As a** runner using an unsupported device or experiencing sync issues,
**I want** to manually upload my `.FIT` activity files,
**So that** I don't lose my training data on the Runhub platform.

## Acceptance Criteria
- [ ] Add a "Manual Upload" button to the Activities dashboard.
- [ ] Support `.fit` file parsing in the backend.
- [ ] Extract core telemetry (GPS, HR, Power, Cadence) from the uploaded file.
- [ ] Ensure manual uploads are tagged with a "Manual" source to distinguish them from API-synced activities.
- [ ] Prevent uploading the same file twice (hash check or ID check).

## Technical Considerations
- **Backend**: Integrate a Java library for FIT file parsing (e.g., `garmin-fit-sdk`).
- **Frontend**: Implement a drag-and-drop file uploader in Angular.
