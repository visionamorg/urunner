# Community Features

This document covers the four major community features: Events, Calendar, Chat, and Private Rooms. All are accessible from the community detail page. The community admin controls all of them.

---

## Overview

| Tab | Members See | Admin Controls |
|-----|-------------|----------------|
| Feed | Posts, reactions, comments | Delete/pin any post |
| Events | List of community events, register | Create / edit / cancel events |
| Calendar | Monthly grid of events | Create events by clicking a day |
| Chat | Community-wide chat messages | Visible only to members |
| Rooms | Private rooms they belong to | Create/delete rooms, manage membership |
| Members | Member list | Kick, change roles |
| Invites | — (admin only) | Invite by username, cancel invites |
| Settings | — (admin only) | Edit name/description/Drive folder |

---

## 1. Events Tab

### What it does
A per-community list of running events. Members can view all events and their details. Admins can create, edit, and cancel events.

### Backend API

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/communities/{id}/events` | Public | List community events ordered by date |
| `POST` | `/api/communities/{id}/events` | Admin | Create a new community event |
| `PUT` | `/api/communities/{id}/events/{eid}` | Admin | Update event details |
| `DELETE` | `/api/communities/{id}/events/{eid}` | Admin | Cancel an event (soft cancel via `isCancelled` flag) |

### Event Model

```
Event {
  id, name, description, eventDate, location,
  distanceKm, price, maxParticipants, organizer,
  community, isCancelled, createdAt
}
```

The `isCancelled` field (Boolean, default `false`) is set to `true` when an admin cancels an event. Cancelled events remain visible with a "Cancelled" badge.

### Frontend Files
- `frontend/src/app/core/models/event.model.ts` — Added `isCancelled` and `UpdateEventRequest`
- `frontend/src/app/core/services/community.service.ts` — Added `getCommunityEvents`, `createCommunityEvent`, `updateCommunityEvent`, `cancelCommunityEvent`
- Events tab in `community-detail.component.html` — Shows event cards with date, location, distance, price, participant count

### DB Change
```sql
ALTER TABLE events ADD COLUMN IF NOT EXISTS is_cancelled BOOLEAN NOT NULL DEFAULT FALSE;
```

---

## 2. Calendar Tab

### What it does
A monthly grid calendar showing all community events. Click any day to see its events in a side panel. Admins can click any day and create a new event directly from the calendar.

### Backend API
No new endpoints. Consumes `GET /api/communities/{id}/events`.

### Calendar Logic
- Builds a 6×7 grid for the current month
- Days with events show colored dots (orange = active, red = cancelled)
- Selected day shows events in a detail panel below the grid
- Navigate with `<` / `>` buttons to change months
- Admin sees "Add Event" button on the selected day panel

### Frontend Files
- `frontend/src/app/features/communities/community-calendar/community-calendar.component.ts`
- `frontend/src/app/features/communities/community-calendar/community-calendar.component.html`
- Inputs: `communityId: number`, `isAdmin: boolean`
- Output: `eventCreated: EventEmitter<RunEvent>` — emits when an event is created, parent refreshes its list

---

## 3. Chat Tab

### What it does
Community-wide chat embedded directly inside the community detail page. Only members who have joined the community can see and send messages.

### Backend API

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/messages?communityId={id}` | Authenticated | Fetch community messages |
| `POST` | `/api/messages` | Authenticated | Send a message (`{ communityId, content }`) |

The `ChatController` now also accepts `roomId` as a query parameter (see Rooms below).

### UI
- WhatsApp-style bubbles: own messages right-aligned (orange gradient), others left-aligned (dark surface)
- Enter to send, Shift+Enter for newline
- Lazy-loaded on first tab activation

### Frontend Files
- `frontend/src/app/core/services/chat.service.ts` — Added `roomId` parameter to `getMessages`
- `frontend/src/app/core/models/message.model.ts` — Added `roomId` to `Message` and `SendMessageRequest`
- Chat tab in `community-detail.component.html`

---

## 4. Private Rooms Tab

### What it does
Private chat rooms inside a community. The admin creates rooms (e.g. "Women", "VIP Event", "Coaches"), assigns members, and each room has its own isolated chat thread. Non-members cannot see or message in private rooms.

### Backend API

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/communities/{id}/rooms` | Authenticated | List rooms the user can access |
| `POST` | `/api/communities/{id}/rooms` | Admin | Create a new room |
| `DELETE` | `/api/communities/{id}/rooms/{rid}` | Admin | Delete a room |
| `GET` | `/api/communities/{id}/rooms/{rid}/members` | Room member / Admin | List room members |
| `POST` | `/api/communities/{id}/rooms/{rid}/members` | Admin | Add a member `{ userId }` |
| `DELETE` | `/api/communities/{id}/rooms/{rid}/members/{uid}` | Admin | Remove a member |
| `GET` | `/api/messages?roomId={rid}` | Room member | Fetch room messages |
| `POST` | `/api/messages` | Room member | Send a message `{ roomId, content }` |

### Room Visibility Rules
- **Public rooms** (`isPrivate: false`): All community members can see and chat
- **Private rooms** (`isPrivate: true`): Only members assigned to the room can see it; admin sees all rooms regardless
- Sending a message to a private room you're not a member of returns a 400 error

### Data Model

```
Room { id, name, description, community, createdBy, isPrivate, createdAt }
RoomMember { roomId (PK), userId (PK), role, joinedAt }
```

Creator is auto-added as an `ADMIN` room member when the room is created.

### DB Changes
```sql
CREATE TABLE IF NOT EXISTS rooms (
    id           BIGSERIAL    PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    description  TEXT,
    community_id BIGINT       NOT NULL REFERENCES communities(id) ON DELETE CASCADE,
    created_by   BIGINT       NOT NULL REFERENCES users(id),
    is_private   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS room_members (
    room_id    BIGINT      NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id    BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role       VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    PRIMARY KEY (room_id, user_id)
);

ALTER TABLE messages ADD COLUMN IF NOT EXISTS room_id BIGINT REFERENCES rooms(id) ON DELETE SET NULL;
```

**Important:** Apply the `ALTER TABLE messages` migration before restarting the backend. Hibernate's `ddl-auto: update` will create the two new tables automatically but the FK column on the existing `messages` table must be added first.

### Security
The `SecurityConfig` rule `.requestMatchers("/api/communities/*/rooms/**").authenticated()` is inserted **before** the broad `.permitAll()` on `/api/communities/**`. This ensures room endpoints always require authentication.

### Frontend Files
- `frontend/src/app/core/models/room.model.ts` — `RoomDto`, `RoomMemberDto`, `CreateRoomRequest`
- `frontend/src/app/features/communities/community-rooms/community-rooms.component.ts/html/scss`
- Inputs: `communityId`, `isAdmin`, `members` (community member list for the add-member dropdown)

---

## Admin Controls Summary

| Action | Who | How |
|--------|-----|-----|
| Create event | Admin | Events tab → "New Event" button |
| Edit event | Admin | Events tab → pencil icon on event card |
| Cancel event | Admin | Events tab → event-busy icon on event card |
| Create event from calendar | Admin | Calendar tab → select day → "Add Event" |
| Create room | Admin | Rooms tab → "New Room" button |
| Delete room | Admin | Hover room in sidebar → trash icon |
| Add room member | Admin | Select room → manage icon → add member dropdown |
| Remove room member | Admin | Select room → manage icon → × on member chip |
| Kick community member | Admin | Members tab → person_remove icon |
| Change member role | Admin | Members tab → role dropdown |
| Invite member | Admin | Invites tab → username input |

---

## File Inventory

### New Backend Files
```
backend/.../rooms/model/Room.java
backend/.../rooms/model/RoomMemberId.java
backend/.../rooms/model/RoomMember.java
backend/.../rooms/dto/RoomDto.java
backend/.../rooms/dto/CreateRoomRequest.java
backend/.../rooms/dto/RoomMemberDto.java
backend/.../rooms/repository/RoomRepository.java
backend/.../rooms/repository/RoomMemberRepository.java
backend/.../rooms/service/RoomService.java
backend/.../rooms/controller/RoomController.java
backend/.../events/dto/UpdateEventRequest.java
```

### Modified Backend Files
```
backend/.../events/model/Event.java              — added isCancelled
backend/.../events/dto/EventDto.java             — added isCancelled
backend/.../events/repository/EventRepository.java — added findByCommunityId query
backend/.../events/service/EventService.java     — added community-scoped methods
backend/.../events/mapper/EventMapper.java       — no change (MapStruct auto-maps isCancelled)
backend/.../chat/model/Message.java              — added room FK
backend/.../chat/dto/MessageDto.java             — added roomId
backend/.../chat/dto/SendMessageRequest.java     — added roomId
backend/.../chat/mapper/MessageMapper.java       — added room.id → roomId mapping
backend/.../chat/repository/MessageRepository.java — added findByRoomId
backend/.../chat/service/ChatService.java        — added roomId path + privacy guard
backend/.../chat/controller/ChatController.java  — added roomId query param
backend/.../communities/controller/CommunityController.java — added 4 event endpoints
backend/.../config/SecurityConfig.java           — added rooms auth rule
```

### New Frontend Files
```
frontend/.../core/models/room.model.ts
frontend/.../community-calendar/community-calendar.component.ts
frontend/.../community-calendar/community-calendar.component.html
frontend/.../community-calendar/community-calendar.component.scss
frontend/.../community-rooms/community-rooms.component.ts
frontend/.../community-rooms/community-rooms.component.html
frontend/.../community-rooms/community-rooms.component.scss
```

### Modified Frontend Files
```
frontend/.../core/models/event.model.ts          — added isCancelled, UpdateEventRequest
frontend/.../core/models/message.model.ts        — added roomId
frontend/.../core/services/chat.service.ts       — added roomId param
frontend/.../core/services/community.service.ts  — added event + room methods
frontend/.../community-detail/community-detail.component.ts  — new tabs, services, methods
frontend/.../community-detail/community-detail.component.html — new tab buttons + panels
```
