# Epic: Community Growth & Engagement
## Story: Creative - Smart Event Galleries & Google Drive Sync

### Status: DONE

**As a** Community Member or Admin
**I want** a smart way to sync and view photos from Google Drive for specific events
**So that** I don't have to scroll through a single feed post, and I can easily find photos of myself using AI recognition.

### Description
The current Drive Sync feature pulls photos from a folder and dumps them into a single Feed Post. This is a very basic MVP approach. To make the app feel premium and highly engaging, we need to upgrade this to **"Smart Event Galleries"**. 

Instead of a generic feed post, photos from a Drive folder should be linked directly to an "Event" to create a permanent, dedicated Event Gallery. Furthermore, we can use AI to auto-tag runners based on their Bib numbers so they get instant notifications when a photo of them is uploaded. Finally, members should be able to contribute their own photos to the shared Drive folder directly from the app.

### Acceptance Criteria
- [ ] Admins can link a Google Drive subfolder specifically to an existing Event (not just to the whole community).
- [ ] A new "Gallery" or "Memories" tab exists on the Event Details page showing all synced photos in a modern masonry grid layout.
- [ ] (Creative OCR) Implementation of a Cloud Vision or Claude AI prompt to scan the synced Google Drive images for bib numbers.
- [ ] If a bib number matches a registered runner, the runner is automatically tagged and receives an in-app notification: "Spotted! You have new photos from [Event Name]".
- [ ] Members can click an "Upload your photos" button which securely uploads files directly to the Event's Google Drive folder (via a backend upload endpoint), allowing community contribution to the gallery.

### Technical Notes for Claude
- Review `GoogleDriveService.java` and `CommunityService.java` (`syncDrivePhotos` method). 
- Instead of calling `feedService.createPhotoPost`, create a new `EventGallery` entity/model (or extend `Event`) to store the Drive image links alongside the event.
- Explore the Claude Vision API or add a simulated `BibRecognitionService` that mocks identifying runners in images for the MVP demonstration.
- The UI should feature a modern masonry image grid in `frontend/src/app/features/communities/` (add a gallery view to the events tab). Ensure the frontend components are dynamic and visually appealing.
