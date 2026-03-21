-- Export Templates (Community Template Marketplace)
CREATE TABLE IF NOT EXISTS export_templates (
    id BIGSERIAL PRIMARY KEY,
    creator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    css_layout TEXT NOT NULL,
    preview_url VARCHAR(500),
    votes INTEGER NOT NULL DEFAULT 0,
    downloads INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS template_votes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    template_id BIGINT NOT NULL REFERENCES export_templates(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, template_id)
);

CREATE INDEX IF NOT EXISTS idx_export_templates_votes ON export_templates(votes DESC);
CREATE INDEX IF NOT EXISTS idx_template_votes_user ON template_votes(user_id, template_id);
