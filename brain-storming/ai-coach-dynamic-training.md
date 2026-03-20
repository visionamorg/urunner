Act as an expert Backend Developer and AI Integration Specialist. I am building a running community application, and I want to upgrade our current static training programs into a "Dynamic AI Coach".

I need you to write the code and provide the technical architecture for this feature. Standard apps give a 12-week static PDF-style plan. I want my users to receive dynamic, continuously adjusting training plans based on their actual performance, fatigue, and logged activities.

Please review the requirements below and generate the necessary logic structure and code.

1. Feature Overview & Concepts
Adaptive Workouts: If a user fails to hit their target pace on Tuesday, the AI Coach automatically adjusts Thursday's interval session to be slightly easier to aid recovery.
Fatigue & Load Tracking: Calculate a simple "Training Load" metric based on heart rate, duration, and pace.
Weekly Briefings: Generate a short, personalized text briefing every Sunday (e.g., "Great job hitting your mileage. Let's focus on speed this week.") using an LLM API (like OpenAI).

2. Technical & Architecture Constraints
Prompt Engineering: Design the exact system prompt to send to the LLM that includes the user's past 7 days of running data and asks for an adjusted 7-day future plan.
Scheduled Tasks: Use Spring's `@Scheduled` or Quartz to run a weekly batch job that evaluates all active users' progress and generates their new week's plan.
Database Schema: Explain how to store the generic training program template versus the personalized, AI-modified daily sessions (`user_program_progress`).

3. UI/UX Requirements
Coach Dashboard: An Angular component displaying the current week's plan. Highlight sessions that were modified by the AI with a glowing or distinct styling.
Feedback Loop: Allow users to rate how hard a session was (RPE scale 1-10) upon completion, feeding this back into the AI model.

4. Desired Output
Backend Logic: The Spring Boot service that gathers a user's weekly stats and formats the prompt for the LLM.
LLM Integration: The API call logic and the JSON schema response expected from the LLM.
Frontend Component: The Angular code for the weekly briefing and the 7-day adaptive calendar view.
