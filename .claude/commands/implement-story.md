---
description: Read a backlog story and implement it end-to-end
argument: story file path (e.g. events/epic4-story1-interactive-gpx-routes)
---

Implement the backlog story efficiently with minimal token usage:

1. Read the story file at `backlog/$ARGUMENTS.md`
2. Identify all acceptance criteria and technical notes
3. Plan the minimal set of changes needed (backend entity/dto/service/controller, DB, frontend model/service/component)
4. Implement all changes — follow existing patterns in the codebase (see CLAUDE.md)
5. Build the frontend (`npx ng build --configuration=development`) to verify no compile errors
6. After successful implementation, run `/mark-done $ARGUMENTS` to mark the story as complete

Rules:
- Minimize file reads — use Grep/Glob to find what you need, read only necessary sections
- Follow existing code patterns (standalone components, MapStruct mappers, Lombok DTOs)
- Hibernate ddl-auto:update handles new columns — no manual SQL migration needed
- Don't add unnecessary dependencies — prefer built-in APIs
- Keep responses concise — skip explanations unless there's an error
