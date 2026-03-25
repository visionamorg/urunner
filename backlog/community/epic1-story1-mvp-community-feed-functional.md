# Epic: Community MVP & Enhancements
## Story: MVP1 - Make Community Feed & Members Tab Functional Done

**As a** Community Member or Admin
**I want** the Community Feed and Members tab to be fully functional
**So that** I can engage with my community, view posts, and manage members appropriately (for MVP1 launch).

### Description
Currently, the basic community features are defined in the docs but need to be 100% functional for MVP1. Claude Code needs to ensure that:
1. **Feed Tab**: Users can see posts, add reactions, and comment. Admins can pin and delete posts.
2. **Members Tab**: Shows a complete list of members. Admins should be able to kick members or change their roles.
3. **Settings & Invites**: Admins should be able to invite users by username, cancel invites, and manage community settings (name/description).

### Acceptance Criteria
- [ ] Feed properly loads and paginates posts.
- [ ] Normal members can create posts and leave comments.
- [ ] Admins can delete any post.
- [ ] Members tab displays all current members with their roles.
- [ ] Admin can alter roles or remove members via the UI.
- [ ] The backend API for these actions must correctly validate permissions.

### Technical Notes for Claude
- Verify the frontend components in `frontend/src/app/features/communities/community-detail/` (specifically Feed and Member views).
- Refer to `docs/community-features.md` for role structures. Make sure `community.service.ts` has all the correct endpoints connected.
