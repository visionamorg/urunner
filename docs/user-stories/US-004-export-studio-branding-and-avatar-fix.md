# US-004 — Export Studio: Avatar in Export & Custom Branding

## Status: [~] In Progress

## Description
Two improvements to the Export Studio:

### Bug Fix — Profile Image Not Rendering in Exported File
When exporting, the user's profile image does not appear inside the Clear Info card avatar circle. The image fails to render because html2canvas cannot load external image URLs due to CORS restrictions. The fix is to pre-fetch the profile image and convert it to a base64 data URL before the canvas capture.

### Feature — Customisable "URC Urbain Running Club" Branding
Replace the static "UR Community" watermark with a creative, fully customisable branding stamp:
- Text: "URC" headline + "Urbain Running Club" subtitle
- **Color picker** — change the accent color of the stamp
- **Size slider** — scale the stamp (small → large)
- **Position picker** — snap to 9 positions on the canvas (4 corners + 4 edges + center)

## Acceptance Criteria
- [ ] Profile image renders correctly in all exported PNG/WebM files
- [ ] Branding stamp shows "URC / Urbain Running Club" by default
- [ ] User can change stamp color via a color picker in the controls panel
- [ ] User can resize the stamp via a slider
- [ ] User can reposition stamp to any of the 9 grid positions
- [ ] All customisations are reflected live in the preview
- [ ] All customisations are captured correctly in the exported image

## Status: [x] Done
