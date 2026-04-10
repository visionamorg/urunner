# US-009 — AI Coach Chat UI

**Status:** [ ] Pending
**Priority:** 🔴 High

---

## Problem

The backend has an AI coach endpoint (`/api/ai/...`) but there is no dedicated chat UI for it. The AI coach is invisible to users — one of RunHub's strongest differentiators is completely unused in the frontend.

---

## Story

As a **runner**, I want to chat with an AI coach that knows my training history, so I can ask it questions about my training, get personalized advice, and receive structured workout suggestions.

---

## Acceptance Criteria

### AI Coach Page (`/ai-coach`)
- [ ] Dedicated page in the sidebar (icon: `smart_toy` or `psychology`)
- [ ] Chat interface: messages thread + input at bottom (same UX pattern as the existing Chat page)
- [ ] User message appears immediately (optimistic); AI response streams in or shows typing indicator
- [ ] Conversation is persisted per session (scroll up to see history)
- [ ] "New conversation" button clears the thread

### Context-Aware Responses
- [ ] The first message in each session automatically sends user context to the backend:
  - Recent 4-week activity summary (total KM, avg pace, longest run)
  - Current training zone (from Performance page: OPTIMAL / OVERREACHING / RECOVERY / DETRAINING)
  - Active training program (name + current week if enrolled)
  - Upcoming events (within 30 days)
- [ ] This context is injected into the system prompt, not shown to the user

### Quick Prompt Chips
- [ ] Chips above input for common questions:
  - "Analyze my last 4 weeks"
  - "Suggest a workout for today"
  - "Am I ready to race?"
  - "How do I improve my 10K pace?"
  - "What should I do on a recovery day?"
- [ ] Clicking a chip sends that message immediately

### Structured Workout Suggestions
- [ ] When the AI suggests a workout with structured steps, it renders a workout card (not plain text):
  - Step-by-step list (Warmup / Intervals / Cooldown)
  - "Add to Garmin Clipboard" button — pre-fills the Garmin workout builder with the suggested steps
- [ ] Backend detects if AI response contains a JSON workout block and sends it in a structured field

---

## Technical Notes

### Backend
- Existing `AiController` / `AiService` — verify endpoint and update to accept:
  - `POST /api/ai/chat` with body `{ message: string, context?: UserContext }`
  - Response: `{ reply: string, workout?: GarminWorkoutDto }`
- `UserContext` DTO: built by `AiContextService` that queries activity, performance, programs, events
- Optional: use streaming SSE response for typing effect (`text/event-stream`)

### Frontend
- New component `features/ai-coach/ai-coach.component.ts`
- Route: `/ai-coach`
- `AiService` in `core/services/ai.service.ts`
- Conversation stored in component state (no DB needed for chat history in MVP2)
- Workout card sub-component reuses Garmin step model from `garmin-clipboard`

---

## UI Layout

```
┌─────────────────────────────────────┐
│  AI Coach                    [New]  │
├─────────────────────────────────────┤
│                                     │
│  [system] Based on your last 4      │
│  weeks: 142km, avg pace 5:12...     │
│                                     │
│  [user]  Am I ready for a tempo?    │
│                                     │
│  [ai]  Yes! Your TSB is +8 which    │
│  means you're fresh. Here's a       │
│  suggested workout:                  │
│  ┌─────────────────────────────┐    │
│  │ 5x1km @ 4:30 pace           │    │
│  │ Warmup 10min | Cool 10min   │    │
│  │ [Add to Garmin Clipboard →] │    │
│  └─────────────────────────────┘    │
│                                     │
├─────────────────────────────────────┤
│ [Analyze my last 4 weeks] [Ready?]  │
│ [Suggest workout] [Recovery tips]   │
├─────────────────────────────────────┤
│  Type a message...          [Send]  │
└─────────────────────────────────────┘
```
