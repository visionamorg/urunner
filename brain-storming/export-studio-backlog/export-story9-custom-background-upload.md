# Epic: Activity Canvas & Export Studio
## Story: Custom Background Photo Upload & Filters

**As a** runner with my own race photography,
**I want to** upload a local photo from my camera roll to use as the background for the Activity Canvas,
**So that** my exported stat graphic is personalized to my specific run experience.

### Acceptance Criteria:
- *Given* I am in the Export Studio, *when* I tap "Change Background", *then* I can select an image from my device's native file picker.
- *When* the image loads, *then* a slider allows me to adjust the opacity/blur of the image so that the text and templates remain highly contrastive and readable over complex photos.

### Status: ✅ Done
**Implemented:** Background photo upload via native file picker, displayed as full-cover background on the canvas. Opacity slider (10-100%) and blur slider (0-20px) let users adjust readability. Remove button to clear the background. Preview thumbnail in the controls panel.
