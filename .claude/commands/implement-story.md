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

## Logging (always run — even on error)
After the skill completes (success or failure), append one JSON line to `.claude/logs/agent-actions.jsonl`:
```bash
mkdir -p .claude/logs && python3 -c "
import json, datetime
entry = {
    'timestamp': datetime.datetime.utcnow().isoformat() + 'Z',
    'action': 'implement-story',
    'agent': 'claude-sonnet-4-6',
    'target': '$ARGUMENTS',
    'status': 'success',          # change to 'error' if build/implementation failed
    'error': None,                # set to error message string if status is 'error'
    'files_changed': [],          # list every file path that was created or edited
    'notes': ''                   # optional: short summary of what was implemented
}
open('.claude/logs/agent-actions.jsonl', 'a').write(json.dumps(entry) + '\n')
"
```
Fill in the actual values before running: set `status` to `'error'` and `error` to the exception/build error message if anything failed; populate `files_changed` with all touched paths; add a one-line `notes` summary.
