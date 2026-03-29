# RunHub: Detailed Project Functionality (Current State)

RunHub is a premium, high-performance running platform that blends activity tracking, social engagement, and AI-driven coaching into a single elite ecosystem. Below are the exhaustive details of the currently implemented features.

## 🔑 1. Authentication & Advanced User Profiles
*   **Secure Identity Management**: JWT-backed registration and login with robust security headers.
*   **Role Infrastructure**:
    *   **USER**: Core experience—logs runs, joins communities, tracks metrics.
    *   **ORGANIZER**: Community leadership—creates and manages clubs, organizes races, and moderates content.
    *   **ADMIN**: Platform oversight—manages users, system settings, and global leaderboards.
*   **Profile Personalization**: 
    *   Dynamic avatars (initials or uploaded photos).
    *   Biography sections and connected social media link integration.
    *   Public vs. Private stats visibility.
*   **Achievement Milestones**: Automated badge engine that awards recognition for consistency (e.g., "7-Day Streak"), performance ("Sub-20 5K"), and community contribution.

## 🏃 2. Activity Tracking & Performance Analytics
*   **Precision Manual Logging**: Capture runs with high-fidelity fields:
    *   Title, Date/Time, Distance (km), Duration (minutes).
    *   Location (Geographic/Name tagging) and Personal Notes for subjective feel.
*   **Auto-Metric Calculation**: Real-time pace calculation (min/km) and effort analysis.
*   **Health & Readiness Insights**:
    *   **Daily Readiness Score**: A specialized algorithm calculating a fitness level (1-100) with personalized recovery recommendations based on recent volume.
    *   **Volume Trend Monitoring**: Weekly/Monthly distance totals vs. historical averages.
*   **Gamified Consistency (Streaks)**: 
    *   Visual "Fire" indicators for daily activity.
    *   Current and Longest streak tracking.
    *   **Streak Freezes**: A safeguard mechanism allowing users to preserve their streak during rest or injury.
*   **RunPoints (RP)**: An economy system awarding **10 RP per kilometer**, used for ranking and unlocking future rewards.

## 🎨 3. The Export Studio (Social Media Powerhouse)
*   **Professional Poster Generator**: Transform any run into a high-quality social media graphic or video.
*   **Visual Formats**: 
    *   **1:1 (Square)** for Instagram/Twitter.
    *   **9:16 (Story)** for Reels/TikTok.
    *   **16:9 (Landscape)** for YouTube/Widescreen presentations.
*   **Dynamic Template Engine**: Over 15 professional themes including:
    *   **Newspaper ("The Daily Run")**: A vintage editorial layout.
    *   **Cyberpunk**: A glitch-styled, futuristic mission report.
    *   **Receipt**: A minimalist "Activity Receipt" showing stats as items.
    *   **VHS/Retro**: Nostalgic 90s aesthetic with scanlines.
    *   **Annual Wrapped**: A data-viz summary of yearly performance.
*   **Deep Customization**: 
    *   Collage support (up to 4 background images).
    *   Custom gradients, patterns, and filter controls.
    *   Branding position and size controls (UR Urban Runners stamp).
    *   Toggleable stat displays (Show/Hide specific metrics for aesthetic balance).
*   **Template Marketplace**: "Explore Community" tab to browse, vote for, and save trending designs created by other athletes.

## 🤝 4. Community & Social Ecosystem
*   **Dynamic Communities**: Create or join specialized groups (e.g., "Urban Runners Casablanca").
*   **Real-time Communication**: 
    *   **Chat Rooms**: Community-scoped instant messaging including system alerts for club milestones.
    *   **Discussion Feed**: Global and club-specific feeds with support for text posts, likes, and nested comments.
*   **Community Calendars**: Dedicated event timelines for clubs to schedule group runs and social gatherings.
*   **Discovery Engine**: Searchable directory of athletes and communities with distance / activity-based rankings.

## 🏆 5. Competitive & Events
*   **Event Management**: 
    *   Racing dashboard for discovering local marathons and community runs.
    *   One-click registration and attendee tracking.
*   **Advanced Leaderboards**: 
    *   Global and community-specific "Wall of Fame".
    *   Dynamic filtering by **Weekly, Monthly, and All-Time** rankings.
    *   Ranking type toggles: Top Distance vs. Top Performance.

## 🤖 6. AI-Driven Coaching & Training
*   **Training Plan Engine**: 
    *   Access to structured programmes (e.g., Couch to 5K, Half-Marathon Elite).
    *   Granular progress tracking (e.g., "Session 12 of 24 completed").
*   **AI Marathon Assistant**: A 24/7 chat-based coach capable of answering technical training questions, suggesting pacing strategies, and providing motivational advice based on user data.

## 🏗️ 7. Technical Excellence
*   **Spring Boot 3.2 Backend**: Highly scalable REST API with Java 21 features.
*   **Angular 17 Frontend**: Modern standalone component architecture with Angular Material 17.
*   **Adaptive Design**: Fully responsive UI tailored for both desktop power users and mobile-first athletes in the field.
*   **Data Visualization**: High-performance charting using **ApexCharts** for real-time telemetry.
