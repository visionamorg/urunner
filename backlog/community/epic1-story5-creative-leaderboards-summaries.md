# Epic: Community Growth & Engagement
## Story: Creative - Dynamic Leaderboards & Activity Summaries

**As a** Community Admin
**I want** a dynamic leaderboard and AI-generated weekly activity summaries
**So that** I can boost member engagement and foster friendly competition within the community.

### Description
Once the core functionality is solid, this improvement will drive active engagement. Members love to see how they rank against their peers, and weekly summaries help users catch up on community events easily without scrolling through hundreds of chat messages.

### Acceptance Criteria
- [ ] Add a new "Leaderboard" tab in the community view showing top 10 runners (based on distance or time).
- [ ] Incorporate Weekly Challenges (e.g., "Run 50km this week") and visually show progress bars for all participating members.
- [ ] Implement a nightly/weekly cron or trigger to use Claude AI to summarize top discussions in the Chat or popular achievements into a "Weekly Digest" posted automatically to the Feed.
- [ ] Admins can adjust the leaderboard metric (Time / Distance / Elevation).
- [ ] Allow members to "Opt-out" of leaderboard tracking if they prefer privacy.

### Technical Notes for Claude
- Look into connecting the AI/GPT summarization tool (`activity-summrization_gpt.md`) with the `ChatService` data.
- You'll likely need a new backend scheduler (maybe Spring Boot `@Scheduled`) or just trigger it via admin action for MVP.
- Integrate the leaderboard with the existing user statistics API endpoints.
