-- Activity Telemetry: new columns on running_activities + activity_splits table

ALTER TABLE running_activities ADD COLUMN IF NOT EXISTS elevation_gain_meters INTEGER;
ALTER TABLE running_activities ADD COLUMN IF NOT EXISTS avg_heart_rate INTEGER;
ALTER TABLE running_activities ADD COLUMN IF NOT EXISTS max_heart_rate INTEGER;
ALTER TABLE running_activities ADD COLUMN IF NOT EXISTS avg_cadence INTEGER;
ALTER TABLE running_activities ADD COLUMN IF NOT EXISTS map_polyline TEXT;

CREATE TABLE IF NOT EXISTS activity_splits (
    id BIGSERIAL PRIMARY KEY,
    activity_id BIGINT NOT NULL REFERENCES running_activities(id) ON DELETE CASCADE,
    split_km INTEGER NOT NULL,
    split_pace DECIMAL(6,2),
    split_elevation DECIMAL(8,2),
    split_heart_rate INTEGER
);

CREATE INDEX IF NOT EXISTS idx_activity_splits_activity_id ON activity_splits(activity_id);
