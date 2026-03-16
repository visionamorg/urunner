-- RunHub Seed Data
-- Run AFTER schema.sql

-- ============================================================
-- USERS (password: "password123" - BCrypt hash)
-- 50 athletes across 7 categories
-- ============================================================

INSERT INTO users (username, email, password, first_name, last_name, bio, profile_image_url, role, location, running_category, passion, gender, years_running, weekly_goal_km, pb_5k, pb_10k, pb_half_marathon, pb_marathon, instagram_handle) VALUES

-- ---- ADMIN ----
('admin', 'admin@runhub.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Admin', 'User', 'RunHub administrator', 'https://i.pravatar.cc/300?u=admin', 'ADMIN', 'San Francisco, CA', 'ROAD', 'Building the best running platform for athletes everywhere.', 'Male', 8, 50, '18:30', '38:45', '1:24:00', '3:02:15', '@runhub_admin'),

-- ---- MARATHONERS ----
('alice_runner', 'alice@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Alice', 'Johnson', 'Marathon enthusiast. Running since 2018. Boston qualifier 2023!', 'https://i.pravatar.cc/300?u=alice_runner', 'USER', 'Boston, MA', 'MARATHON', 'I run marathons to prove to myself that limits are just suggestions.', 'Female', 6, 80, '19:45', '41:20', '1:32:10', '3:18:45', '@alice.runs'),
('iris_speed', 'iris@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Iris', 'Taylor', 'Sub-3 marathon goal. Training hard every day.', 'https://i.pravatar.cc/300?u=iris_speed', 'USER', 'Los Angeles, CA', 'MARATHON', 'Every mile is a gift. Chasing sub-3 is my obsession.', 'Female', 5, 90, '18:10', '38:30', '1:25:00', '3:01:48', '@iris.speed'),
('emma_pace', 'emma@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Emma', 'Davis', 'Certified running coach. Helping others reach their goals.', 'https://i.pravatar.cc/300?u=emma_pace', 'ORGANIZER', 'New York, NY', 'MARATHON', 'Coaching athletes to find their fastest selves, one mile at a time.', 'Female', 10, 70, '17:55', '37:20', '1:21:45', '2:58:30', '@emma.pace.coach'),
('kate_42k', 'kate@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Kate', 'Harrison', '6x marathon finisher. London, Berlin, Tokyo, Chicago, NYC, Boston. Chasing the Six Stars.', 'https://i.pravatar.cc/300?u=kate_42k', 'USER', 'London, UK', 'MARATHON', 'Six World Majors, one obsession. The finish line is just the beginning.', 'Female', 9, 85, '19:10', '40:05', '1:28:30', '3:12:00', '@kate42k'),
('thomas_marathon', 'thomas@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Thomas', 'Keller', 'Berlin Marathon champion. Precision pacing is my superpower.', 'https://i.pravatar.cc/300?u=thomas_marathon', 'USER', 'Berlin, Germany', 'MARATHON', 'A marathon is a race against yourself. The clock is just a mirror.', 'Male', 12, 100, '16:45', '34:55', '1:18:00', '2:46:22', '@thomas.runs.berlin'),
('sara_26miles', 'sara@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Sara', 'Gonzalez', 'Boston qualifier 5 years running. Passionate about negative splits.', 'https://i.pravatar.cc/300?u=sara_26miles', 'USER', 'Boston, MA', 'MARATHON', 'Start easy, finish strong. Negative splits every race.', 'Female', 8, 75, '18:50', '39:40', '1:29:15', '3:15:30', '@sara.negative.splits'),
('carlos_boston', 'carlos@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Carlos', 'Mendez', 'Running coach and 2:52 marathoner from Mexico City.', 'https://i.pravatar.cc/300?u=carlos_boston', 'USER', 'Mexico City, Mexico', 'MARATHON', 'El maratón es poesía en movimiento. 42.2km de pura pasión.', 'Male', 14, 110, '16:20', '33:45', '1:16:00', '2:52:10', '@carlos.pace.mx'),
('nina_chicago', 'nina@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Nina', 'Kowalski', 'Chicago native, marathon addict. Age group podium winner.', 'https://i.pravatar.cc/300?u=nina_chicago', 'USER', 'Chicago, IL', 'MARATHON', 'The city streets are my track. Chicago is where I found my stride.', 'Female', 7, 80, '19:30', '40:55', '1:31:00', '3:22:45', '@nina.chicago.runs'),
('james_roadking', 'james@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'James', 'Nakamura', 'Tokyo marathon veteran. Running the streets of Japan since 2014.', 'https://i.pravatar.cc/300?u=james_roadking', 'USER', 'Tokyo, Japan', 'MARATHON', 'Running taught me patience. The marathon is a 40K warmup and a 2K race.', 'Male', 11, 90, '17:30', '36:10', '1:20:30', '2:58:00', '@james.tokyo.runner'),
('olivia_berliner', 'olivia@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Olivia', 'Weber', 'Berlin-based runner. Targeting sub-3 at next Berlin Marathon.', 'https://i.pravatar.cc/300?u=olivia_berliner', 'USER', 'Berlin, Germany', 'MARATHON', 'Running through history. Berlin streets have the best marathon energy.', 'Female', 6, 70, '19:00', '40:20', '1:30:00', '3:08:15', '@olivia.runs.berlin'),
('michael_pace', 'michael@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Michael', 'Chen', 'Sydney Striders elite. 2:51 personal best, aiming for 2:45.', 'https://i.pravatar.cc/300?u=michael_pace', 'USER', 'Sydney, Australia', 'MARATHON', 'Australian heat training makes everyone else''s conditions feel easy.', 'Male', 9, 95, '16:55', '35:20', '1:17:45', '2:51:00', '@michael.pace.sydney'),

-- ---- TRAIL RUNNERS ----
('carol_trails', 'carol@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Carol', 'Williams', 'Trail runner. Mountains are my happy place.', 'https://i.pravatar.cc/300?u=carol_trails', 'USER', 'Vancouver, BC', 'TRAIL', 'Technical single track, mountain summits, and mud. This is where I belong.', 'Female', 7, 60, '20:15', '42:30', '1:33:45', '3:22:18', '@carol.trails.bc'),
('henry_ironman', 'henry@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Henry', 'Moore', 'Triathlete who found his true passion on mountain trails.', 'https://i.pravatar.cc/300?u=henry_ironman', 'USER', 'Austin, TX', 'TRAIL', 'I swim, bike, and run. But trails are where my heart stays.', 'Male', 9, 75, '19:45', '41:20', '1:31:00', '3:18:45', '@henry.tri.trails'),
('sofia_peaks', 'sofia@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Sofia', 'Brunner', 'Chamonix trail queen. UTMB finisher 2024 and 2025.', 'https://i.pravatar.cc/300?u=sofia_peaks', 'USER', 'Chamonix, France', 'TRAIL', 'The Alps are my playground. Every summit earns its descent.', 'Female', 10, 80, '21:30', '46:00', '1:42:00', NULL, '@sofia.peaks.utmb'),
('alex_mountains', 'alex@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Alex', 'Porter', 'Boulder trail runner. Colorado mountain is my office.', 'https://i.pravatar.cc/300?u=alex_mountains', 'USER', 'Boulder, CO', 'TRAIL', 'Running at altitude teaches you that breathing is a privilege, not a right.', 'Male', 8, 65, '20:45', '44:10', '1:37:00', '3:28:00', '@alex.boulder.trails'),
('jake_ridgerunner', 'jake@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Jake', 'Sullivan', 'Ridge runner. I collect summit finishes like trophies.', 'https://i.pravatar.cc/300?u=jake_ridgerunner', 'USER', 'Medellin, Colombia', 'TRAIL', 'Every ridge has a story. I run to write mine.', 'Male', 6, 55, '22:00', '47:30', '1:45:00', NULL, '@jake.ridge.runner'),
('luna_trailfire', 'luna@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Luna', 'Vance', 'Queenstown trail fire. Skyrunning specialist, vertical km obsessed.', 'https://i.pravatar.cc/300?u=luna_trailfire', 'USER', 'Queenstown, New Zealand', 'TRAIL', 'Vertical kilometers are my currency. I buy them one step at a time.', 'Female', 8, 70, '19:55', '43:20', '1:38:30', NULL, '@luna.skyrunner.nz'),
('marco_summit', 'marco@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Marco', 'Ferrari', 'Italian trail runner. Dolomites are home.', 'https://i.pravatar.cc/300?u=marco_summit', 'USER', 'Innsbruck, Austria', 'TRAIL', 'In the mountains, the mind quiets and the legs speak.', 'Male', 11, 75, '21:10', '45:00', '1:40:15', '3:35:00', '@marco.dolomites.run'),
('zoe_wilderness', 'zoe@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Zoe', 'Ashby', 'Blue Ridge trail runner. Wilderness lover, ultralight gear nerd.', 'https://i.pravatar.cc/300?u=zoe_wilderness', 'USER', 'Asheville, NC', 'TRAIL', 'The wilder the trail, the more alive I feel.', 'Female', 5, 50, '22:30', '48:00', '1:46:00', NULL, '@zoe.wilderness.run'),
('ryan_singletrack', 'ryan@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Ryan', 'OBrien', 'Pacific Northwest trail specialist. Cascades and coast.', 'https://i.pravatar.cc/300?u=ryan_singletrack', 'USER', 'Bend, OR', 'TRAIL', 'Single track is an art form. I run it like I''m painting with my feet.', 'Male', 7, 65, '20:30', '43:45', '1:35:00', '3:20:00', '@ryan.singletrack.pnw'),
('ava_highaltitude', 'ava@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Ava', 'Reyes', 'High altitude specialist. Training at 3,600m in Quito.', 'https://i.pravatar.cc/300?u=ava_highaltitude', 'USER', 'Quito, Ecuador', 'TRAIL', 'I live where most people run out of air. Altitude is my advantage.', 'Female', 9, 70, '21:45', '46:30', '1:43:00', '3:42:00', '@ava.altitude.run'),
('leo_dirtroads', 'leo@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Leo', 'van der Berg', 'Cape Town trail runner. Table Mountain is my gym.', 'https://i.pravatar.cc/300?u=leo_dirtroads', 'USER', 'Cape Town, South Africa', 'TRAIL', 'Dirt roads lead to the most beautiful destinations.', 'Male', 6, 60, '21:00', '44:30', '1:38:00', '3:25:00', '@leo.tablemountain.run'),
('mia_rockpath', 'mia@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Mia', 'Larsen', 'Moab slickrock runner. Desert trails and canyon loops.', 'https://i.pravatar.cc/300?u=mia_rockpath', 'USER', 'Moab, UT', 'TRAIL', 'Red rock, blue sky, and the sound of footsteps on ancient stone.', 'Female', 8, 65, '20:50', '44:00', '1:39:30', NULL, '@mia.moab.runs'),

-- ---- SPRINTERS / TRACK ----
('bob_sprints', 'bob@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Bob', 'Smith', '5k and 10k specialist. Love track workouts.', 'https://i.pravatar.cc/300?u=bob_sprints', 'USER', 'Chicago, IL', 'SPRINT', 'I was born to run fast. Every second off the PB is a victory.', 'Male', 4, 60, '16:42', '34:15', '1:15:30', NULL, '@bob.sprints'),
('dasha_sprint', 'dasha@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Dasha', 'Volkov', '400m and 800m specialist. Track is life.', 'https://i.pravatar.cc/300?u=dasha_sprint', 'USER', 'Miami, FL', 'SPRINT', 'Speed is an art form. I train to be the fastest version of myself.', 'Female', 7, 55, '15:45', '33:20', '1:12:00', NULL, '@dasha.sprint'),
('marcus_flash', 'marcus@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Marcus', 'Johnson', 'Atlanta track star. Competitive 5K and 10K.', 'https://i.pravatar.cc/300?u=marcus_flash', 'USER', 'Atlanta, GA', 'SPRINT', 'The 5K is the purest test of speed and guts. I love the pain.', 'Male', 5, 65, '15:30', '32:45', '1:11:30', NULL, '@marcus.flash.atl'),
('petra_quick', 'petra@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Petra', 'Lindqvist', 'Swedish national team hopeful. 1500m and 5000m track specialist.', 'https://i.pravatar.cc/300?u=petra_quick', 'USER', 'Stockholm, Sweden', 'SPRINT', 'Nordic winters make summer racing feel like flying.', 'Female', 9, 70, '14:55', '31:30', '1:09:45', NULL, '@petra.quick.sweden'),
('ben_fastrack', 'ben@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Ben', 'Tanner', 'Texas speed machine. High school track coach and elite 10K runner.', 'https://i.pravatar.cc/300?u=ben_fastrack', 'USER', 'Houston, TX', 'SPRINT', 'Speed is built in the dark at 5am. Races are just the celebration.', 'Male', 12, 80, '15:10', '32:00', '1:10:15', '2:48:30', '@ben.fastrack.tx'),
('kim_swift', 'kim@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Kim', 'Yoon', 'Korean national 3000m steeplechase and 5K athlete.', 'https://i.pravatar.cc/300?u=kim_swift', 'USER', 'Seoul, South Korea', 'SPRINT', 'I run to honor every sacrifice my coaches made for me.', 'Female', 8, 75, '15:00', '31:45', '1:10:00', NULL, '@kim.swift.seoul'),
('nadia_trackkween', 'nadia@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Nadia', 'Beaumont', 'Paris track kween. 800m heart, 5K legs.', 'https://i.pravatar.cc/300?u=nadia_trackkween', 'USER', 'Paris, France', 'SPRINT', 'On the track, every lane is equal. It''s just you and the clock.', 'Female', 6, 60, '15:20', '32:10', '1:11:00', NULL, '@nadia.track.paris'),
('felix_400m', 'felix@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Felix', 'Campbell', 'Jamaican-born sprinter, UK based. Speed demon on roads and track.', 'https://i.pravatar.cc/300?u=felix_400m', 'USER', 'Kingston, Jamaica', 'SPRINT', 'Jamaica runs in my blood. Speed is my birthright.', 'Male', 10, 65, '14:30', '30:15', '1:08:00', '2:44:00', '@felix.400m.jam'),
('sam_speedster', 'sam@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Sam', 'Rivera', 'Phoenix road racer turned track convert. 5K specialist.', 'https://i.pravatar.cc/300?u=sam_speedster', 'USER', 'Phoenix, AZ', 'SPRINT', 'Desert heat is my secret weapon. I thrive when others wilt.', 'Male', 5, 60, '16:10', '33:45', '1:14:30', NULL, '@sam.speedster.az'),

-- ---- ULTRA RUNNERS ----
('david_ultra', 'david@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'David', 'Brown', 'Ultramarathon runner. 100-miler finisher. Hardrock veteran.', 'https://i.pravatar.cc/300?u=david_ultra', 'ORGANIZER', 'Denver, CO', 'ULTRA', 'The ultra mindset is simple: keep moving. The finish line always comes.', 'Male', 12, 120, '21:00', '44:30', '1:38:00', '3:28:45', '@david.ultra.co'),
('miles_beyond', 'miles@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Miles', 'Fenton', 'Flagstaff ultrarunner. 100-mile finisher x5. Badwater aspirant.', 'https://i.pravatar.cc/300?u=miles_beyond', 'USER', 'Flagstaff, AZ', 'ULTRA', 'I run beyond what is possible. Then I run further.', 'Male', 15, 130, '22:00', '47:00', '1:44:00', '3:40:00', '@miles.beyond.ultra'),
('endurance_eric', 'eric@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Eric', 'Haglund', 'Salt Lake ultra legend. Western States 100 finisher 2023.', 'https://i.pravatar.cc/300?u=endurance_eric', 'USER', 'Salt Lake City, UT', 'ULTRA', 'The Western States buckle represents 100 miles of humility and triumph.', 'Male', 13, 110, '21:30', '45:45', '1:41:00', '3:35:00', '@eric.endurance.ws'),
('karen_100miler', 'karen@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Karen', 'Sato', 'San Francisco ultra mama. Mother of 3, 100-miler, life optimist.', 'https://i.pravatar.cc/300?u=karen_100miler', 'USER', 'San Francisco, CA', 'ULTRA', 'If I can raise 3 kids and run 100 miles, you can do anything.', 'Female', 11, 100, '22:30', '47:30', '1:45:00', '3:52:00', '@karen.100miler'),
('desert_pete', 'pete@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Pete', 'Rawson', 'Desert ultrarunner. Badwater 135 finisher and Sahara Race veteran.', 'https://i.pravatar.cc/300?u=desert_pete', 'USER', 'Tucson, AZ', 'ULTRA', 'The desert doesn''t forgive weakness. It just makes you stronger.', 'Male', 18, 140, '23:00', '49:00', '1:50:00', '3:58:30', '@desert.pete.ultra'),
('jungle_jamie', 'jamie@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Jamie', 'Torres', 'Jungle ultrarunner. Amazon Race and Borneo ultra finisher.', 'https://i.pravatar.cc/300?u=jungle_jamie', 'USER', 'Bogota, Colombia', 'ULTRA', 'Jungle running is survival with a race bib. Nature always wins, but you can finish.', 'Male', 9, 90, '22:45', '48:15', '1:47:30', '3:55:00', '@jungle.jamie.ultra'),
('nick_ultradog', 'nick@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Nick', 'Dlamini', 'Comrades Ultra veteran. 10x finisher of South Africa''s iconic race.', 'https://i.pravatar.cc/300?u=nick_ultradog', 'USER', 'Durban, South Africa', 'ULTRA', 'Comrades taught me: finish every race, no matter how long it takes.', 'Male', 16, 120, '22:15', '46:45', '1:43:30', '3:45:00', '@nick.comrades.za'),

-- ---- ROAD RACERS (5K-HM) ----
('frank_morning', 'frank@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Frank', 'Miller', 'Early morning runner. 5am club member. Road racing enthusiast.', 'https://i.pravatar.cc/300?u=frank_morning', 'USER', 'Portland, OR', 'ROAD', 'The 5am run is the best investment you''ll make all day.', 'Male', 3, 45, '20:30', '43:15', '1:35:30', NULL, '@frank.5am.run'),
('priya_roadracer', 'priya@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Priya', 'Sharma', 'London parkrun champion. Half marathon specialist targeting 1:28.', 'https://i.pravatar.cc/300?u=priya_roadracer', 'USER', 'London, UK', 'ROAD', 'Parkrun changed my life. Now I race everything I can find.', 'Female', 5, 55, '18:45', '39:30', '1:29:45', NULL, '@priya.road.racer'),
('nora_parkrun', 'nora@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Nora', 'Murphy', 'Dublin road runner. Parkrun course record holder at Phoenix Park.', 'https://i.pravatar.cc/300?u=nora_parkrun', 'USER', 'Dublin, Ireland', 'ROAD', 'Running in Ireland means running in the rain. And I love every second of it.', 'Female', 6, 50, '18:00', '37:45', '1:24:30', '2:59:45', '@nora.parkrun.dublin'),
('owen_halfking', 'owen@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Owen', 'Richards', 'Melbourne half marathon king. Age group winner, 1:22 PB.', 'https://i.pravatar.cc/300?u=owen_halfking', 'USER', 'Melbourne, Australia', 'ROAD', 'The half marathon is the perfect race — long enough to be epic, short enough to hurt right.', 'Male', 8, 65, '17:55', '37:10', '1:22:00', '2:55:30', '@owen.halfking.mel'),
('isla_tempo', 'isla@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Isla', 'MacGregor', 'Edinburgh road racer. Tempo runs and hill reps specialist.', 'https://i.pravatar.cc/300?u=isla_tempo', 'USER', 'Edinburgh, UK', 'ROAD', 'Scotland''s hills are the best running coaches money can''t buy.', 'Female', 5, 55, '19:15', '40:30', '1:30:15', '3:10:00', '@isla.tempo.scotland'),
('felix_road', 'felix.road@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Felix', 'Romero', 'Barcelona road warrior. 10K city race fanatic.', 'https://i.pravatar.cc/300?u=felix_road', 'USER', 'Barcelona, Spain', 'ROAD', 'Barcelona''s beachfront road is the most beautiful running route in the world.', 'Male', 4, 50, '18:20', '38:10', '1:26:30', NULL, '@felix.road.bcn'),

-- ---- CASUAL / FITNESS ----
('grace_newbie', 'grace@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Grace', 'Wilson', 'Just started running 3 months ago. Loving every step!', 'https://i.pravatar.cc/300?u=grace_newbie', 'USER', 'Seattle, WA', 'CASUAL', 'Three months ago I couldn''t run to the mailbox. Now I''m running 5K. Progress is beautiful.', 'Female', NULL, 15, '38:10', NULL, NULL, NULL, '@grace.run.seattle'),
('pat_fitness', 'pat@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Pat', 'Collins', 'Running for fitness and mental health. 2 years, no plans to stop.', 'https://i.pravatar.cc/300?u=pat_fitness', 'USER', 'Denver, CO', 'CASUAL', 'Running is my therapy, my meditation, and my daily reset button.', 'Male', 2, 20, '28:45', '1:01:00', NULL, NULL, '@pat.fitness.run'),
('joy_jogger', 'joy@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Joy', 'Washington', 'Nashville jogger. Running for joy, not for trophies.', 'https://i.pravatar.cc/300?u=joy_jogger', 'USER', 'Nashville, TN', 'CASUAL', 'I don''t run to win. I run because moving my body makes my soul sing.', 'Female', 1, 15, '35:20', NULL, NULL, NULL, '@joy.jogs.nashville'),
('sam_weekend', 'sam_weekend@example.com', '$2a$10$b/zK1hJO8ulFz8LB1aziseYOghdLuEORodLfOlwPGK1Hl3x15G7FC', 'Sam', 'Parker', 'Weekend warrior. Monday-Friday office, Saturday-Sunday roads.', 'https://i.pravatar.cc/300?u=sam_weekend', 'USER', 'Minneapolis, MN', 'CASUAL', 'Weekends are for running. Weekdays are just waiting for weekends.', 'Male', 3, 20, '30:10', '1:05:30', '2:20:00', NULL, '@sam.weekend.warrior');

-- ============================================================
-- COMMUNITIES
-- ============================================================

INSERT INTO communities (name, description, creator_id, member_count) VALUES
('City Marathon Club', 'A community for marathon runners in the city. Weekly long runs, training plans, and race support.', 5, 6),
('Trail Blazers', 'For trail running enthusiasts. Explore mountains, forests, and off-road adventures together.', 14, 4),
('Morning Runners Squad', 'Early risers who love starting the day with a run. 5am-7am sessions every weekday.', 45, 5);

-- ============================================================
-- COMMUNITY MEMBERS
-- ============================================================

-- City Marathon Club (id=1) - creator is emma_pace (user 5 in new ordering... wait)
-- Let me use the actual user IDs based on insert order:
-- 1=admin, 2=alice_runner, 3=iris_speed, 4=emma_pace, 5=kate_42k
-- 6=thomas_marathon, 7=sara_26miles, 8=carlos_boston, 9=nina_chicago, 10=james_roadking
-- 11=olivia_berliner, 12=michael_pace, 13=carol_trails, 14=henry_ironman, 15=sofia_peaks
-- 16=alex_mountains, 17=jake_ridgerunner, 18=luna_trailfire, 19=marco_summit, 20=zoe_wilderness
-- 21=ryan_singletrack, 22=ava_highaltitude, 23=leo_dirtroads, 24=mia_rockpath
-- 25=bob_sprints, 26=dasha_sprint, 27=marcus_flash, 28=petra_quick, 29=ben_fastrack
-- 30=kim_swift, 31=nadia_trackkween, 32=felix_400m, 33=sam_speedster
-- 34=david_ultra, 35=miles_beyond, 36=endurance_eric, 37=karen_100miler, 38=desert_pete
-- 39=jungle_jamie, 40=nick_ultradog
-- 41=frank_morning, 42=priya_roadracer, 43=nora_parkrun, 44=owen_halfking, 45=isla_tempo, 46=felix_road
-- 47=grace_newbie, 48=pat_fitness, 49=joy_jogger, 50=sam_weekend

INSERT INTO community_members (community_id, user_id, role) VALUES
-- City Marathon Club
(1, 4, 'ADMIN'),   -- emma_pace (organizer)
(1, 2, 'MEMBER'),  -- alice_runner
(1, 3, 'MEMBER'),  -- iris_speed
(1, 5, 'MEMBER'),  -- kate_42k
(1, 14, 'MEMBER'), -- henry_ironman
(1, 25, 'MEMBER'); -- bob_sprints

INSERT INTO community_members (community_id, user_id, role) VALUES
-- Trail Blazers
(2, 13, 'ADMIN'),  -- carol_trails
(2, 14, 'MEMBER'), -- henry_ironman
(2, 34, 'MEMBER'), -- david_ultra
(2, 15, 'MEMBER'); -- sofia_peaks

INSERT INTO community_members (community_id, user_id, role) VALUES
-- Morning Runners Squad
(3, 41, 'ADMIN'),  -- frank_morning
(3, 25, 'MEMBER'), -- bob_sprints
(3, 47, 'MEMBER'), -- grace_newbie
(3, 4, 'MODERATOR'), -- emma_pace
(3, 14, 'MEMBER'); -- henry_ironman

-- ============================================================
-- EVENTS
-- ============================================================

INSERT INTO events (name, description, event_date, location, distance_km, price, max_participants, organizer_id, community_id) VALUES
('City Spring Marathon 2026', 'Annual city marathon through the heart of downtown. Fast, flat course. Boston qualifier!', '2026-04-15 07:00:00', 'City Hall Plaza, Downtown', 42.2, 75.00, 500, 4, 1),
('Trail Adventure 10K', 'Scenic trail run through the forest. Moderate difficulty with 400m elevation gain.', '2026-03-28 09:00:00', 'Riverside Trail Head, North Park', 10.0, 25.00, 100, 13, 2),
('5K Fun Run for Charity', 'Annual charity 5K supporting local youth running programs. All levels welcome!', '2026-04-05 08:30:00', 'Central Park Bandshell', 5.0, 15.00, 300, 4, NULL),
('Half Marathon Weekend', 'A beautiful half marathon along the coastline. Scenic views and post-race party!', '2026-05-10 08:00:00', 'Oceanfront Boardwalk', 21.1, 45.00, 200, 4, 1),
('Night Run - City Lights', 'Unique evening run through illuminated city streets. Headlamps required.', '2026-04-20 20:00:00', 'Theater District Start', 8.0, 20.00, 150, 4, 3);

-- ============================================================
-- EVENT REGISTRATIONS
-- ============================================================

INSERT INTO event_registrations (event_id, user_id, status) VALUES
(1, 2, 'REGISTERED'),  -- alice in spring marathon
(1, 14, 'REGISTERED'), -- henry in spring marathon
(1, 3, 'REGISTERED'),  -- iris in spring marathon
(1, 25, 'REGISTERED'), -- bob in spring marathon
(2, 13, 'REGISTERED'), -- carol in trail 10k
(2, 34, 'REGISTERED'), -- david in trail 10k
(2, 41, 'REGISTERED'), -- frank in trail 10k
(3, 47, 'REGISTERED'), -- grace in charity 5k
(3, 2, 'REGISTERED'),  -- alice in charity 5k
(3, 4, 'REGISTERED'),  -- emma in charity 5k
(4, 3, 'REGISTERED'),  -- iris in half marathon
(4, 2, 'REGISTERED'),  -- alice in half marathon
(5, 41, 'REGISTERED'), -- frank in night run
(5, 25, 'REGISTERED'); -- bob in night run

-- ============================================================
-- RUNNING ACTIVITIES (30 total)
-- ============================================================

-- Alice (user 2)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(2, 'Morning Long Run', 21.1, 115, '2026-03-15', 'River Path', 'Great half marathon training run! Felt strong throughout.'),
(2, 'Easy Recovery Run', 8.0, 52, '2026-03-13', 'Neighborhood Loop', 'Easy pace to recover from Sunday''s long run.'),
(2, 'Tempo Run', 12.0, 58, '2026-03-11', 'City Park', '4 miles at tempo pace, felt the burn!'),
(2, 'Track Intervals', 6.4, 35, '2026-03-09', 'High School Track', '8x800m intervals with 400m recovery.');

-- Bob (user 25)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(25, '5K Race Effort', 5.0, 21, '2026-03-15', 'City Park', 'Race simulation. Hit 4:12/km pace!'),
(25, 'Speed Work', 8.0, 40, '2026-03-12', 'Track', '10x400m at 5K pace with 90s recovery.'),
(25, 'Hill Repeats', 7.5, 48, '2026-03-10', 'Hilly Neighborhood', '8 hill repeats. Quads are burning!'),
(25, 'Easy Jog', 5.0, 32, '2026-03-08', 'Riverside Path', 'Active recovery day.');

-- Carol (user 13)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(13, 'Mountain Trail Run', 18.5, 185, '2026-03-14', 'North Mountain', 'Epic trail run with 1200m elevation. Stunning views!'),
(13, 'Forest Loop', 12.0, 105, '2026-03-11', 'Pine Forest Trail', 'Technical single track through the forest.'),
(13, 'Trail Easy Run', 8.0, 65, '2026-03-09', 'River Trail', 'Easy recovery pace on the trails.');

-- David (user 34)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(34, 'Ultra Training - Long Haul', 42.0, 360, '2026-03-10', 'Mountain Circuit', 'Back-to-back long run training for upcoming ultra.'),
(34, 'Recovery Ultra Shuffle', 15.0, 115, '2026-03-12', 'Canal Path', 'Easy ultra shuffle. Heart rate cap 140.'),
(34, 'Night Run', 10.0, 72, '2026-03-14', 'City Streets', 'Night training. Worked on pace consistency.');

-- Emma (user 4)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(4, 'Coach Demo Run', 10.0, 55, '2026-03-15', 'City Park', 'Running with clients, demonstrating proper form.'),
(4, 'Personal Training Run', 15.0, 82, '2026-03-13', 'Lake Trail', 'My personal training run. Feeling fit!'),
(4, 'Marathon Pace Run', 20.0, 108, '2026-03-09', 'Road Course', 'Marathon pace practice. Splits very consistent.');

-- Frank (user 41)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(41, '5am Club Morning Run', 8.0, 45, '2026-03-16', 'Neighborhood', 'Classic early morning run. Sunrise was beautiful!'),
(41, 'Pre-Work Run', 6.0, 35, '2026-03-14', 'River Path', 'Quick morning run before the office.'),
(41, 'Weekend Long Run', 16.0, 95, '2026-03-10', 'Country Road', 'Long run with the morning squad. Great group energy!'),
(41, 'Morning Hills', 9.0, 58, '2026-03-08', 'Hilly Route', 'Added some hills to the morning routine.');

-- Grace (user 47)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(47, 'My First 5K!', 5.0, 38, '2026-03-15', 'Central Park', 'Did my first 5K non-stop! So proud of myself!'),
(47, 'Easy Run Day 2', 3.0, 25, '2026-03-13', 'Neighborhood', 'Following the training plan. Keeping it easy.'),
(47, 'Run-Walk Intervals', 4.0, 35, '2026-03-11', 'Local Park', 'Run 2 min, walk 1 min. Building endurance.');

-- Henry (user 14)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(14, 'Brick Run (After Bike)', 10.0, 52, '2026-03-15', 'Triathlon Circuit', 'Off the bike, legs felt like jelly at first then found rhythm.'),
(14, 'Marathon Prep Run', 18.0, 98, '2026-03-12', 'Road Course', 'Marathon pace run. On track for spring race goal.'),
(14, 'Recovery Jog', 6.0, 40, '2026-03-10', 'Park Loop', 'Easy recovery day after swim/bike double session.');

-- Iris (user 3)
INSERT INTO running_activities (user_id, title, distance_km, duration_minutes, activity_date, location, notes) VALUES
(3, 'Sub-3 Goal Run', 20.0, 100, '2026-03-15', 'Road', 'Running at sub-3 marathon pace. Legs feel ready!'),
(3, 'VO2 Max Intervals', 8.0, 42, '2026-03-13', 'Track', '5x1000m at VO2 max pace. Brutal but necessary.'),
(3, 'Easy Aerobic Run', 12.0, 70, '2026-03-11', 'Park', 'Easy aerobic base building. Heart rate 130-140.'),
(3, 'Long Run', 32.0, 175, '2026-03-08', 'Road Course', '32K long run at marathon pace. Feeling confident!');

-- ============================================================
-- POSTS (social feed)
-- ============================================================

INSERT INTO posts (author_id, content, community_id) VALUES
(2, 'Just finished my longest run this year - 21K in 1:55! The marathon training is going well. Who else is training for City Spring Marathon? 🏃‍♀️', 1),
(13, 'The trails are incredible this time of year. Just got back from an 18.5K mountain run with 1200m of elevation. My legs are jelly but my soul is full. 🏔️', 2),
(47, 'HUGE milestone today: I ran my first ever 5K without stopping!! 3 months ago I could barely run for 5 minutes. If I can do it, you can too! 🎉', NULL),
(34, 'Ultra training update: 42K back-to-back training run complete. 6 hours on feet. The mental game is just as important as the physical. Stay strong everyone!', 1),
(41, '5am club crew was out again this morning! Nothing beats watching the sunrise while getting your miles in. Who wants to join us tomorrow morning?', 3),
(4, 'Coach tip of the day: Your easy runs should be EASY. If you can''t hold a conversation, you''re going too fast. 80% of your training should be in zone 1-2. Run slow to race fast! 🎯', 1),
(3, 'New week, new goals. 32K long run done at marathon pace. Sub-3 marathon is within reach. 8 weeks to go! 💪', 1),
(25, 'Speed work session: 10x400m at 5K pace. Average split: 89 seconds. Fastest I''ve ever run! The track work is paying off.', NULL),
(14, 'Triathlon training note: The transition from bike to run is a skill. Brick workouts are essential - your legs need to know what''s coming! Just finished a 40K bike + 10K run.', NULL),
(2, 'Trail running tip for beginners: Start with easy trails, shorten your stride on technical terrain, and ALWAYS tell someone where you''re going. Safety first!', 2);

-- ============================================================
-- COMMENTS
-- ============================================================

INSERT INTO comments (post_id, author_id, content) VALUES
(1, 4, 'Amazing pace Alice! You''re going to crush the marathon!'),
(1, 3, 'I''m training too! Let''s run together at some point 🏃‍♀️'),
(1, 34, 'Great work! Consistency is the key to marathon success.'),
(3, 2, 'This is so inspiring Grace! Keep it up! We''ve all been there.'),
(3, 4, 'What an achievement! You should be so proud. The first 5K is always the hardest.'),
(3, 41, 'Amazing!! Keep going! Before you know it you''ll be doing 10K!'),
(5, 14, 'Respect! 42K training run is no joke. What''s your ultra target?'),
(7, 2, 'Yes!! You''re going to do it! Sub-3 is totally within reach with your training.'),
(6, 47, 'This is exactly what I needed to read! I always feel like I''m running too slow.'),
(10, 13, 'Great tips! I''d add: waterproof shoes make a huge difference on wet trails.');

-- ============================================================
-- LIKES
-- ============================================================

INSERT INTO likes (post_id, user_id) VALUES
(1, 25), (1, 34), (1, 4), (1, 41), (1, 3),
(2, 2), (2, 34), (2, 41), (2, 14),
(3, 2), (3, 13), (3, 34), (3, 4), (3, 41), (3, 14), (3, 3),
(4, 2), (4, 14), (4, 3),
(5, 2), (5, 25), (5, 4), (5, 14),
(6, 2), (6, 25), (6, 47), (6, 14), (6, 3),
(7, 2), (7, 4), (7, 14),
(8, 25), (8, 41),
(9, 2), (9, 25), (9, 41),
(10, 34), (10, 14);

UPDATE posts SET likes_count = (SELECT COUNT(*) FROM likes WHERE post_id = posts.id);
UPDATE posts SET comments_count = (SELECT COUNT(*) FROM comments WHERE post_id = posts.id);

-- ============================================================
-- MESSAGES
-- ============================================================

INSERT INTO messages (sender_id, community_id, content) VALUES
(4, 1, 'Welcome everyone to the City Marathon Club chat! Use this to coordinate training runs and share updates.'),
(2, 1, 'Hey everyone! Is anyone doing a long run this Sunday? Looking for running partners around 6am.'),
(3, 1, 'I''m in for Sunday! Meet at the usual spot by the fountain?'),
(4, 1, 'Reminder: group tempo run on Tuesday at 6pm at City Park. All paces welcome!'),
(14, 1, 'Just signed up for the spring marathon - so excited! My goal is 3:30.'),
(13, 2, 'Trail run this Saturday at 8am! Meet at the North Park trailhead. Moderate 15K route.'),
(34, 2, 'I''ll be there! Should we bring poles for the steep sections?'),
(41, 2, 'Just getting into trail running - is this trail beginner friendly?'),
(13, 2, 'It has some technical sections but nothing too scary. Hiking shoes recommended!'),
(41, 3, 'Good morning squad! Anyone else joining us tomorrow at 5:30am? Fresh air and good vibes!'),
(25, 3, 'I''ll be there! Making it a habit this month.'),
(47, 3, 'I want to join but 5am feels so early... Is it worth it?'),
(41, 3, 'Grace - ABSOLUTELY worth it! You''ll feel amazing all day. We''ll meet you at the park entrance!');

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
-- USER BADGES
-- ============================================================

-- Alice (user 2)
INSERT INTO user_badges (user_id, badge_id) VALUES (2, 1), (2, 2), (2, 3), (2, 4);

-- Iris (user 3)
INSERT INTO user_badges (user_id, badge_id) VALUES (3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6);

-- Emma (user 4)
INSERT INTO user_badges (user_id, badge_id) VALUES (4, 1), (4, 2), (4, 3), (4, 4);

-- Carol (user 13)
INSERT INTO user_badges (user_id, badge_id) VALUES (13, 1), (13, 2), (13, 3), (13, 4);

-- David (user 34)
INSERT INTO user_badges (user_id, badge_id) VALUES (34, 1), (34, 2), (34, 3), (34, 4), (34, 5), (34, 6), (34, 12);

-- Frank (user 41)
INSERT INTO user_badges (user_id, badge_id) VALUES (41, 1), (41, 2), (41, 3), (41, 8);

-- Grace (user 47)
INSERT INTO user_badges (user_id, badge_id) VALUES (47, 1), (47, 2);

-- Henry (user 14)
INSERT INTO user_badges (user_id, badge_id) VALUES (14, 1), (14, 2), (14, 3), (14, 4);

-- Bob (user 25)
INSERT INTO user_badges (user_id, badge_id) VALUES (25, 1), (25, 2);

-- ============================================================
-- TRAINING PROGRAMS
-- ============================================================

INSERT INTO programs (name, description, level, duration_weeks, target_distance_km) VALUES
('Couch to 5K', 'Start from scratch and run your first 5K in 8 weeks. Perfect for absolute beginners with no running experience.', 'BEGINNER', 8, 5.0),
('10K Intermediate Builder', 'Already running 5K? Level up to 10K in 6 weeks with structured speed and endurance work.', 'INTERMEDIATE', 6, 10.0),
('Marathon Preparation', 'Complete 16-week marathon training plan from half-marathon base. Includes long runs, tempo work, and recovery.', 'ADVANCED', 16, 42.2);

INSERT INTO program_sessions (program_id, week_number, day_number, title, description, distance_km, duration_minutes) VALUES
(1, 1, 1, 'Run/Walk Introduction', 'Warm up 5 min walk, then alternate 60s run and 90s walk for 20 minutes, cool down 5 min walk', 2.5, 30),
(1, 1, 2, 'Rest Day', 'Active recovery - gentle stretching or yoga.', 0, 30),
(1, 1, 3, 'Run/Walk Session 2', 'Same as Day 1. Focus on breathing and easy conversational pace.', 2.5, 30),
(1, 1, 4, 'Rest Day', 'Rest or light cross-training.', 0, 0),
(1, 1, 5, 'Run/Walk Session 3', 'Third session of week 1.', 2.5, 30),
(1, 1, 6, 'Rest Day', 'Rest day. Hydrate well.', 0, 0),
(1, 1, 7, 'Easy Walk', 'Long easy walk 30-45 minutes.', 3.0, 40),
(1, 2, 1, 'Longer Run Intervals', 'Alternate 90s run and 2 min walk.', 3.0, 30),
(1, 2, 3, 'Run Intervals Day 2', '90s run / 2 min walk intervals.', 3.0, 30),
(1, 2, 5, 'Run Intervals Day 3', 'Final session of week 2.', 3.2, 32),
(1, 3, 1, 'Mixed Intervals', '2x (90s run, 90s walk, 3 min run, 3 min walk)', 3.5, 35),
(1, 3, 3, 'Mixed Intervals Day 2', 'Repeat week 3 pattern.', 3.5, 35),
(1, 3, 5, 'Mixed Intervals Day 3', 'Last mixed session.', 3.8, 38);

INSERT INTO program_sessions (program_id, week_number, day_number, title, description, distance_km, duration_minutes) VALUES
(2, 1, 1, 'Baseline 5K', 'Run 5K at comfortable pace.', 5.0, 35),
(2, 1, 2, 'Rest Day', 'Full rest or light stretching.', 0, 20),
(2, 1, 3, 'Easy 4K Run', 'Easy aerobic run.', 4.0, 28),
(2, 1, 4, 'Strength Cross Training', 'Squats, lunges, core work.', 0, 40),
(2, 1, 5, 'Tempo 3K', '1K warm up, 3K at comfortably hard pace, 1K cool down', 5.0, 30),
(2, 1, 6, 'Long Run 6K', 'Your first long run of the program.', 6.0, 45),
(2, 1, 7, 'Rest Day', 'Complete rest.', 0, 0);

INSERT INTO program_sessions (program_id, week_number, day_number, title, description, distance_km, duration_minutes) VALUES
(3, 1, 1, 'Easy Run', 'Easy aerobic 8K to start the program.', 8.0, 50),
(3, 1, 2, 'Rest or Cross Train', 'Cycling, swimming, or complete rest.', 0, 0),
(3, 1, 3, 'Medium Run + Strides', '10K easy with 4x100m strides at the end.', 10.5, 60),
(3, 1, 4, 'Rest', 'Complete rest. Stretch and foam roll.', 0, 20),
(3, 1, 5, 'Tempo Run', '2K warm up, 5K at threshold pace, 2K cool down', 9.0, 55),
(3, 1, 6, 'Easy 6K', 'Easy recovery run the day before long run.', 6.0, 40),
(3, 1, 7, 'Long Run', 'First long run: 18K at easy/marathon pace.', 18.0, 110);

-- ============================================================
-- USER PROGRAM PROGRESS
-- ============================================================

INSERT INTO user_program_progress (user_id, program_id, started_at, completed_sessions, status) VALUES
(47, 1, '2026-03-01 08:00:00', 6, 'ACTIVE'),   -- Grace on Couch to 5K
(3, 3, '2026-01-15 08:00:00', 28, 'ACTIVE'),   -- Iris on Marathon Prep
(2, 3, '2026-01-20 08:00:00', 25, 'ACTIVE'),   -- Alice on Marathon Prep
(25, 2, '2026-02-15 08:00:00', 12, 'COMPLETED'),-- Bob completed 10K program
(14, 3, '2026-01-10 08:00:00', 32, 'ACTIVE');   -- Henry on Marathon Prep
