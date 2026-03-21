# Epic: Activity Canvas & Export Studio
## Story: UI Enhancements for "Clear Info Card" Template

**As a** user exporting my run with the Clear Info Card layout,
**I want the** typography spacing and profile images to look highly polished and professional,
**So that** the final export doesn't look like a buggy or unfinished template.

### UI Bugs Identified from Render:
1. **Logo Badge Spacing:** The gap between the bold "URC" acronym and the "URBAIN RUNNING CLUB" subtitle in the top-right pill is too tight and visually uncomfortable.
2. **Missing Profile Picture:** The avatar next to the username is currently rendering a flat red fallback circle instead of the user's actual profile photo.
3. **Illegible Activity Title:** The "MORNING LONG RUN" text at the absolute bottom of the frame is blending invisibly into the background photo.

### Acceptance Criteria (Fixes):
- *Given* the top-right URC badge, *when* rendered, *then* apply a `line-height` adjustment or a `margin-bottom` of `4px` to the "URC" text so it breathes properly above the subtitle.
- *Given* the user profile section in the frosted glass card, *then* the system must successfully pass the user's `profileImageUrl` props into the `<img>` tag. The image must have `border-radius: 50%` and `object-fit: cover` to ensure it acts as a perfect circular avatar instead of a red div.
- *Given* the activity title (e.g., "MORNING LONG RUN"), *then* increase the opacity of the text to at least `0.8` (or 80%) AND add a heavy CSS `text-shadow: 0px 2px 10px rgba(0,0,0,0.8)` so it remains readable regardless of how bright or dark the background photo is.
