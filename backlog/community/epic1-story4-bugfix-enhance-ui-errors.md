# Epic: Community MVP & Enhancements
## Story: BugFix & Enhancement - UI Polish & Error Handling

**As a** Community Admin
**I want** robust error handling and polished UI across all Community features
**So that** users don't encounter confusing errors and my community looks professional.

### Description
Once the functional blocks are complete, we need to address any lingering bugs and enhance the frontend before full MVP1 launch.
Specifically, making sure error messages are graceful and UI components scale correctly on smaller devices.

### Acceptance Criteria
- [x] Add loading skeletons or spinners while data loads in all 6 main tabs.
- [x] Implement friendly error toasts/alerts on backend failures (e.g. "Failed to kick member" instead of an unhandled exception).
- [x] Fix any layout breaking issues when viewing Community details on mobile devices (e.g. Chat input area not sticky or hidden).
- [x] Enhance Calendar UI color scheme to better match the RunHub unified styling tokens (use orange gradient for active dates, dark surface context).
- [x] If chat connection fails midway, auto-reconnect or show an "offline" status bar.

### Technical Notes for Claude
- Use core `ToastService` or similar pattern for error handling.
- Review CSS Grid/Flexbox in `community-calendar.component.scss` and `community-rooms.component.scss` to make them fully responsive.

### Implementation Notes (2026-04-05)
- **Loading skeletons**: Feed (spinner), Events (spinner), Members (5-row pulse skeleton), Leaderboard (5-row pulse skeleton), Programmes (3-card pulse skeleton) were already present. Added a 3-card pulse skeleton for the Challenges tab.
- **Error toasts**: Replaced all remaining `console.error()` and `alert()` calls with `toast.error()` across `community-detail.component.ts` (joinOrLeave, createPost, addComment, updateCommunity) and `community-rooms.component.ts` (loadRooms, loadMessages, sendMessage, deleteRoom, removeMember). ToastService injected into CommunityRoomsComponent.
- **Mobile chat layout**: Changed chat container from `h-[calc(100vh-280px)]` to `h-[calc(100dvh-320px)]` (uses `dvh` for dynamic viewport height on iOS/Android). Added `min-h-[360px]` guard. Changed input padding to `p-3 sm:p-4` and added `flex-shrink-0` to prevent it being compressed. Added SCSS utility class for future reference.
- **Calendar orange gradient**: Today's date cell now uses `bg-gradient-to-br from-amber-500 to-orange-500 text-white shadow-lg shadow-orange-500/30`. Selected day uses `bg-primary/15 border border-primary/40`. Event dots on today's date are white (`bg-white/80`), on other dates are `bg-amber-500`. Week view today circle uses the same gradient. Week view event chips use `bg-amber-500/20 text-amber-400`. Day/Agenda/SelectedDay event icons use `bg-gradient-to-br from-amber-500/20 to-orange-500/20` with `text-amber-500`.
- **Chat offline banner**: Was already implemented (`chatOffline` flag, `retryChatConnection()` method, offline banner with Reconnect button). Verified present and functioning.
- **Rooms mobile responsiveness**: Sidebar hides on mobile when a room is selected (shows full-width chat). Back arrow button added in room header (mobile only) to return to rooms list. Container height uses `dvh` same as chat tab.
