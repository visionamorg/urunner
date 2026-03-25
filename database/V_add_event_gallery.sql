-- Event Gallery: link Drive folders to events, store gallery photos

ALTER TABLE events ADD COLUMN IF NOT EXISTS drive_folder_id VARCHAR(255);

CREATE TABLE IF NOT EXISTS event_gallery_photos (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    photo_url TEXT NOT NULL,
    thumbnail_url TEXT,
    drive_file_id VARCHAR(255),
    uploaded_by_username VARCHAR(100),
    bib_number VARCHAR(20),
    tagged_username VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_gallery_event_id ON event_gallery_photos(event_id);
CREATE INDEX IF NOT EXISTS idx_gallery_drive_file ON event_gallery_photos(event_id, drive_file_id);
