# Epic: Privacy & Trust
## Story: Privacy Zones (Hide Start/End Locations)

**As a** privacy-conscious user,
**I want to** define a 500m "Privacy Zone" around my home address,
**So that** my public runs automatically obscure where I actually started and finished my run.

### Acceptance Criteria:
- *Given* I define a coordinate point as my "Home Base", *when* I upload a run that starts/ends within 500m of that point, *then* the Polyline data is cropped by the backend before being stored in the database.
- *When* other users view my activity map, *then* the start and end markers are placed exactly at the 500m boundary line, hiding my actual residence.
