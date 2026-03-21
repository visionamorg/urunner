# Epic: Activity Canvas & Export Studio
## Story: Custom Typography Uploads

**As a** brand or running club manager,
**I want to** upload my own custom `.ttf` or `.woff2` font files into the builder,
**So that** my exports perfectly match my club's exact branding guidelines.

### Acceptance Criteria:
- *Given* I am editing a template's text, *when* I open the font dropdown, *then* there is an "Upload Custom Font" button.
- *When* I provide a valid web-font file, *then* it is loaded via the CSS Font Loading API and immediately applied to the canvas text.
