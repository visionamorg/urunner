---
description: Mark a backlog story as done after implementation
argument: story file path (e.g. events/epic4-story1-interactive-gpx-routes)
---

Mark the specified backlog story as completed:

1. Read the file at `backlog/$ARGUMENTS.md`
2. Change all `- [ ]` checkboxes to `- [x]` in the Acceptance Criteria section
3. Add a `### Status: DONE` line and completion date at the top, right after the story title
4. Keep changes minimal — only update status markers, don't rewrite content
