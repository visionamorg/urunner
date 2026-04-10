# US-010 — Direct Photo Upload in Posts

**Status:** [ ] Pending
**Priority:** 🔴 High

---

## Problem

Users can only create `TEXT` posts in the global feed, or `PHOTO_ALBUM` posts synced from Google Drive by community admins. There is no way for a regular user to upload a photo directly from their device. This is a fundamental social media feature that is completely missing.

---

## Story

As a **runner**, I want to upload photos directly from my device when creating a feed post, so I can share race photos, training moments, and community highlights without needing a Google Drive setup.

---

## Acceptance Criteria

### Post Composer (Global Feed)
- [ ] "Create post" area at the top of the feed shows a photo attachment icon
- [ ] Clicking opens a file picker (accept `image/*`, max 5 files)
- [ ] Selected images show as thumbnails in the composer before posting
- [ ] Each thumbnail has an X to remove it
- [ ] On submit, images are uploaded then post is created — loading state shown

### Post Composer (Community Feed)
- [ ] Same photo upload available when creating a post in a community feed

### Viewing Photo Posts
- [ ] Photo posts render using the existing Facebook-style photo grid (1/2/3/4+ layout already built)
- [ ] Photos are tap/click-able to open a lightbox (fullscreen view)
- [ ] Lightbox: swipe/arrow navigation between photos, X to close

### Backend — File Upload
- [ ] `POST /api/uploads/images` — multipart file upload, returns list of URLs
- [ ] Files stored either on local filesystem (Docker volume) or cloud (first: local `/uploads/`, served via Nginx)
- [ ] Max file size: 10MB per file, max 5 files per request
- [ ] Validate MIME type server-side (only images)
- [ ] `FeedService.createPhotoPost()` already accepts `photoUrls` — reuse it
- [ ] `CommunityService.createPost()` for community photo posts — same approach

### Security
- [ ] Upload endpoint requires authentication
- [ ] Sanitize filenames (UUID-based rename on server side)

---

## Technical Notes

### Backend
- New `UploadController` at `/api/uploads/images`
- `StorageService`: saves file to `/app/uploads/` inside Docker container, returns public URL `/uploads/{uuid}.jpg`
- `docker-compose.yml`: add volume `./uploads:/app/uploads` and Nginx location `/uploads/ -> /app/uploads/`
- Alternatively: integrate with Google Cloud Storage (future)

### Frontend
- `UploadService` in `core/services/upload.service.ts`:
  - `uploadImages(files: File[]): Observable<string[]>`
- Post composer updated in `feed.component.html` and `community-detail.component.html`
- Lightbox: simple overlay component `shared/components/lightbox/lightbox.component.ts`
- No external library needed — CSS `fixed inset-0` overlay with flexbox image

---

## Database Migration

No migration needed — `photo_urls` column already exists on `posts` as `TEXT` (JSON array of URLs).
