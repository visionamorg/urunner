-- Community custom tags for members
CREATE TABLE IF NOT EXISTS community_tags (
    id BIGSERIAL PRIMARY KEY,
    community_id BIGINT NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) NOT NULL DEFAULT '#3b82f6',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS member_tags (
    id BIGSERIAL PRIMARY KEY,
    community_id BIGINT NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES community_tags(id) ON DELETE CASCADE,
    UNIQUE(community_id, user_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_community_tags_community ON community_tags(community_id);
CREATE INDEX IF NOT EXISTS idx_member_tags_member ON member_tags(community_id, user_id);
