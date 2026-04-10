# US-010 — Direct Photo Upload in Posts

**Status: DONE** — Completed 2026-04-10


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
- [x] "Create post" area at the top of the feed shows a photo attachment icon
- [x] Clicking opens a file picker (accept `image/*`, max 5 files)
- [x] Selected images show as thumbnails in the composer before posting
- [x] Each thumbnail has an X to remove it
- [x] On submit, images are uploaded then post is created — loading state shown

### Post Composer (Community Feed)
- [x] Same photo upload available when creating a post in a community feed

### Viewing Photo Posts
- [x] Photo posts render using the existing Facebook-style photo grid (1/2/3/4+ layout already built)
- [x] Photos are tap/click-able to open a lightbox (fullscreen view)
- [x] Lightbox: swipe/arrow navigation between photos, X to close

### Backend — File Upload
- [x] `POST /api/uploads/images` — multipart file upload, returns list of URLs
- [x] Files stored either on local filesystem (Docker volume) or cloud (first: local `/uploads/`, served via Nginx)
- [x] Max file size: 10MB per file, max 5 files per request
- [x] Validate MIME type server-side (only images)
- [x] `FeedService.createPhotoPost()` already accepts `photoUrls` — reuse it
- [x] `CommunityService.createPost()` for community photo posts — same approach

### Security
- [x] Upload endpoint requires authentication
- [x] Sanitize filenames (UUID-based rename on server side)

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
