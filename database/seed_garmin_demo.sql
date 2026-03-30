-- =============================================================
-- Garmin Demo Seed Data
-- Covers: G001–G007 (health metrics, Garmin tokens)
--         GC-001–GC-006 (coaching connections, comments)
-- Run AFTER seed.sql
-- =============================================================

-- =============================================================
-- 1. GARMIN-LINKED USERS
--    Simulate alice, iris, thomas, kate, sofia, david having
--    connected their Garmin device (mock OAuth1 tokens).
-- =============================================================

UPDATE users SET
  auth_provider        = 'GARMIN',
  provider_id          = 'garmin_2001',
  provider_access_token  = 'demo_access_token_alice',
  provider_token_secret  = 'demo_token_secret_alice'
WHERE username = 'alice_runner';

UPDATE users SET
  auth_provider        = 'GARMIN',
  provider_id          = 'garmin_2002',
  provider_access_token  = 'demo_access_token_iris',
  provider_token_secret  = 'demo_token_secret_iris'
WHERE username = 'iris_speed';

UPDATE users SET
  auth_provider        = 'GARMIN',
  provider_id          = 'garmin_2003',
  provider_access_token  = 'demo_access_token_thomas',
  provider_token_secret  = 'demo_token_secret_thomas'
WHERE username = 'thomas_marathon';

UPDATE users SET
  auth_provider        = 'GARMIN',
  provider_id          = 'garmin_2004',
  provider_access_token  = 'demo_access_token_kate',
  provider_token_secret  = 'demo_token_secret_kate'
WHERE username = 'kate_42k';

UPDATE users SET
  auth_provider        = 'GARMIN',
  provider_id          = 'garmin_2005',
  provider_access_token  = 'demo_access_token_sofia',
  provider_token_secret  = 'demo_token_secret_sofia'
WHERE username = 'sofia_peaks';

UPDATE users SET
  auth_provider        = 'GARMIN',
  provider_id          = 'garmin_2006',
  provider_access_token  = 'demo_access_token_david',
  provider_token_secret  = 'demo_token_secret_david'
WHERE username = 'david_ultra';

-- =============================================================
-- 2. HEALTH METRICS  (G006)
--    7 days of data per Garmin-linked athlete.
--    alice  → GREEN  (well recovered, peak fitness)
--    iris   → GREEN  (high performer, good sleep)
--    thomas → RED    (overtraining scenario)
--    kate   → YELLOW (moderate fatigue)
--    sofia  → GREEN  (mountain athlete, excellent HRV)
--    david  → YELLOW (heavy ultra load)
-- =============================================================

INSERT INTO health_metrics
  (user_id, date, resting_heart_rate, sleep_score, vo2_max, fitness_age,
   hrv_status, body_battery_max, stress_level, created_at)
SELECT
  u.id,
  (CURRENT_DATE - s.day_offset)::DATE,
  -- resting_heart_rate
  CASE u.username
    WHEN 'alice_runner'    THEN 52 - (s.day_offset % 3)
    WHEN 'iris_speed'      THEN 49 - (s.day_offset % 2)
    WHEN 'thomas_marathon' THEN 68 + s.day_offset       -- elevated = overtraining
    WHEN 'kate_42k'        THEN 58 + (s.day_offset % 4)
    WHEN 'sofia_peaks'     THEN 47 - (s.day_offset % 2)
    WHEN 'david_ultra'     THEN 62 + (s.day_offset % 3)
  END,
  -- sleep_score (0-100)
  CASE u.username
    WHEN 'alice_runner'    THEN 82 - s.day_offset
    WHEN 'iris_speed'      THEN 79 + (s.day_offset % 4)
    WHEN 'thomas_marathon' THEN 44 - s.day_offset       -- poor sleep = RED
    WHEN 'kate_42k'        THEN 63 + (s.day_offset % 5)
    WHEN 'sofia_peaks'     THEN 85 - (s.day_offset % 3)
    WHEN 'david_ultra'     THEN 55 + (s.day_offset % 6)
  END,
  -- vo2_max
  CASE u.username
    WHEN 'alice_runner'    THEN 56.2
    WHEN 'iris_speed'      THEN 61.8
    WHEN 'thomas_marathon' THEN 58.4
    WHEN 'kate_42k'        THEN 53.1
    WHEN 'sofia_peaks'     THEN 64.0
    WHEN 'david_ultra'     THEN 57.7
  END,
  -- fitness_age
  CASE u.username
    WHEN 'alice_runner'    THEN 28
    WHEN 'iris_speed'      THEN 25
    WHEN 'thomas_marathon' THEN 30
    WHEN 'kate_42k'        THEN 31
    WHEN 'sofia_peaks'     THEN 24
    WHEN 'david_ultra'     THEN 32
  END,
  -- hrv_status
  CASE u.username
    WHEN 'alice_runner'    THEN 'BALANCED'
    WHEN 'iris_speed'      THEN 'BALANCED'
    WHEN 'thomas_marathon' THEN 'POOR'
    WHEN 'kate_42k'        THEN 'UNBALANCED'
    WHEN 'sofia_peaks'     THEN 'BALANCED'
    WHEN 'david_ultra'     THEN 'UNBALANCED'
  END,
  -- body_battery_max (0-100)
  CASE u.username
    WHEN 'alice_runner'    THEN 84 - s.day_offset
    WHEN 'iris_speed'      THEN 88 - (s.day_offset * 2)
    WHEN 'thomas_marathon' THEN 32 - s.day_offset       -- depleted = RED
    WHEN 'kate_42k'        THEN 58 + (s.day_offset % 6)
    WHEN 'sofia_peaks'     THEN 90 - s.day_offset
    WHEN 'david_ultra'     THEN 46 + (s.day_offset % 5)
  END,
  -- stress_level (0-100)
  CASE u.username
    WHEN 'alice_runner'    THEN 22 + s.day_offset
    WHEN 'iris_speed'      THEN 18 + (s.day_offset * 2)
    WHEN 'thomas_marathon' THEN 72 + s.day_offset
    WHEN 'kate_42k'        THEN 38 + (s.day_offset % 7)
    WHEN 'sofia_peaks'     THEN 15 + s.day_offset
    WHEN 'david_ultra'     THEN 45 + (s.day_offset % 4)
  END,
  NOW()
FROM users u
CROSS JOIN (VALUES (0),(1),(2),(3),(4),(5),(6)) AS s(day_offset)
WHERE u.username IN ('alice_runner','iris_speed','thomas_marathon','kate_42k','sofia_peaks','david_ultra')
ON CONFLICT (user_id, date) DO NOTHING;

-- =============================================================
-- 3. COACHING CONNECTIONS  (GC-001)
--    emma_pace  coaches: alice_runner (FULL), iris_speed (FULL), kate_42k (BASIC)
--    carlos_boston coaches: thomas_marathon (FULL), david_ultra (BASIC)
-- =============================================================

INSERT INTO coaching_connections
  (coach_id, athlete_id, garmin_access_level, status, invite_token, created_at, updated_at)
VALUES
  -- emma_pace → alice_runner  (ACTIVE, FULL access)
  ((SELECT id FROM users WHERE username = 'emma_pace'),
   (SELECT id FROM users WHERE username = 'alice_runner'),
   'FULL', 'ACTIVE', NULL, NOW() - INTERVAL '30 days', NOW()),

  -- emma_pace → iris_speed  (ACTIVE, FULL access)
  ((SELECT id FROM users WHERE username = 'emma_pace'),
   (SELECT id FROM users WHERE username = 'iris_speed'),
   'FULL', 'ACTIVE', NULL, NOW() - INTERVAL '25 days', NOW()),

  -- emma_pace → kate_42k  (ACTIVE, BASIC access)
  ((SELECT id FROM users WHERE username = 'emma_pace'),
   (SELECT id FROM users WHERE username = 'kate_42k'),
   'BASIC', 'ACTIVE', NULL, NOW() - INTERVAL '14 days', NOW()),

  -- carlos_boston → thomas_marathon  (ACTIVE, FULL access)
  ((SELECT id FROM users WHERE username = 'carlos_boston'),
   (SELECT id FROM users WHERE username = 'thomas_marathon'),
   'FULL', 'ACTIVE', NULL, NOW() - INTERVAL '45 days', NOW()),

  -- carlos_boston → david_ultra  (ACTIVE, BASIC access)
  ((SELECT id FROM users WHERE username = 'carlos_boston'),
   (SELECT id FROM users WHERE username = 'david_ultra'),
   'BASIC', 'ACTIVE', NULL, NOW() - INTERVAL '20 days', NOW()),

  -- emma_pace → sofia_peaks  (PENDING — waiting for acceptance)
  ((SELECT id FROM users WHERE username = 'emma_pace'),
   (SELECT id FROM users WHERE username = 'sofia_peaks'),
   'FULL', 'PENDING', 'demo_invite_token_sofia_abc123', NOW() - INTERVAL '2 days', NOW());

-- =============================================================
-- 4. COACHING COMMENTS  (GC-006)
--    emma_pace leaves feedback on alice_runner's and iris_speed's activities.
--    carlos_boston leaves feedback on thomas_marathon's and david_ultra's activities.
-- =============================================================

INSERT INTO coaching_comments
  (coach_id, activity_id, content, rating, lap_number, pinned_to_athlete_dashboard, created_at)
VALUES

  -- emma → alice: Morning Long Run (activity 1)
  ((SELECT id FROM users WHERE username = 'emma_pace'), 1,
   'Excellent long run, Alice! Your pacing was very consistent in the first 15km. HR drift in the final 6km tells me you pushed slightly too hard on km 17 — back off 5 sec/km there next week. Overall 9/10 execution.',
   9, NULL, TRUE, NOW() - INTERVAL '6 days'),

  -- emma → alice: Tempo Run (activity 3)
  ((SELECT id FROM users WHERE username = 'emma_pace'), 3,
   'Good tempo effort. Lap 2 was your strongest — hold that rhythm for the full session next time. Make sure you''re hitting 4:05–4:10/km target, not pushing sub-4. Save that for race day.',
   8, 2, FALSE, NOW() - INTERVAL '4 days'),

  -- emma → iris: Sub-3 Goal Run (activity 28)
  ((SELECT id FROM users WHERE username = 'emma_pace'), 28,
   'Iris, this is exactly the kind of run that builds marathon fitness. 20km at 4:10 avg pace with a negative split in the second half is textbook sub-3 preparation. Your body battery trend looks great this week too.',
   10, NULL, TRUE, NOW() - INTERVAL '5 days'),

  -- emma → iris: VO2 Max Intervals (activity 29)
  ((SELECT id FROM users WHERE username = 'emma_pace'), 29,
   'Intervals looked sharp. Rep 3 and 4 were slightly off — I suspect fatigue from yesterday''s long run. Consider moving intervals to Wednesday next cycle, not the day after your 20km.',
   7, 3, FALSE, NOW() - INTERVAL '3 days'),

  -- carlos → thomas: Coach Demo Run (activity 15)
  ((SELECT id FROM users WHERE username = 'carlos_boston'), 15,
   'Thomas, I''m seeing elevated HR relative to your normal output this week. Your RHR is up 8bpm and sleep score dropped below 50 for three consecutive nights. I''m flagging this as overtraining risk — mandatory easy week. No workouts above Zone 2.',
   5, NULL, TRUE, NOW() - INTERVAL '2 days'),

  -- carlos → thomas: Marathon Pace Run (activity 17)
  ((SELECT id FROM users WHERE username = 'carlos_boston'), 17,
   'Before the overtraining concern — this marathon pace run was a 10/10. Perfect 4:55/km splits, HR ceiling respected. This is the fitness you need for Berlin. We just need to protect the body now.',
   10, NULL, FALSE, NOW() - INTERVAL '8 days'),

  -- carlos → david: Ultra Training (activity 12)
  ((SELECT id FROM users WHERE username = 'carlos_boston'), 12,
   'David — 42km training run logged. Your body battery hit 46 max today which is acceptable for this phase of the block. I want to see it recover above 60 before next Saturday''s back-to-back long runs. Prioritize sleep this week.',
   8, NULL, FALSE, NOW() - INTERVAL '3 days'),

  -- carlos → david: Recovery Ultra Shuffle (activity 13)
  ((SELECT id FROM users WHERE username = 'carlos_boston'), 13,
   'Good recovery shuffle. Keeping effort in Zone 1 was the right call. HRV status improving. Stay disciplined on the easy stuff — this is where ultra performance is actually built.',
   9, NULL, FALSE, NOW() - INTERVAL '2 days');

-- =============================================================
-- 5. TELEMETRY on existing activities  (G006 visual demo)
--    Add avg_heart_rate / max_heart_rate / elevation data
--    to alice's and iris's activities for richer coach feed.
-- =============================================================

UPDATE running_activities SET
  avg_heart_rate = 148, max_heart_rate = 167, elevation_gain_meters = 84
WHERE id = 1;  -- alice: Morning Long Run

UPDATE running_activities SET
  avg_heart_rate = 132, max_heart_rate = 155, elevation_gain_meters = 22
WHERE id = 2;  -- alice: Easy Recovery Run

UPDATE running_activities SET
  avg_heart_rate = 162, max_heart_rate = 175, elevation_gain_meters = 35
WHERE id = 3;  -- alice: Tempo Run

UPDATE running_activities SET
  avg_heart_rate = 174, max_heart_rate = 188, elevation_gain_meters = 15
WHERE id = 4;  -- alice: Track Intervals

UPDATE running_activities SET
  avg_heart_rate = 155, max_heart_rate = 172, elevation_gain_meters = 120
WHERE id = 28; -- iris: Sub-3 Goal Run

UPDATE running_activities SET
  avg_heart_rate = 178, max_heart_rate = 192, elevation_gain_meters = 28
WHERE id = 29; -- iris: VO2 Max Intervals

UPDATE running_activities SET
  avg_heart_rate = 168, max_heart_rate = 178, elevation_gain_meters = 45
WHERE id = 15; -- emma coached thomas: Coach Demo Run

UPDATE running_activities SET
  avg_heart_rate = 158, max_heart_rate = 170, elevation_gain_meters = 65
WHERE id = 17; -- thomas: Marathon Pace Run

UPDATE running_activities SET
  avg_heart_rate = 138, max_heart_rate = 162, elevation_gain_meters = 890
WHERE id = 12; -- david: Ultra Training

UPDATE running_activities SET
  avg_heart_rate = 122, max_heart_rate = 148, elevation_gain_meters = 310
WHERE id = 13; -- david: Recovery Ultra Shuffle
