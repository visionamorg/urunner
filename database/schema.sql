-- RunHub Database Schema
-- PostgreSQL 15+

-- Create database (run as superuser if needed)
-- CREATE DATABASE runhub;
-- CREATE USER runhub WITH PASSWORD 'runhub123';
-- GRANT ALL PRIVILEGES ON DATABASE runhub TO runhub;

-- ============================================================
-- ENUMS
-- ============================================================

-- Note: All enums stored as VARCHAR to avoid Hibernate named-enum cast conflicts
CREATE TYPE registration_status AS ENUM ('REGISTERED', 'CANCELLED', 'COMPLETED');
CREATE TYPE program_level AS ENUM ('BEGINNER', 'INTERMEDIATE', 'ADVANCED');
CREATE TYPE progress_status AS ENUM ('ACTIVE', 'COMPLETED', 'PAUSED');

-- ============================================================
-- USERS
-- ============================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    bio TEXT,
    profile_image_url VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(255),
    provider_access_token VARCHAR(1000),
    provider_refresh_token VARCHAR(1000),
    provider_token_secret VARCHAR(1000),
    provider_token_expires_at BIGINT,
    location VARCHAR(200),
    running_category VARCHAR(50),
    passion TEXT,
    gender VARCHAR(20),
    years_running INTEGER,
    weekly_goal_km DECIMAL(8,2),
    pb_5k VARCHAR(20),
    pb_10k VARCHAR(20),
    pb_half_marathon VARCHAR(20),
    pb_marathon VARCHAR(20),
    instagram_handle VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- ============================================================
-- RUNNING ACTIVITIES
-- ============================================================

CREATE TABLE running_activities (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    distance_km DECIMAL(8,2) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    pace_min_per_km DECIMAL(6,2),
    activity_date DATE NOT NULL,
    location VARCHAR(300),
    notes TEXT,
    source VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    external_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_activities_user_id ON running_activities(user_id);
CREATE INDEX idx_activities_date ON running_activities(activity_date DESC);
CREATE INDEX idx_activities_user_date ON running_activities(user_id, activity_date DESC);
CREATE INDEX idx_activities_external_id ON running_activities(external_id) WHERE external_id IS NOT NULL;

-- ============================================================
-- COMMUNITIES
-- ============================================================

CREATE TABLE communities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    cover_url VARCHAR(500),
    drive_folder_id VARCHAR(500),
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    creator_id BIGINT NOT NULL REFERENCES users(id),
    member_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_communities_creator ON communities(creator_id);

CREATE TABLE community_members (
    community_id BIGINT NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (community_id, user_id)
);

CREATE INDEX idx_community_members_user ON community_members(user_id);

-- ============================================================
-- EVENTS
-- ============================================================

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    location VARCHAR(300) NOT NULL,
    distance_km DECIMAL(8,2),
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    max_participants INTEGER,
    is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
    photo_urls TEXT,
    organizer_id BIGINT NOT NULL REFERENCES users(id),
    community_id BIGINT REFERENCES communities(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_events_organizer ON events(organizer_id);
CREATE INDEX idx_events_community ON events(community_id);

CREATE TABLE event_registrations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status registration_status NOT NULL DEFAULT 'REGISTERED',
    registered_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (event_id, user_id)
);

CREATE INDEX idx_event_reg_event ON event_registrations(event_id);
CREATE INDEX idx_event_reg_user ON event_registrations(user_id);

-- ============================================================
-- SOCIAL FEED
-- ============================================================

CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    image_url VARCHAR(500),
    photo_urls TEXT,
    post_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    community_id BIGINT REFERENCES communities(id),
    likes_count INTEGER NOT NULL DEFAULT 0,
    comments_count INTEGER NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_posts_author ON posts(author_id);
CREATE INDEX idx_posts_community ON posts(community_id);
CREATE INDEX idx_posts_created ON posts(created_at DESC);

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_post ON comments(post_id);

CREATE TABLE likes (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (post_id, user_id)
);

CREATE INDEX idx_likes_post ON likes(post_id);
CREATE INDEX idx_likes_user ON likes(user_id);

-- ============================================================
-- CHAT MESSAGES
-- ============================================================

-- ============================================================
-- ROOMS (private channels within communities)
-- ============================================================

CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    community_id BIGINT NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    created_by BIGINT NOT NULL REFERENCES users(id),
    is_private BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rooms_community ON rooms(community_id);

CREATE TABLE room_members (
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (room_id, user_id)
);

-- ============================================================
-- CHAT MESSAGES
-- ============================================================

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    community_id BIGINT REFERENCES communities(id),
    event_id BIGINT REFERENCES events(id),
    room_id BIGINT REFERENCES rooms(id),
    content TEXT NOT NULL DEFAULT '',
    media_url VARCHAR(1000),
    media_type VARCHAR(20),
    sent_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_messages_community ON messages(community_id, sent_at DESC);
CREATE INDEX idx_messages_event ON messages(event_id, sent_at DESC);
CREATE INDEX idx_messages_room ON messages(room_id, sent_at DESC);
CREATE INDEX idx_messages_sender ON messages(sender_id);

-- ============================================================
-- POST REACTIONS (emoji reactions)
-- ============================================================

CREATE TABLE post_reactions (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    emoji VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (post_id, user_id)
);

CREATE INDEX idx_post_reactions_post ON post_reactions(post_id);

-- ============================================================
-- COMMUNITY INVITES
-- ============================================================

CREATE TABLE community_invites (
    id BIGSERIAL PRIMARY KEY,
    community_id BIGINT NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    invited_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    invited_by_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(36) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL DEFAULT (NOW() + INTERVAL '7 days')
);

CREATE INDEX idx_invites_community ON community_invites(community_id);
CREATE INDEX idx_invites_token ON community_invites(token);

-- ============================================================
-- BADGES
-- ============================================================

CREATE TABLE badges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT NOT NULL,
    icon_url VARCHAR(500),
    criteria VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_badges (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    badge_id BIGINT NOT NULL REFERENCES badges(id),
    earned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, badge_id)
);

CREATE INDEX idx_user_badges_user ON user_badges(user_id);

-- ============================================================
-- TRAINING PROGRAMS
-- ============================================================

CREATE TABLE programs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    level program_level NOT NULL DEFAULT 'BEGINNER',
    duration_weeks INTEGER NOT NULL,
    target_distance_km DECIMAL(6,2),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE program_sessions (
    id BIGSERIAL PRIMARY KEY,
    program_id BIGINT NOT NULL REFERENCES programs(id) ON DELETE CASCADE,
    week_number INTEGER NOT NULL,
    day_number INTEGER NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    distance_km DECIMAL(6,2),
    duration_minutes INTEGER
);

CREATE INDEX idx_program_sessions_program ON program_sessions(program_id);

CREATE TABLE user_program_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    program_id BIGINT NOT NULL REFERENCES programs(id),
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_sessions INTEGER NOT NULL DEFAULT 0,
    status progress_status NOT NULL DEFAULT 'ACTIVE'
);

CREATE INDEX idx_progress_user ON user_program_progress(user_id);
CREATE INDEX idx_progress_program ON user_program_progress(program_id);

-- ============================================================
-- PAYMENTS (stub)
-- ============================================================

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    event_id BIGINT REFERENCES events(id),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    stripe_payment_id VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- FUNCTIONS & TRIGGERS
-- ============================================================

-- Auto-calculate pace on activity insert/update
CREATE OR REPLACE FUNCTION calculate_pace()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.distance_km > 0 THEN
        NEW.pace_min_per_km := NEW.duration_minutes::DECIMAL / NEW.distance_km;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_calculate_pace
    BEFORE INSERT OR UPDATE ON running_activities
    FOR EACH ROW EXECUTE FUNCTION calculate_pace();

-- Auto-update member count on community_members change
CREATE OR REPLACE FUNCTION update_community_member_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE communities SET member_count = member_count + 1 WHERE id = NEW.community_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE communities SET member_count = member_count - 1 WHERE id = OLD.community_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_community_member_count
    AFTER INSERT OR DELETE ON community_members
    FOR EACH ROW EXECUTE FUNCTION update_community_member_count();

-- Auto-update post likes count
CREATE OR REPLACE FUNCTION update_post_likes_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE posts SET likes_count = likes_count + 1 WHERE id = NEW.post_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE posts SET likes_count = likes_count - 1 WHERE id = OLD.post_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_post_likes_count
    AFTER INSERT OR DELETE ON likes
    FOR EACH ROW EXECUTE FUNCTION update_post_likes_count();

-- Auto-update post comments count
CREATE OR REPLACE FUNCTION update_post_comments_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE posts SET comments_count = comments_count + 1 WHERE id = NEW.post_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE posts SET comments_count = comments_count - 1 WHERE id = OLD.post_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_post_comments_count
    AFTER INSERT OR DELETE ON comments
    FOR EACH ROW EXECUTE FUNCTION update_post_comments_count();

-- Update users.updated_at
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();
