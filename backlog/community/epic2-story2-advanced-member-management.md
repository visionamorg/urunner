# Epic: Community Management & Growth
## Story: Feature - Advanced Member Management & Roles

**As a** Community Admin
**I want to** have advanced controls over my members (roles, tags, activity monitoring)
**So that** I can easily organize large groups, spot inactive runners, and reward the most active contributors.

### Description
As communities grow from 10 members to 1,000 members, the basic "Admin" and "Member" roles are not enough. Admins need customized tags/roles like "Coach", "Pacer", or "VIP" which appear as colorful badges next to member names in Chat and on the Feed. 
Furthermore, admins need an "Analytics/Activity" dashboard inside the Members Tab to see who hasn't logged a run in the last 30 days, or who is the most vocal in chat, empowering them to moderate or engage effectively.

### Acceptance Criteria
- [ ] Admins can create custom "Roles" or "Tags" (e.g., Pacer, Coach) with custom hex colors.
- [ ] Admins can assign these custom roles to members via the "Members" tab.
- [ ] Member tags are rendered prominently as badges next to their username in the Community Feed, Chat Rooms, and Leaderboards.
- [ ] Admins have an "Activity" view in the Members tab that shows "Last Run Logged Date" and "Messages Sent (30d)" for every member.
- [ ] Admins can batch-select inactive members (e.g., no run in 60 days) and send them a re-engagement notification or remove them.

### Technical Notes for Claude
- Update `CommunityMember.java` to support multiple roles, or create a new `CommunityTag` entity mapping to members.
- The default `ROLE` string may need to be expanded into an array or a secondary relationship.
- Write a repository query to aggregate member activity metrics (last activity date) to display in the admin view.
- Ensure the frontend UI (badges) uses standard RunHub aesthetic design tokens for premium styling.
