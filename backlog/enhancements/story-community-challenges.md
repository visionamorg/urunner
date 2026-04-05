# Story: Community Challenges & Rewards

### Status: DONE

## 🎯 Goal
Drive user engagement and retention by allowing communities to set collective goals and reward their members for achieving them.

## 👤 User Story
`As a Community Member, I want to join the 'Casablanca 1000km Total' challenge so that I can contribute to my club's success and earn a unique badge.`

## 🛠️ Acceptance Criteria
- [ ] Admin UI: Community admins can create "Challenges" with a Start Date, End Date, and Target (Total Distance, Total Elevation, nr of runs).
- [ ] Member UI: A "Challenges" tab in the community view with a live **Progress Bar**.
- [ ] Analytics: Aggregate `running_activities` from all community members within the timeline.
- [ ] Badge System: Automatically award a "Challenge Finisher" badge to all participants when the goal is met.

## 🚀 Powerful Addition: "Inter-Community Varsity"
Allow two communities (e.g., "Casablanca Runners" vs "Rabat Runners") to go head-to-head. The community with the highest average distance per member at the end of the month wins a "Golden Runner" trophy for their page.

## 💡 Technical Strategy
1. Create a `challenges` table and a `challenge_participants` link table.
2. Use a PostgreSQL `SUM()` query grouped by `community_id` for the progress tracking.
3. Optimize performance by caching the challenge totals for 1 hour to prevent DB load.
