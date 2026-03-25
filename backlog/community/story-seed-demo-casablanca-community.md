# Epic: Community Management & Growth
## Story: Feature - Seed Demo Data for "UR Urban Runners Casablanca"

**As a** Presenter / System Admin
**I want to** have a database seeder that automatically provisions a fully populated demonstration community
**So that** I can reliably showcase the platform's capabilities with realistic, localized data without manual setup.

### Description
Create a designated seeder that safely injects standard demo data on application. This ensures the "UR Urban Runners Casablanca" community is always present, fully fleshed out with members, events, and feed posts for live demonstrations.

### Acceptance Criteria

#### 1. Community Creation
- **Name:** UR Urban Runners Casablanca
- **Description:** "Welcome to UR Urban Runners Casablanca! We are a passionate community of runners navigating the vibrant streets and breathtaking coastlines of Casa. Whether you're training for your next marathon or just looking for a weekend recovery run followed by Moroccan mint tea, you belong here. Lace up and let's go!"
- **Cover/Logo:** Assign some placeholder URL for a cover image representing Casablanca (e.g., Hassan II Mosque backdrop or a coastal running path).

#### 2. Members & Roles Configuration
Generate exactly 15 members with varying roles to populate the community list and leaderboard. Ensure they are injected into the database and linked to this community.
- **Admins:** Maryiam, Elmahdi, Siham
- **Crew Members (Moderators/Leaders):** Kenza, Abdelaziz, Hind
- **Regular Members:** Yasser, plus 8 additional generated user profiles (e.g., Youssef, Fatima, Tariq, Salma, Omar, Nadia, Karim, Amina).

#### 3. Events & Calendar
Seed at least 4-5 upcoming and past events representing a typical active Moroccan running club:
- **Event 1:** "Weekly Aïn Diab Coastal Long Run" (Recurring weekly, Distance: 15km)
- **Event 2:** "Medina Sunrise 5K & Beldi Breakfast" (Distance: 5km, Location: Old Medina, Price: 50 MAD for breakfast)
- **Event 3:** "Bouskoura Forest Trail Prep" (Distance: 20km, Location: Bouskoura Forest)
- **Event 4:** "Recovery Shakeout Run + Coffee" (Distance: 5km, Location: Maarif)
- **Event 5:** "Casablanca Night Run" (Distance: 10km, Location: Corniche)

#### 4. Feed Posts & Engagement
Populate the community feed with realistic posts from the members:
- **Admin Post (Elmahdi):** "Welcome to the new members! Don't forget our Aïn Diab run this Sunday. Bring water and good vibes! 🌊" (Pinned)
- **Crew Post (Kenza):** "The Bouskoura trail was amazing today. Check out these photos! 🌲🏃‍♀️" (Attach placeholder photo URLs)
- **Member Post (Yasser):** "Anyone doing an easy 5k around Anfa Park tomorrow morning? Looking for a running buddy!"
- Add randomized reactions (likes, fire emojis) and a few comments to these posts to make the feed look alive.