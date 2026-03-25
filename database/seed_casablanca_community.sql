-- Seed Demo Data: UR Urban Runners Casablanca
-- Uses community_id = 4 (already exists)

-- 1. Create 15 Casablanca members (skip if username already exists)
INSERT INTO users (username, email, password, first_name, last_name) VALUES
  ('maryiam_casa', 'maryiam@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Maryiam', 'El Idrissi'),
  ('elmahdi_run', 'elmahdi@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Elmahdi', 'Hajouji'),
  ('siham_pace', 'siham@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Siham', 'Benali'),
  ('kenza_trails', 'kenza@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Kenza', 'Amrani'),
  ('abdelaziz_run', 'abdelaziz@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Abdelaziz', 'Tazi'),
  ('hind_runner', 'hind@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Hind', 'Fassi'),
  ('yasser_casa', 'yasser@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Yasser', 'Ouazzani'),
  ('youssef_run', 'youssef@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Youssef', 'Berrada'),
  ('fatima_jog', 'fatima@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Fatima', 'Chraibi'),
  ('tariq_speed', 'tariq@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Tariq', 'Senhaji'),
  ('salma_run', 'salma@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Salma', 'Kettani'),
  ('omar_casa', 'omar@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Omar', 'Alami'),
  ('nadia_run', 'nadia_casa@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Nadia', 'Benmoussa'),
  ('karim_pacer', 'karim@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Karim', 'Lahlou'),
  ('amina_run', 'amina@runhub.demo', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Amina', 'Sqalli')
ON CONFLICT (username) DO NOTHING;

-- 2. Update community 4
UPDATE communities SET
  name = 'UR Urban Runners Casablanca',
  description = 'Welcome to UR Urban Runners Casablanca! We are a passionate community of runners navigating the vibrant streets and breathtaking coastlines of Casa. Whether you''re training for your next marathon or just looking for a weekend recovery run followed by Moroccan mint tea, you belong here. Lace up and let''s go!',
  cover_url = 'https://images.unsplash.com/photo-1570168007204-dfb528c6958f?w=1200',
  image_url = 'https://images.unsplash.com/photo-1596727362302-b8d891c42ab8?w=400'
WHERE id = 4;

-- 3. Add all 15 members to community 4
INSERT INTO community_members (community_id, user_id, role) VALUES
  -- Admins
  (4, (SELECT id FROM users WHERE username = 'maryiam_casa'), 'ADMIN'),
  (4, (SELECT id FROM users WHERE username = 'elmahdi_run'), 'ADMIN'),
  (4, (SELECT id FROM users WHERE username = 'siham_pace'), 'ADMIN'),
  -- Moderators
  (4, (SELECT id FROM users WHERE username = 'kenza_trails'), 'MODERATOR'),
  (4, (SELECT id FROM users WHERE username = 'abdelaziz_run'), 'MODERATOR'),
  (4, (SELECT id FROM users WHERE username = 'hind_runner'), 'MODERATOR'),
  -- Members
  (4, (SELECT id FROM users WHERE username = 'yasser_casa'), 'MEMBER'),
  (4, (SELECT id FROM users WHERE username = 'youssef_run'), 'MEMBER'),
  (4, (SELECT id FROM users WHERE username = 'fatima_jog'), 'MEMBER'),
  (4, (SELECT id FROM users WHERE username = 'tariq_speed'), 'MEMBER'),
  (4, (SELECT id FROM users WHERE username = 'salma_run'), 'MEMBER'),
  (4, (SELECT id FROM users WHERE username = 'omar_casa'), 'MEMBER'),
  (4, (SELECT id FROM users WHERE username = 'nadia_run'), 'MEMBER'),
  (4, (SELECT id FROM users WHERE username = 'karim_pacer'), 'MEMBER'),
  (4, (SELECT id FROM users WHERE username = 'amina_run'), 'MEMBER')
ON CONFLICT (community_id, user_id) DO UPDATE SET role = EXCLUDED.role;

-- Update member count
UPDATE communities SET member_count = (SELECT COUNT(*) FROM community_members WHERE community_id = 4) WHERE id = 4;

-- 4. Custom Tags
INSERT INTO community_tags (community_id, name, color) VALUES
  (4, 'Pacer', '#f59e0b'),
  (4, 'Coach', '#ef4444'),
  (4, 'Trail Expert', '#22c55e'),
  (4, 'Newbie', '#8b5cf6')
ON CONFLICT DO NOTHING;

-- Assign tags
INSERT INTO member_tags (community_id, user_id, tag_id) VALUES
  (4, (SELECT id FROM users WHERE username = 'abdelaziz_run'), (SELECT id FROM community_tags WHERE community_id = 4 AND name = 'Pacer')),
  (4, (SELECT id FROM users WHERE username = 'kenza_trails'), (SELECT id FROM community_tags WHERE community_id = 4 AND name = 'Trail Expert')),
  (4, (SELECT id FROM users WHERE username = 'elmahdi_run'), (SELECT id FROM community_tags WHERE community_id = 4 AND name = 'Coach')),
  (4, (SELECT id FROM users WHERE username = 'amina_run'), (SELECT id FROM community_tags WHERE community_id = 4 AND name = 'Newbie')),
  (4, (SELECT id FROM users WHERE username = 'youssef_run'), (SELECT id FROM community_tags WHERE community_id = 4 AND name = 'Newbie'))
ON CONFLICT (community_id, user_id, tag_id) DO NOTHING;

-- 5. Events (mix of past and upcoming)
INSERT INTO events (name, description, event_date, location, distance_km, price, max_participants, organizer_id, community_id) VALUES
  ('Weekly Ain Diab Coastal Long Run',
   'Join us every Sunday for our signature coastal long run along the Ain Diab Corniche. Beautiful ocean views, steady pace groups, and post-run mint tea at the cafe!',
   NOW() + INTERVAL '3 days', 'Ain Diab Corniche, Casablanca', 15.0, 0.00, 50,
   (SELECT id FROM users WHERE username = 'maryiam_casa'), 4),

  ('Medina Sunrise 5K & Beldi Breakfast',
   'Experience the magic of running through the Old Medina at sunrise. Narrow alleys, ancient walls, and the call to prayer as your soundtrack. Followed by traditional Beldi breakfast!',
   NOW() + INTERVAL '10 days', 'Old Medina, Casablanca', 5.0, 50.00, 30,
   (SELECT id FROM users WHERE username = 'elmahdi_run'), 4),

  ('Bouskoura Forest Trail Prep',
   'Trail prep session in the beautiful Bouskoura Forest. Elevation changes, dirt paths, and fresh air. Perfect for those training for upcoming trail races.',
   NOW() + INTERVAL '17 days', 'Bouskoura Forest, Casablanca', 20.0, 0.00, 25,
   (SELECT id FROM users WHERE username = 'kenza_trails'), 4),

  ('Recovery Shakeout Run + Coffee',
   'Easy recovery shakeout through the Maarif neighborhood. No pressure, no pace — just good company and a coffee stop at our favorite spot.',
   NOW() - INTERVAL '3 days', 'Maarif, Casablanca', 5.0, 0.00, NULL,
   (SELECT id FROM users WHERE username = 'siham_pace'), 4),

  ('Casablanca Night Run',
   'Experience Casa under the lights! A 10K loop along the illuminated Corniche. Reflective gear required. Water stations provided.',
   NOW() + INTERVAL '24 days', 'Corniche, Casablanca', 10.0, 0.00, 100,
   (SELECT id FROM users WHERE username = 'maryiam_casa'), 4);

-- Register participants for events
INSERT INTO event_registrations (event_id, user_id, status)
SELECT e.id, u.id, 'REGISTERED'
FROM events e
CROSS JOIN (
  SELECT id FROM users WHERE username IN ('maryiam_casa','elmahdi_run','siham_pace','kenza_trails','abdelaziz_run','hind_runner','yasser_casa','youssef_run','fatima_jog','tariq_speed')
) u
WHERE e.community_id = 4 AND e.event_date > NOW()
ON CONFLICT DO NOTHING;

-- 6. Feed Posts
-- Pinned welcome post by Elmahdi
INSERT INTO posts (author_id, community_id, content, post_type, pinned, created_at) VALUES
  ((SELECT id FROM users WHERE username = 'elmahdi_run'), 4,
   'Welcome to the new members! Don''t forget our Ain Diab run this Sunday. Bring water and good vibes! 🌊🏃‍♂️',
   'TEXT', true, NOW() - INTERVAL '2 days');

-- Trail post by Kenza
INSERT INTO posts (author_id, community_id, content, post_type, photo_urls, created_at) VALUES
  ((SELECT id FROM users WHERE username = 'kenza_trails'), 4,
   'The Bouskoura trail was amazing today. Check out these photos! 🌲🏃‍♀️ The forest section is so peaceful in the morning.',
   'PHOTO_ALBUM',
   '["https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=800","https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=800","https://images.unsplash.com/photo-1571008887538-b36bb32f4571?w=800"]',
   NOW() - INTERVAL '1 day');

-- Member post by Yasser
INSERT INTO posts (author_id, community_id, content, post_type, created_at) VALUES
  ((SELECT id FROM users WHERE username = 'yasser_casa'), 4,
   'Anyone doing an easy 5k around Anfa Park tomorrow morning? Looking for a running buddy! 🏃‍♂️',
   'TEXT', NOW() - INTERVAL '6 hours');

-- More posts for engagement
INSERT INTO posts (author_id, community_id, content, post_type, created_at) VALUES
  ((SELECT id FROM users WHERE username = 'maryiam_casa'), 4,
   'Reminder: Night Run registration closes this Friday! Already 45 runners signed up. Let''s make it our biggest event yet 🌙✨',
   'TEXT', NOW() - INTERVAL '12 hours');

INSERT INTO posts (author_id, community_id, content, post_type, created_at) VALUES
  ((SELECT id FROM users WHERE username = 'abdelaziz_run'), 4,
   'Pace group update for Sunday''s long run: Group A (5:00/km), Group B (5:30/km), Group C (6:00+/km). Everyone is welcome, no runner left behind! 💪',
   'TEXT', NOW() - INTERVAL '18 hours');

INSERT INTO posts (author_id, community_id, content, post_type, created_at) VALUES
  ((SELECT id FROM users WHERE username = 'fatima_jog'), 4,
   'Just completed my first ever 10K! Couldn''t have done it without the encouragement from this amazing group. Thank you all 🙏❤️',
   'TEXT', NOW() - INTERVAL '3 days');

INSERT INTO posts (author_id, community_id, content, post_type, photo_urls, created_at) VALUES
  ((SELECT id FROM users WHERE username = 'hind_runner'), 4,
   'Sunset run along the Corniche yesterday. Casa never fails to deliver these views 🌅',
   'PHOTO_ALBUM',
   '["https://images.unsplash.com/photo-1504609773096-104ff2c73ba4?w=800","https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800"]',
   NOW() - INTERVAL '4 days');

INSERT INTO posts (author_id, community_id, content, post_type, created_at) VALUES
  ((SELECT id FROM users WHERE username = 'salma_run'), 4,
   'Who''s tried the new running track near Morocco Mall? Heard it''s lit at night with good lighting. Would love a group session there!',
   'TEXT', NOW() - INTERVAL '5 days');

-- 7. Likes on posts
INSERT INTO likes (post_id, user_id)
SELECT p.id, u.id
FROM posts p
CROSS JOIN (
  SELECT id FROM users WHERE username IN ('maryiam_casa','elmahdi_run','siham_pace','kenza_trails','abdelaziz_run','hind_runner','yasser_casa','youssef_run','fatima_jog','tariq_speed','salma_run','omar_casa','karim_pacer','amina_run')
) u
WHERE p.community_id = 4
  AND random() < 0.4
ON CONFLICT DO NOTHING;

-- 8. Comments
INSERT INTO comments (post_id, author_id, content, created_at)
SELECT p.id, (SELECT id FROM users WHERE username = 'siham_pace'),
  'Love this! Count me in 🔥', p.created_at + INTERVAL '30 minutes'
FROM posts p WHERE p.community_id = 4 AND p.author_id = (SELECT id FROM users WHERE username = 'elmahdi_run') LIMIT 1;

INSERT INTO comments (post_id, author_id, content, created_at)
SELECT p.id, (SELECT id FROM users WHERE username = 'youssef_run'),
  'Those photos are incredible! Which trail was that?', p.created_at + INTERVAL '1 hour'
FROM posts p WHERE p.community_id = 4 AND p.author_id = (SELECT id FROM users WHERE username = 'kenza_trails') LIMIT 1;

INSERT INTO comments (post_id, author_id, content, created_at)
SELECT p.id, (SELECT id FROM users WHERE username = 'tariq_speed'),
  'I can join! Let''s meet at 7am by the park entrance 👟', p.created_at + INTERVAL '45 minutes'
FROM posts p WHERE p.community_id = 4 AND p.author_id = (SELECT id FROM users WHERE username = 'yasser_casa') LIMIT 1;

INSERT INTO comments (post_id, author_id, content, created_at)
SELECT p.id, (SELECT id FROM users WHERE username = 'karim_pacer'),
  'Congrats Fatima! That''s a huge milestone 🎉🎉', p.created_at + INTERVAL '2 hours'
FROM posts p WHERE p.community_id = 4 AND p.author_id = (SELECT id FROM users WHERE username = 'fatima_jog') LIMIT 1;

INSERT INTO comments (post_id, author_id, content, created_at)
SELECT p.id, (SELECT id FROM users WHERE username = 'omar_casa'),
  'Group C is my speed 😂 See you Sunday!', p.created_at + INTERVAL '1 hour'
FROM posts p WHERE p.community_id = 4 AND p.author_id = (SELECT id FROM users WHERE username = 'abdelaziz_run') LIMIT 1;

-- 9. Running activities for the 15 members (last 30 days)
INSERT INTO running_activities (user_id, activity_date, distance_km, duration_minutes, pace_min_per_km, source, title, elevation_gain_meters, avg_heart_rate, avg_cadence)
SELECT u.id,
  (CURRENT_DATE - (random() * 28)::int),
  round((3 + random() * 17)::numeric, 2),
  (20 + (random() * 100)::int),
  round((4.0 + random() * 3.0)::numeric, 2),
  'MANUAL',
  (ARRAY['Morning Run','Corniche Loop','Ain Diab Coastal','Anfa Park Easy','Bouskoura Trail','Maarif Recovery','Night Run Session','Long Run Sunday','Tempo Run','Interval Training'])[(1 + (random() * 9)::int)],
  (10 + (random() * 200)::int),
  (130 + (random() * 40)::int),
  (160 + (random() * 20)::int)
FROM users u
CROSS JOIN generate_series(1, 5) s
WHERE u.username IN ('maryiam_casa','elmahdi_run','siham_pace','kenza_trails','abdelaziz_run','hind_runner','yasser_casa','youssef_run','fatima_jog','tariq_speed','salma_run','omar_casa','nadia_run','karim_pacer','amina_run');

-- 10. Chat messages
INSERT INTO messages (sender_id, community_id, content, sent_at) VALUES
  ((SELECT id FROM users WHERE username = 'maryiam_casa'), 4, 'Good morning everyone! Ready for Sunday?', NOW() - INTERVAL '2 hours'),
  ((SELECT id FROM users WHERE username = 'elmahdi_run'), 4, 'Always ready! What''s the meeting point?', NOW() - INTERVAL '1 hour 50 minutes'),
  ((SELECT id FROM users WHERE username = 'kenza_trails'), 4, 'The usual spot by the lighthouse. 7am sharp!', NOW() - INTERVAL '1 hour 45 minutes'),
  ((SELECT id FROM users WHERE username = 'yasser_casa'), 4, 'Can someone bring extra water bottles? 😅', NOW() - INTERVAL '1 hour 30 minutes'),
  ((SELECT id FROM users WHERE username = 'siham_pace'), 4, 'I''ll bring some! Don''t worry', NOW() - INTERVAL '1 hour 20 minutes'),
  ((SELECT id FROM users WHERE username = 'tariq_speed'), 4, 'What pace are we targeting?', NOW() - INTERVAL '1 hour'),
  ((SELECT id FROM users WHERE username = 'abdelaziz_run'), 4, 'Multiple pace groups: 5:00, 5:30, and 6:00+. Pick your vibe!', NOW() - INTERVAL '50 minutes'),
  ((SELECT id FROM users WHERE username = 'fatima_jog'), 4, '6:00 group for me please 🐢😂', NOW() - INTERVAL '40 minutes'),
  ((SELECT id FROM users WHERE username = 'hind_runner'), 4, 'Weather looks perfect for tomorrow', NOW() - INTERVAL '30 minutes'),
  ((SELECT id FROM users WHERE username = 'omar_casa'), 4, 'Let''s gooo! 🔥🔥', NOW() - INTERVAL '15 minutes');

-- 11. Notifications for new members
INSERT INTO notifications (user_id, type, title, message, link) VALUES
  ((SELECT id FROM users WHERE username = 'amina_run'), 'GENERAL', 'Welcome!', 'Welcome to UR Urban Runners Casablanca! Check out the upcoming events and join a run.', '/communities/4'),
  ((SELECT id FROM users WHERE username = 'youssef_run'), 'GENERAL', 'Welcome!', 'Welcome to UR Urban Runners Casablanca! Check out the upcoming events and join a run.', '/communities/4');

-- 12. Training Programme
INSERT INTO programs (name, description, level, duration_weeks, target_distance_km, community_id, created_by_id) VALUES
  ('Couch to 10K Casa', 'A 6-week progressive programme designed for Casablanca runners. From easy jogs around Anfa Park to a full 10K along the Corniche!', 'BEGINNER', 6, 10.0,
   4, (SELECT id FROM users WHERE username = 'elmahdi_run'));

INSERT INTO program_sessions (program_id, week_number, day_number, title, description, distance_km, duration_minutes) VALUES
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 1, 1, 'Easy Walk/Jog', 'Alternate 2 min walk / 1 min jog around Anfa Park', 2.0, 20),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 1, 3, 'Easy Jog', '3 min jog / 1 min walk repeats', 2.5, 25),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 1, 5, 'Long Walk', 'Brisk walk along Ain Diab', 3.0, 30),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 2, 1, 'Steady Jog', '5 min continuous jog, walk breaks', 3.0, 25),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 2, 3, 'Tempo Walk/Run', 'Alternate 3 min jog / 1 min walk', 3.5, 30),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 2, 5, 'Long Jog', 'Easy continuous jog, Maarif loop', 4.0, 35),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 3, 1, 'Corniche Easy 5K', '5K at conversational pace along the coast', 5.0, 35),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 3, 3, 'Intervals', '6x400m with 200m walk recovery', 4.0, 30),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 3, 5, 'Long Run', 'Easy 6K through Ain Diab', 6.0, 42),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 4, 1, 'Tempo 5K', '5K at slightly faster pace', 5.0, 32),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 4, 3, 'Hill Repeats', '5x hill reps near Anfa, easy jog down', 4.0, 30),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 4, 5, 'Long Run', '7K easy along the coast', 7.0, 48),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 5, 1, 'Easy Recovery', 'Relaxed jog around the park', 4.0, 28),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 5, 3, 'Fast Finish', '6K with last 2K at race pace', 6.0, 38),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 5, 5, 'Dress Rehearsal', '8K at target 10K pace', 8.0, 50),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 6, 1, 'Easy Shakeout', 'Light 3K jog, stay loose', 3.0, 20),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 6, 3, 'Rest or Stretch', 'Active recovery, yoga, stretching', 0.0, 20),
  ((SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 6, 5, 'RACE DAY: 10K!', 'Casablanca Corniche 10K — you earned this!', 10.0, 60);

-- Enroll some members
INSERT INTO user_program_progress (user_id, program_id, completed_sessions, status) VALUES
  ((SELECT id FROM users WHERE username = 'amina_run'), (SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 4, 'ACTIVE'),
  ((SELECT id FROM users WHERE username = 'youssef_run'), (SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 8, 'ACTIVE'),
  ((SELECT id FROM users WHERE username = 'fatima_jog'), (SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 14, 'ACTIVE'),
  ((SELECT id FROM users WHERE username = 'omar_casa'), (SELECT id FROM programs WHERE name = 'Couch to 10K Casa'), 18, 'COMPLETED');
