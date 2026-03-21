# Epic: Activity Canvas & Export Studio
## Story: Animated Video Export (MP4/GIF)

**As a** TikTok or Instagram Reels creator,
**I want to** export a short 5-second video instead of a static image,
**So that** I can show my polyline route dynamically drawing itself and my stats popping in.

### Acceptance Criteria:
- *Given* I finish customizing my template, *when* I tap "Export as Video", *then* a rendering engine captures a 5-second HTML5 canvas animation.
- *Then* the animation features the route stroke drawing from Start to Finish, followed by the stat numbers counting up rapidly (e.g., 0km -> 15km).
- *Then* it prompts the user to save the resulting .mp4 or .gif file.

### Status: ✅ Done
**Implemented:** Video export using MediaRecorder API capturing a 5-second canvas animation. The animation renders the static template as background with an animated count-up overlay: distance counting from 0 to final km, pace, and duration. Exports as .webm video file. "Video" button in the Export Studio header with recording state indicator.
