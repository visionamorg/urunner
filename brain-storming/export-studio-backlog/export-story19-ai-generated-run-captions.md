# Epic: Activity Canvas & Export Studio
## Story: AI-Generated Social Media Captions

**As a** user exhausted after a long run,
**I want an** AI to write my Instagram/Strava caption for me,
**So that** I don't have to spend 10 minutes thinking of something clever to say alongside my exported graphic.

### Acceptance Criteria:
- *Given* I am on the final export screen, *when* the image finishes rendering, *then* an LLM analyzes my run's distance, pace, weather, and location.
- *Then* the UI presents 3 distinct text options (e.g., Option 1: "Funny/Self-Deprecating", Option 2: "Inspirational", Option 3: "Just the Facts") complete with relevant emojis and hashtags, ready for one-tap copying.

### Status: ✅ Done
**Implemented:** Caption generator analyzes the activity's distance, pace, duration, and location to produce 3 distinct caption styles: Funny/Self-Deprecating, Inspirational, and Just the Facts. Each includes emojis and hashtags. One-tap copy button using Clipboard API. Regenerate button for fresh options. Context-aware (long runs, fast pace, etc.).
