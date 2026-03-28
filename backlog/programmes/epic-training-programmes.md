# Epic: Mastering the Program (RunHub AI Coach)

## 🎯 Vision
Transform RunHub from a tracking tool into a world-class training platform. Using "Adaptive Coaching," the platform will generate custom plans and modify them in real-time based on user performance, fatigue, and environmental conditions.

## 🚀 Core Pillars
1. **AI-Driven Personalization**: Plans built specifically for YOUR current fitness and YOUR future goals.
2. **Dynamic Scaling**: If you're tired or the weather is too hot, the AI adjusts the plan today, not tomorrow.
3. **Immersive Execution**: A mobile-first session experience that guides you through every interval.
4. **Program Marketplace**: A curated space for both AI-generated and Coach-verified programs.

## 📋 Features & User Stories

### 1. AI Training Plan Architect
**Story:** `As a runner, I want the AI to generate a 12-week marathon plan based on my Strava history and a target time.`
*   **Powerful Detail**: The AI analyzes the last 6 months of data (Pace/HR/Distance) to set realistic training zones (Z1-Z5).
*   **Deliverable**: A complete calendar of workouts injected into the `user_program_progress` table.

### 2. The Adaptive Coach (RAC)
**Story:** `As a runner, I want my next session to be easier if I failed to hit my target pace in the previous one.`
*   **Powerful Detail**: Implementing a **Fatigue-Detection Algorithm**. If the user's "Efficiency Factor" (Pace / HR) drops significantly, the AI suggests a rest day or a recovery run.
*   **Status**: High Priority.

### 3. Progressive Session View (Mobile)
**Story:** `As a runner, I want a detailed breakdown of my current session while running.`
*   **Powerful Detail**: A "Live Performance Index" that shows if the user is over-training (too fast for the zone) or under-training.
*   **UI Focus**: Large, high-contrast numbers for pace and heart rate, with clear audio cues for interval changes.

### 4. Marketplace: "The Community Lab"
**Story:** `As a community leader, I want to create a '10K Speedier' program for my members.`
*   **Powerful Detail**: Integration with `community_id`. Programs can be "Official community programs" with their own leaderboards and chat rooms.

## 🛠️ Data Model Enhancements
The `programs` and `program_sessions` tables will be extended with:
- `is_ai_generated`: Boolean.
- `target_hr_zone`: Integer (1-5).
- `adaptive_logic`: JSONB (storing rules for scaling).

## 💡 Powerful Suggestion: "Weather-Adaptive Training"
Automatically adjust target paces for a session based on the local temperature and humidity. A 4:00/km pace in 15°C is much easier than in 35°C. The AI should suggest a 4:10/km pace if a heatwave is detected in the user's location.

## 📚 Detailed Stories & Backlog
Explore the specific requirements for each component of the Training System:

- [AI Training Plan Architect](file:///Users/macbook/Documents/CODE/urunner/runhub/backlog/programmes/story-ai-plan-generator.md) — How the AI builds the plan.
- [Live Session Guide](file:///Users/macbook/Documents/CODE/urunner/runhub/backlog/programmes/story-live-session-guide.md) — The mobile UI for taking the run.
- [AI Adaptive Recovery](file:///Users/macbook/Documents/CODE/urunner/runhub/backlog/programmes/story-ai-adaptive-recovery.md) — Fatigue monitoring and auto-rest.
- [Elite Performance Dashboard](file:///Users/macbook/Documents/CODE/urunner/runhub/backlog/programmes/story-elite-performance-dashboard.md) — Fitness (CTL), Fatigue (ATL), and Form (TSB).
- [Community Programmes](file:///Users/macbook/Documents/CODE/urunner/runhub/backlog/programmes/story-community-led-programmes.md) — Group-based training.
- [Premium Monetization](file:///Users/macbook/Documents/CODE/urunner/runhub/backlog/programmes/story-premium-programme-monetization.md) — Charging for high-value plans.
- [Garmin Integration](file:///Users/macbook/Documents/CODE/urunner/runhub/backlog/Garmin/US-G005-push-training-plans-calendar.md) — External sync to hardware.
