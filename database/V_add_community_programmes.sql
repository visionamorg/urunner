-- Community Training Programmes
ALTER TABLE programs ADD COLUMN IF NOT EXISTS community_id BIGINT REFERENCES communities(id) ON DELETE CASCADE;
ALTER TABLE programs ADD COLUMN IF NOT EXISTS created_by_id BIGINT REFERENCES users(id);

CREATE INDEX IF NOT EXISTS idx_programs_community ON programs(community_id);
