-- ============================================================
-- MOROCCO RUNNING EVENTS CALENDAR 2026/27
-- Source: "Calendrier des Courses à Pieds au Maroc 2026/27"
-- by Mohamed AB (Révision 05)
-- Associated with community 4 (UR : Runners casablanca)
-- ============================================================

INSERT INTO events (name, description, event_date, location, distance_km, price, max_participants, organizer_id, community_id) VALUES

-- ── MARS 2026 ──────────────────────────────────────────────────────────────
('Course de la Paix',
 'Course de la paix à M''diq. Une course emblématique dans la ville côtière du nord du Maroc.',
 '2026-03-09 08:00:00', 'M''diq', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Course de Fès',
 'Course à travers le Riad principal de Fès. Parcours historique à travers la médina.',
 '2026-03-15 08:00:00', 'Fès, Riad Principal Jean Mari', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Casablanca Challenge - 1ère Édition',
 'Première édition du Casablanca Challenge à Hay Hassani. Un nouveau défi pour les coureurs casablancais!',
 '2026-03-29 08:00:00', 'Casablanca, Hay Hassani', 21.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── AVRIL 2026 ─────────────────────────────────────────────────────────────
('Trail des Coquillages et des Oueds',
 'Ultra trail légendaire à Oualidia. Plusieurs distances disponibles : 180km, 100km, 60km, 30km, 28km, 15km. Parcours côtier et sauvage entre lagunes et falaises.',
 '2026-04-04 06:00:00', 'Oualidia', 100.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('10km de Casa',
 'Course de 10km à travers les rues de Casablanca. Parcours urbain rapide et plat.',
 '2026-04-04 08:00:00', 'Casablanca', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Fun Run Benslimane',
 'Course conviviale à Benslimane. Ouverte à tous les niveaux.',
 '2026-04-05 09:00:00', 'Benslimane', 5.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Run Alize Sprint',
 'Sprint dans les environs de Moulay Yacoub, Fès. Parcours rapide et technique.',
 '2026-04-05 08:00:00', 'Fès, Moulay Yacoub', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Trail Tizi n''Tretten',
 'Trail en montagne sur le col de Tizi n''Tretten. Parcours technique avec dénivelé important.',
 '2026-04-05 07:00:00', 'Tizi n''Tretten', 25.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Run Dark Run',
 'Course nocturne le long de la corniche Ain Diab. Distances : 10km et 5km. Ambiance unique sous les lumières de la côte!',
 '2026-04-11 20:00:00', 'Casablanca, Ain Diab', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Semi Marathon de Berkane',
 'Semi-marathon à Berkane, dans la région de l''Oriental. Parcours à travers les plaines d''agrumes.',
 '2026-04-12 08:00:00', 'Berkane, Ain Hassani', 21.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Rabat Marathon',
 'Marathon de la capitale. Parcours le long des avenues principales et monuments historiques de Rabat.',
 '2026-04-12 07:00:00', 'Rabat', 42.2, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Nfiss Semi Run',
 'Course dans la vallée du Nfiss, près d''Ain El Ouadiane. Paysages sublimes de l''Atlas.',
 '2026-04-13 08:00:00', 'Ain El Ouadiane', 21.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Atlas Semi Run',
 'Semi-trail dans les montagnes de Tafraout. Parcours entre amandiers et formations granitiques.',
 '2026-04-19 07:00:00', 'Tafraout', 21.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Sur Trail Morocco',
 'Trail découverte dans la province d''Ifrane / Zagora. Paysages variés entre cèdres et désert.',
 '2026-04-13 07:00:00', 'Ifrane, Province de Zagora', 30.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── MI-AVRIL 2026 (14-19 avril) ────────────────────────────────────────────
('Berrad High Atlas Ultra Trail',
 'Ultra trail mythique dans le Haut Atlas, au départ d''Ouirgane. Distances : 130km, 100km, 60km, 45km, 21km. Dénivelé massif à travers les sommets de l''Atlas.',
 '2026-04-14 05:00:00', 'Ouirgane, Haut Atlas', 130.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Trail Moulay Yacoub',
 'Trail de 21km autour des sources thermales de Moulay Yacoub. Parcours vallonné avec vues sur la campagne fassi.',
 '2026-04-18 08:00:00', 'Moulay Yacoub', 21.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Tanger Challenge',
 'Défi sportif dans la zone franche de Tanger. Course urbaine avec vue sur le détroit de Gibraltar.',
 '2026-04-18 08:00:00', 'Tanger Free Zone', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── FIN AVRIL 2026 (21 avril - 5 mai) ──────────────────────────────────────
('Ultra Trail Maroc Massif',
 'Ultra trail dans les gorges du Dadès et Todra à Ouarzazate. Paysages désertiques spectaculaires et canyons vertigineux.',
 '2026-04-25 06:00:00', 'Ouarzazate, Gorges du Dadès', 100.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Run 6 Fatlawi',
 'Course à Bouknadel, près de Casablanca. Parcours le long de la forêt et du littoral.',
 '2026-04-26 08:00:00', 'Casablanca, Bouknadel', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Trail Ouest GR Atlasiya',
 'Trail à Marrakech avec départ de Sebraayoud. Parcours à travers les collines de l''ouest Atlasique.',
 '2026-04-26 07:00:00', 'Marrakech, Sebraayoud', 30.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── MAI 2026 ───────────────────────────────────────────────────────────────
('Trail Compositeur',
 'Trail dans la palmeraie de Marrakech. Un parcours unique à travers les palmiers et jardins historiques.',
 '2026-05-02 07:00:00', 'Marrakech, Palmeraie', 21.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Marathon de l''Afrique',
 'Marathon à Ouarzazate, la porte du désert. Parcours mythique entre kasbahs et paysages sahariens. Format marathon et trail roses du Dadès.',
 '2026-05-09 06:00:00', 'Ouarzazate', 42.2, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Semi Marathon Bouknadel',
 'Semi-marathon à Bouknadel, Casablanca. Parcours côtier avec ambiance festive.',
 '2026-05-09 08:00:00', 'Casablanca, Bouknadel', 21.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── JUIN 2026 ──────────────────────────────────────────────────────────────
('Marathon International de Nador',
 'Marathon international à Nador. Distances : 42km et 21km. Parcours le long de la lagune de Marchica.',
 '2026-06-13 07:00:00', 'Nador', 42.2, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Trail des Cèdres',
 'Trail à Ain Leuh dans la forêt de cèdres du Moyen Atlas. Parcours en pleine nature entre singes magots et cèdres centenaires.',
 '2026-06-13 07:00:00', 'Ain Leuh', 25.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Marathon Bay Big Tangier',
 'Marathon de la baie de Tanger. Parcours spectaculaire le long de la côte méditerranéenne avec vue sur l''Espagne.',
 '2026-06-14 07:00:00', 'Tanger, M''diq', 42.2, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('10km International de Casablanca',
 '10km international au cœur de Casablanca, départ du Twin Center. Course rapide et urbaine.',
 '2026-06-11 08:00:00', 'Casablanca, Twin Center', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Les Fontaines d''Akka',
 'Trail à Chefchaouen, la ville bleue. Parcours magique à travers les montagnes du Rif et les cascades d''Akchour.',
 '2026-06-21 07:00:00', 'Chefchaouen', 25.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Trail Anfa',
 'Course à Anfa Place, Casablanca. Parcours urbain premium dans le quartier d''affaires.',
 '2026-06-14 08:00:00', 'Casablanca, Anfa Place', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── JUILLET 2026 ───────────────────────────────────────────────────────────
('Moonlight Boulevard Ultra',
 'Ultra nocturne à Meknès, départ de Bab Mansour. Course unique sous les étoiles à travers la ville impériale.',
 '2026-07-04 21:00:00', 'Meknès, Bab Mansour', 50.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Run It Kénitra',
 'Course à Kénitra. Parcours à travers la forêt de Maâmora et les rives du Sebou.',
 '2026-07-04 07:00:00', 'Kénitra', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── AOÛT 2026 ──────────────────────────────────────────────────────────────
('Ultra Bouknadel',
 'Ultra marathon de Bouknadel, Casablanca. Course multi-étapes le long du littoral atlantique.',
 '2026-08-02 06:00:00', 'Casablanca, Bouknadel', 100.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Trail Moulay Abdellah',
 'Trail à El Jadida, Moulay Abdellah. Parcours historique entre la cité portugaise et les plages.',
 '2026-08-13 07:00:00', 'El Jadida, Moulay Abdellah', 25.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('TangerFilm Run',
 'Course pendant le festival TangerFilm. Distances : 14km et 7km. Ambiance festive et culturelle.',
 '2026-08-20 08:00:00', 'Tanger', 14.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Semi Marathon de Casablanca',
 'Le semi-marathon annuel de Casablanca. 21km à travers les grandes avenues et la corniche.',
 '2026-08-23 07:00:00', 'Casablanca', 21.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── SEPTEMBRE 2026 ─────────────────────────────────────────────────────────
('Marathon International de Casablanca',
 'Le grand marathon international de Casablanca. Distances : 5km, 10km, 21km, 42km. Le plus grand événement running du Maroc!',
 '2026-09-09 07:00:00', 'Casablanca', 42.2, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── NOVEMBRE 2026 ──────────────────────────────────────────────────────────
('Sahara Trail Zagora',
 'Trail saharien à Zagora. Aventure dans les dunes et les palmeraies du Drâa. Une expérience inoubliable dans le désert!',
 '2026-11-04 06:00:00', 'Zagora, Sahara', 50.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Le Grand Trail de la Vallée du Drâa',
 'Le Grand Trail de la Vallée du Drâa. 100km en 3 étapes ou 130km en 6 étapes. Traversée épique entre oasis, kasbahs et désert.',
 '2026-11-07 05:00:00', 'Zagora, Vallée du Drâa', 130.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Ultra 8 Morocco',
 'Ultra trail à Agadir. Parcours côtier et montagneux entre l''Anti-Atlas et l''océan Atlantique.',
 '2026-11-14 06:00:00', 'Agadir', 80.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── DÉCEMBRE 2026 ──────────────────────────────────────────────────────────
('Ultra Trail Dakhla',
 'Ultra trail multi-étapes à Dakhla. Course dans le Sahara occidental entre lagune et désert. Paysages à couper le souffle!',
 '2026-12-12 06:00:00', 'Dakhla', 100.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

-- ── JANVIER 2027 ───────────────────────────────────────────────────────────
('Course de Hay Mohammed',
 'Course de début d''année à Casablanca, Hay Mohammed. Bien commencer 2027 en courant!',
 '2027-01-03 08:00:00', 'Casablanca, Hay Mohammed', 10.0, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4),

('Marathon International de Marrakech',
 'Le mythique Marathon International de Marrakech. Distances : 42km et 21km. Parcours à travers la ville ocre, les jardins et les remparts.',
 '2027-01-31 07:00:00', 'Marrakech', 42.2, 0.00, NULL,
 (SELECT id FROM users WHERE username = 'alice_runner'), 4)

ON CONFLICT DO NOTHING;
