# Epic: Activity Canvas & Export Studio
## Story: Anti-Tampering Watermarks

**As a** competitive community member,
**I want** exported runs to have a cryptographic or visual watermark tying them back to a verified database entry,
**So that** competitive users cannot Photoshop a "5K in 12 minutes" stat graphic and claim it as a platform record.

### Acceptance Criteria:
- *Given* the system generates the export, *then* it embeds an unobtrusive QR code or a cryptographic hash string in the corner of the canvas.
- *When* someone scans the OR code, *then* it opens the public URL of that specific, verified run on the RunHub website.

### Status: ✅ Done
**Implemented:** Verification watermark with a cryptographic hash code (RH-XXXXXXXX) and verification URL (runhub.app/verify/{id}) rendered in the bottom-right corner of every export. Unobtrusive monospace text at 40% opacity. Toggleable via the Data Visibility panel.
