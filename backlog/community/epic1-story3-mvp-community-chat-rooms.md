# Epic: Community MVP & Enhancements
## Story: MVP3 - Make Community Chat & Rooms Functional

**As a** Community Member or Admin
**I want** the Chat and Rooms tabs to be fully functional
**So that** I can engage with other members and communicate in private groups.

### Description
Before we launch MVP1, the communication features defined in `community-features.md` must be completed.
1. **Chat Tab**: WhatsApp-style global community chat. Only members can see and send messages.
2. **Rooms Tab**: Private rooms (e.g., "VIP Event", "Women"). Admins create rooms and assign members. Non-members cannot see or chat in these rooms.

### Acceptance Criteria
- [ ] Chat tab correctly displays messages with WhatsApp-style bubbles (own on right, others on left).
- [ ] Users can send a message using Enter, or add line breaks with Shift+Enter.
- [ ] Chat history is lazy-loaded on the first tab activation.
- [ ] Admins can create new private rooms and delete them from the UI.
- [ ] Admins can add or remove members to a room.
- [ ] If a user tries to access a room they are not part of, they get redirected or blocked (API throws 403/400).
- [ ] Chat functionality also works inside the private rooms isolated from the global chat.

### Technical Notes for Claude
- Verify that `ChatController` handles the `roomId` query parameter properly.
- Ensure `SecurityConfig` rules `requestMatchers("/api/communities/*/rooms/**")` are in the correct order.
- Verify `ChatService`, `MessageRepository` (`findByRoomId`), and frontend `community-rooms.component.ts` logic.
