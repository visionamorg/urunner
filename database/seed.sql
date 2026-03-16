-- RunHub Seed Data
-- Run AFTER schema.sql

-- ============================================================
-- USERS (password: "password123" - BCrypt hash)
-- ============================================================

INSERT INTO users (username, email, password, first_name, last_name, bio, role) VALUES
('admin', 'admin@runhub.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Admin', 'User', 'RunHub administrator', 'ADMIN'),
('alice_runner', 'alice@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Alice', 'Johnson', 'Marathon enthusiast. Running since 2018. Boston qualifier 2023!', 'USER'),
('bob_sprints', 'bob@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Bob', 'Smith', '5k and 10k specialist. Love track workouts.', 'USER'),
('carol_trails', 'carol@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Carol', 'Williams', 'Trail runner. Mountains are my happy place.', 'USER'),
('david_ultra', 'david@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'David', 'Brown', 'Ultramarathon runner. 100-miler finisher.', 'ORGANIZER'),
('emma_pace', 'emma@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Emma', 'Davis', 'Certified running coach. Helping others reach their goals.', 'ORGANIZER'),
('frank_morning', 'frank@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Frank', 'Miller', 'Early morning runner. 5am club member.', 'USER'),
('grace_newbie', 'grace@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Grace', 'Wilson', 'Just started running 3 months ago. Loving it!', 'USER'),
('henry_ironman', 'henry@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Henry', 'Moore', 'Triathlete. Swimming, biking, and running.', 'USER'),
('iris_speed', 'iris@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/IfKT6bEXwl8gDUU7W', 'Iris', 'Taylor', 'Sub-3 marathon goal. Training hard every day.', 'USER');

-- ============================================================
-- COMMUNITIES
-- ============================================================

INSERT INTO communities (name, description, creator_id, member_count) VALUES
('City Marathon Club', 'A community for marathon runners in the city. Weekly long runs, training plans, and race support.', 5, 6),
('Trail Blazers', 'For trail running enthusiasts. Explore mountains, forests, and off-road adventures together.', 4, 4),
('Morning Runners Squad', 'Early risers who love starting the day with a run. 5am-7am sessions every weekday.', 7, 5);

-- ============================================================
-- COMMUNITY MEMBERS
-- ============================================================

-- City Marathon Club (id=1)
INSERT INTO community_members (community_id, user_id, role) VALUES
(1, 5, 'ADMIN'),   -- david_ultra (creator)
(1, 2, 'MEMBER'),  -- alice_runner
(1, 6, 'MODERATOR'), -- emma_pace
(1, 9, 'MEMBER'),  -- henry_ironman
(1, 10, 'MEMBER'), -- iris_speed
(1, 3, 'MEMBER');  -- bob_sprints

-- Trail Blazers (id=2)
INSERT INTO community_members (community_id, user_id, role) VALUES
(2, 4, 'ADMIN'),   -- carol_trails (creator)
(2, 2, 'MEMBER'),  -- alice_runner
(2, 5, 'MEMBER'),  -- david_ultra
(2, 7, 'MEMBER');  -- frank_morning

-- Morning Runners Squad (id=3)
INSERT INTO community_members (community_id, user_id, role) VALUES
(3, 7, 'ADMIN'),   -- frank_morning (creator)
(3, 3, 'MEMBER'),  -- bob_sprints
(3, 8, 'MEMBER'),  -- grace_newbie
(3, 6, 'MODERATOR'), -- emma_pace
(3, 9, 'MEMBER');  -- henry_ironman

-- ============================================================
-- EVENTS
-- ============================================================

INSERT INTO events (name, description, event_date, location, distance_km, price, max_participants, organizer_id, community_id) VALUES
('City Spring Marathon 2026', 'Annual city marathon through the heart of downtown. Fast, flat course. Boston qualifier!', '2026-04-15 07:00:00', 'City Hall Plaza, Downtown', 42.2, 75.00, 500, 5, 1),
('Trail Adventure 10K', 'Scenic trail run through the forest. Moderate difficulty with 400m elevation gain.', '2026-03-28 09:00:00', 'Riverside Trail Head, North Park', 10.0, 25.00, 100, 4, 2),
('5K Fun Run for Charity', 'Annual charity 5K supporting local youth running programs. All levels welcome!', '2026-04-05 08:30:00', 'Central Park Bandshell', 5.0, 15.00, 300, 6, NULL),
('Half Marathon Weekend', 'A beautiful half marathon along the coastline. Scenic views and post-race party!', '2026-05-10 08:00:00', 'Oceanfront Boardwalk', 21.1, 45.00, 200, 5, 1),
('Night Run - City Lights', 'Unique evening run through illuminated city streets. Headlamps required.', '2026-04-20 20:00:00', 'Theater District Start', 8.0, 20.00, 150, 6, 3);

-- ============================================================
-- EVENT REGISTRATIONS
-- ============================================================

INSERT INTO event_registrations (event_id, user_id, status) VALUES
(1, 2, 'REGISTERED'),  -- alice in spring marathon
(1, 9, 'REGISTERED'),  -- henry in spring marathon
(1, 10, 'REGISTERED'), -- iris in spring marathon
(1, 3, 'REGISTERED'),  -- bob in spring marathon
(2, 4, 'REGISTERED'),  -- carol in trail 10k
(2, 5, 'REGISTERED'),  -- david in trail 10k
(2, 7, 'REGISTERED'),  -- frank in trail 10k
(3, 8, 'REGISTERED'),  -- grace in charity 5k
(3, 2, 'REGISTERED'),  -- alice in charity 5k
(3, 6, 'REGISTERED'),  -- emma in charity 5k
(4, 10, 'REGISTERED'), -- iris in half marathon
(4, 2, 'REGISTERED'),  -- alice in half marathon
(5, 7, 'REGISTERED'),  -- frank in night run
(5, 3, 'REGISTERED');  -- bob in night run

-- ============================================================
-- RUNNING ACTIVITIES (30 total)
-- ============================================================

-- Alice (user 2)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(2, 'Morning Long Run', 21.1, 115, '2026-03-15', 'River Path', 'Great half marathon training run! Felt strong throughout.'),
(2, 'Easy Recovery Run', 8.0, 52, '2026-03-13', 'Neighborhood Loop', 'Easy pace to recover from Sunday''s long run.'),
(2, 'Tempo Run', 12.0, 58, '2026-03-11', 'City Park', '4 miles at tempo pace, felt the burn!'),
(2, 'Track Intervals', 6.4, 35, '2026-03-09', 'High School Track', '8x800m intervals with 400m recovery.');

-- Bob (user 3)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(3, '5K Race Effort', 5.0, 21, '2026-03-15', 'City Park', 'Race simulation. Hit 4:12/km pace!'),
(3, 'Speed Work', 8.0, 40, '2026-03-12', 'Track', '10x400m at 5K pace with 90s recovery.'),
(3, 'Hill Repeats', 7.5, 48, '2026-03-10', 'Hilly Neighborhood', '8 hill repeats. Quads are burning!'),
(3, 'Easy Jog', 5.0, 32, '2026-03-08', 'Riverside Path', 'Active recovery day.');

-- Carol (user 4)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(4, 'Mountain Trail Run', 18.5, 185, '2026-03-14', 'North Mountain', 'Epic trail run with 1200m elevation. Stunning views!'),
(4, 'Forest Loop', 12.0, 105, '2026-03-11', 'Pine Forest Trail', 'Technical single track through the forest.'),
(4, 'Trail Easy Run', 8.0, 65, '2026-03-09', 'River Trail', 'Easy recovery pace on the trails.');

-- David (user 5)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(5, 'Ultra Training - Long Haul', 42.0, 360, '2026-03-10', 'Mountain Circuit', 'Back-to-back long run training for upcoming ultra.'),
(5, 'Recovery Ultra Shuffle', 15.0, 115, '2026-03-12', 'Canal Path', 'Easy ultra shuffle. Heart rate cap 140.'),
(5, 'Night Run', 10.0, 72, '2026-03-14', 'City Streets', 'Night training. Worked on pace consistency.');

-- Emma (user 6)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(6, 'Coach Demo Run', 10.0, 55, '2026-03-15', 'City Park', 'Running with clients, demonstrating proper form.'),
(6, 'Personal Training Run', 15.0, 82, '2026-03-13', 'Lake Trail', 'My personal training run. Feeling fit!'),
(6, 'Marathon Pace Run', 20.0, 108, '2026-03-09', 'Road Course', 'Marathon pace practice. Splits very consistent.');

-- Frank (user 7)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(7, '5am Club Morning Run', 8.0, 45, '2026-03-16', 'Neighborhood', 'Classic early morning run. Sunrise was beautiful!'),
(7, 'Pre-Work Run', 6.0, 35, '2026-03-14', 'River Path', 'Quick morning run before the office.'),
(7, 'Weekend Long Run', 16.0, 95, '2026-03-10', 'Country Road', 'Long run with the morning squad. Great group energy!'),
(7, 'Morning Hills', 9.0, 58, '2026-03-08', 'Hilly Route', 'Added some hills to the morning routine.');

-- Grace (user 8)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(8, 'My First 5K!', 5.0, 38, '2026-03-15', 'Central Park', 'Did my first 5K non-stop! So proud of myself!'),
(8, 'Easy Run Day 2', 3.0, 25, '2026-03-13', 'Neighborhood', 'Following the training plan. Keeping it easy.'),
(8, 'Run-Walk Intervals', 4.0, 35, '2026-03-11', 'Local Park', 'Run 2 min, walk 1 min. Building endurance.');

-- Henry (user 9)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(9, 'Brick Run (After Bike)', 10.0, 52, '2026-03-15', 'Triathlon Circuit', 'Off the bike, legs felt like jelly at first then found rhythm.'),
(9, 'Marathon Prep Run', 18.0, 98, '2026-03-12', 'Road Course', 'Marathon pace run. On track for spring race goal.'),
(9, 'Recovery Jog', 6.0, 40, '2026-03-10', 'Park Loop', 'Easy recovery day after swim/bike double session.');

-- Iris (user 10)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(10, 'Sub-3 Goal Run', 20.0, 100, '2026-03-15', 'Road', 'Running at sub-3 marathon pace. Legs feel ready!'),
(10, 'VO2 Max Intervals', 8.0, 42, '2026-03-13', 'Track', '5x1000m at VO2 max pace. Brutal but necessary.'),
(10, 'Easy Aerobic Run', 12.0, 70, '2026-03-11', 'Park', 'Easy aerobic base building. Heart rate 130-140.'),
(10, 'Long Run', 32.0, 175, '2026-03-08', 'Road Course', '32K long run at marathon pace. Feeling confident!');

-- ============================================================
-- POSTS (social feed)
-- ============================================================

INSERT INTO posts (author_id, content, community_id) VALUES
(2, 'Just finished my longest run this year - 21K in 1:55! The marathon training is going well. Who else is training for City Spring Marathon? 🏃‍♀️', 1),
(4, 'The trails are incredible this time of year. Just got back from an 18.5K mountain run with 1200m of elevation. My legs are jelly but my soul is full. 🏔️', 2),
(8, 'HUGE milestone today: I ran my first ever 5K without stopping!! 3 months ago I could barely run for 5 minutes. If I can do it, you can too! 🎉', NULL),
(5, 'Ultra training update: 42K back-to-back training run complete. 6 hours on feet. The mental game is just as important as the physical. Stay strong everyone!', 1),
(7, '5am club crew was out again this morning! Nothing beats watching the sunrise while getting your miles in. Who wants to join us tomorrow morning?', 3),
(6, 'Coach tip of the day: Your easy runs should be EASY. If you can''t hold a conversation, you''re going too fast. 80% of your training should be in zone 1-2. Run slow to race fast! 🎯', 1),
(10, 'New week, new goals. 32K long run done at marathon pace. Sub-3 marathon is within reach. 8 weeks to go! 💪', 1),
(3, 'Speed work session: 10x400m at 5K pace. Average split: 89 seconds. Fastest I''ve ever run! The track work is paying off.', NULL),
(9, 'Triathlon training note: The transition from bike to run is a skill. Brick workouts are essential - your legs need to know what''s coming! Just finished a 40K bike + 10K run.', NULL),
(2, 'Trail running tip for beginners: Start with easy trails, shorten your stride on technical terrain, and ALWAYS tell someone where you''re going. Safety first!', 2);

-- ============================================================
-- COMMENTS
-- ============================================================

INSERT INTO comments (post_id, author_id, content) VALUES
(1, 6, 'Amazing pace Alice! You''re going to crush the marathon!'),
(1, 10, 'I''m training too! Let''s run together at some point 🏃‍♀️'),
(1, 5, 'Great work! Consistency is the key to marathon success.'),
(3, 2, 'This is so inspiring Grace! Keep it up! We''ve all been there.'),
(3, 6, 'What an achievement! You should be so proud. The first 5K is always the hardest.'),
(3, 7, 'Amazing!! Keep going! Before you know it you''ll be doing 10K!'),
(5, 9, 'Respect! 42K training run is no joke. What''s your ultra target?'),
(7, 2, 'Yes!! You''re going to do it! Sub-3 is totally within reach with your training.'),
(6, 8, 'This is exactly what I needed to read! I always feel like I''m running too slow.'),
(10, 4, 'Great tips! I''d add: waterproof shoes make a huge difference on wet trails.');

-- ============================================================
-- LIKES
-- ============================================================

INSERT INTO likes (post_id, user_id) VALUES
(1, 3), (1, 5), (1, 6), (1, 7), (1, 10),
(2, 2), (2, 5), (2, 7), (2, 9),
(3, 2), (3, 4), (3, 5), (3, 6), (3, 7), (3, 9), (3, 10),
(4, 2), (4, 9), (4, 10),
(5, 2), (5, 3), (5, 6), (5, 9),
(6, 2), (6, 3), (6, 8), (6, 9), (6, 10),
(7, 2), (7, 6), (7, 9),
(8, 3), (8, 7),
(9, 2), (9, 3), (9, 7),
(10, 5), (10, 9);

-- Update likes count to match (triggers would do this in real runtime)
UPDATE posts SET likes_count = (SELECT COUNT(*) FROM likes WHERE post_id = posts.id);
UPDATE posts SET comments_count = (SELECT COUNT(*) FROM comments WHERE post_id = posts.id);

-- ============================================================
-- MESSAGES
-- ============================================================

INSERT INTO messages (sender_id, community_id, content) VALUES
(5, 1, 'Welcome everyone to the City Marathon Club chat! Use this to coordinate training runs and share updates.'),
(2, 1, 'Hey everyone! Is anyone doing a long run this Sunday? Looking for running partners around 6am.'),
(10, 1, 'I''m in for Sunday! Meet at the usual spot by the fountain?'),
(6, 1, 'Reminder: group tempo run on Tuesday at 6pm at City Park. All paces welcome!'),
(9, 1, 'Just signed up for the spring marathon - so excited! My goal is 3:30.'),
(4, 2, 'Trail run this Saturday at 8am! Meet at the North Park trailhead. Moderate 15K route.'),
(5, 2, 'I''ll be there! Should we bring poles for the steep sections?'),
(7, 2, 'Just getting into trail running - is this trail beginner friendly?'),
(4, 2, 'It has some technical sections but nothing too scary. Hiking shoes recommended!'),
(7, 3, 'Good morning squad! Anyone else joining us tomorrow at 5:30am? Fresh air and good vibes!'),
(3, 3, 'I''ll be there! Making it a habit this month.'),
(8, 3, 'I want to join but 5am feels so early... Is it worth it?'),
(7, 3, 'Grace - ABSOLUTELY worth it! You''ll feel amazing all day. We''ll meet you at the park entrance!');

-- ============================================================
-- BADGES
-- ============================================================

INSERT INTO badges (name, description, icon_url, criteria) VALUES
('First Run', 'Complete your very first running activity', '🏃', 'Log your first activity'),
('5K Club', 'Run 5 kilometers in a single activity', '🥉', 'Complete a single activity of 5km or more'),
('10K Warrior', 'Run 10 kilometers in a single activity', '🥈', 'Complete a single activity of 10km or more'),
('Half Marathoner', 'Complete a half marathon distance', '🥇', 'Complete a single activity of 21.1km or more'),
('Marathoner', 'Complete a full marathon distance', '🏅', 'Complete a single activity of 42.2km or more'),
('100 KM Club', 'Run a total of 100 kilometers', '💯', 'Accumulate 100km total distance'),
('500 KM Legend', 'Run a total of 500 kilometers', '🌟', 'Accumulate 500km total distance'),
('Early Bird', 'Log 10 morning runs before 7am', '🌅', 'Log 10 activities tagged as morning runs'),
('Streak Runner', 'Run 7 days in a row', '🔥', 'Log activities for 7 consecutive days'),
('Community Champion', 'Join 3 running communities', '🤝', 'Be a member of 3 communities'),
('Social Butterfly', 'Post 10 times in the community feed', '🦋', 'Create 10 posts'),
('Ultra Beast', 'Run 50 kilometers in a single activity', '⚡', 'Complete a single activity of 50km or more');

-- ============================================================
-- USER BADGES (earned badges based on activities above)
-- ============================================================

-- Alice (user 2) - has 21.1K run
INSERT INTO user_badges (user_id, badge_id) VALUES
(2, 1), -- First Run
(2, 2), -- 5K Club
(2, 3), -- 10K Warrior
(2, 4); -- Half Marathoner

-- Bob (user 3)
INSERT INTO user_badges (user_id, badge_id) VALUES
(3, 1), -- First Run
(3, 2); -- 5K Club

-- Carol (user 4) - trail runner
INSERT INTO user_badges (user_id, badge_id) VALUES
(4, 1), -- First Run
(4, 2), -- 5K Club
(4, 3), -- 10K Warrior
(4, 4); -- Half Marathoner

-- David (user 5) - ultra runner
INSERT INTO user_badges (user_id, badge_id) VALUES
(5, 1), -- First Run
(5, 2), -- 5K Club
(5, 3), -- 10K Warrior
(5, 4), -- Half Marathoner
(5, 5), -- Marathoner
(5, 6), -- 100 KM Club
(5, 12); -- Ultra Beast

-- Emma (user 6)
INSERT INTO user_badges (user_id, badge_id) VALUES
(6, 1), -- First Run
(6, 2), -- 5K Club
(6, 3), -- 10K Warrior
(6, 4); -- Half Marathoner

-- Frank (user 7)
INSERT INTO user_badges (user_id, badge_id) VALUES
(7, 1), -- First Run
(7, 2), -- 5K Club
(7, 3), -- 10K Warrior
(7, 8); -- Early Bird

-- Grace (user 8) - newbie
INSERT INTO user_badges (user_id, badge_id) VALUES
(8, 1), -- First Run
(8, 2); -- 5K Club

-- Henry (user 9)
INSERT INTO user_badges (user_id, badge_id) VALUES
(9, 1), -- First Run
(9, 2), -- 5K Club
(9, 3), -- 10K Warrior
(9, 4); -- Half Marathoner

-- Iris (user 10) - speed runner
INSERT INTO user_badges (user_id, badge_id) VALUES
(10, 1), -- First Run
(10, 2), -- 5K Club
(10, 3), -- 10K Warrior
(10, 4), -- Half Marathoner
(10, 5), -- Marathoner
(10, 6); -- 100 KM Club

-- ============================================================
-- TRAINING PROGRAMS
-- ============================================================

INSERT INTO programs (name, description, level, duration_weeks, target_distance_km) VALUES
('Couch to 5K', 'Start from scratch and run your first 5K in 8 weeks. Perfect for absolute beginners with no running experience.', 'BEGINNER', 8, 5.0),
('10K Intermediate Builder', 'Already running 5K? Level up to 10K in 6 weeks with structured speed and endurance work.', 'INTERMEDIATE', 6, 10.0),
('Marathon Preparation', 'Complete 16-week marathon training plan from half-marathon base. Includes long runs, tempo work, and recovery.', 'ADVANCED', 16, 42.2);

-- Program 1: Couch to 5K sessions (week 1 only shown, others follow pattern)
INSERT INTO program_sessions (program_id, week_number, day_number, title, description, distance_km, duration_minutes) VALUES
-- Week 1
(1, 1, 1, 'Run/Walk Introduction', 'Warm up 5 min walk, then alternate 60s run and 90s walk for 20 minutes, cool down 5 min walk', 2.5, 30),
(1, 1, 2, 'Rest Day', 'Active recovery - gentle stretching or yoga. Let your body adapt.', 0, 30),
(1, 1, 3, 'Run/Walk Session 2', 'Same as Day 1. Focus on breathing and easy conversational pace.', 2.5, 30),
(1, 1, 4, 'Rest Day', 'Rest or light cross-training. Swimming or cycling is great!', 0, 0),
(1, 1, 5, 'Run/Walk Session 3', 'Third session of week 1. You should start feeling more comfortable.', 2.5, 30),
(1, 1, 6, 'Rest Day', 'Rest day. Hydrate well.', 0, 0),
(1, 1, 7, 'Easy Walk', 'Long easy walk 30-45 minutes. Active recovery.', 3.0, 40),
-- Week 2
(1, 2, 1, 'Longer Run Intervals', 'Alternate 90s run and 2 min walk. Total 20 min of intervals.', 3.0, 30),
(1, 2, 3, 'Run Intervals Day 2', '90s run / 2 min walk intervals. You should feel the progress!', 3.0, 30),
(1, 2, 5, 'Run Intervals Day 3', 'Final session of week 2. Push slightly harder on the last interval.', 3.2, 32),
-- Week 3
(1, 3, 1, 'Mixed Intervals', '2x (90s run, 90s walk, 3 min run, 3 min walk)', 3.5, 35),
(1, 3, 3, 'Mixed Intervals Day 2', 'Repeat week 3 pattern. Focus on steady breathing.', 3.5, 35),
(1, 3, 5, 'Mixed Intervals Day 3', 'Last mixed session. You''re doing great!', 3.8, 38);

-- Program 2: 10K sessions (week 1)
INSERT INTO program_sessions (program_id, week_number, day_number, title, description, distance_km, duration_minutes) VALUES
(2, 1, 1, 'Baseline 5K', 'Run 5K at comfortable pace. This is your starting point.', 5.0, 35),
(2, 1, 2, 'Rest Day', 'Full rest or light stretching.', 0, 20),
(2, 1, 3, 'Easy 4K Run', 'Easy aerobic run. Heart rate stays conversational.', 4.0, 28),
(2, 1, 4, 'Strength Cross Training', 'Squats, lunges, core work. 30-45 minutes.', 0, 40),
(2, 1, 5, 'Tempo 3K', '1K warm up, 3K at comfortably hard pace, 1K cool down', 5.0, 30),
(2, 1, 6, 'Long Run 6K', 'Your first long run of the program. Easy pace throughout.', 6.0, 45),
(2, 1, 7, 'Rest Day', 'Complete rest. Eat well and hydrate.', 0, 0);

-- Program 3: Marathon Preparation (week 1)
INSERT INTO program_sessions (program_id, week_number, day_number, title, description, distance_km, duration_minutes) VALUES
(3, 1, 1, 'Easy Run', 'Easy aerobic 8K to start the program. Establish your base.', 8.0, 50),
(3, 1, 2, 'Rest or Cross Train', 'Cycling, swimming, or complete rest.', 0, 0),
(3, 1, 3, 'Medium Run + Strides', '10K easy with 4x100m strides at the end.', 10.5, 60),
(3, 1, 4, 'Rest', 'Complete rest. Stretch and foam roll.', 0, 20),
(3, 1, 5, 'Tempo Run', '2K warm up, 5K at threshold pace, 2K cool down', 9.0, 55),
(3, 1, 6, 'Easy 6K', 'Easy recovery run the day before long run.', 6.0, 40),
(3, 1, 7, 'Long Run', 'First long run: 18K at easy/marathon pace. Fuel practice.', 18.0, 110);

-- ============================================================
-- USER PROGRAM PROGRESS
-- ============================================================

INSERT INTO user_program_progress (user_id, program_id, started_at, completed_sessions, status) VALUES
(8, 1, '2026-03-01 08:00:00', 6, 'ACTIVE'),   -- Grace on Couch to 5K
(10, 3, '2026-01-15 08:00:00', 28, 'ACTIVE'),  -- Iris on Marathon Prep
(2, 3, '2026-01-20 08:00:00', 25, 'ACTIVE'),   -- Alice on Marathon Prep
(3, 2, '2026-02-15 08:00:00', 12, 'COMPLETED'),-- Bob completed 10K program
(9, 3, '2026-01-10 08:00:00', 32, 'ACTIVE');   -- Henry on Marathon Prep
