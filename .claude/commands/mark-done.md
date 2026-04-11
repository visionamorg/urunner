---
description: Mark a backlog story as done after implementation
argument: story file path (e.g. events/epic4-story1-interactive-gpx-routes)
---

Mark the specified backlog story as completed:

1. Read the file at `backlog/$ARGUMENTS.md`
2. Change all `- [ ]` checkboxes to `- [x]` in the Acceptance Criteria section
3. Add a `### Status: DONE` line and completion date at the top, right after the story title
4. Keep changes minimal — only update status markers, don't rewrite content

## Logging (always run — even on error)
After the skill completes (success or failure), append one JSON line to `.claude/logs/agent-actions.jsonl`:
```bash
mkdir -p .claude/logs && python3 -c "
import json, datetime
entry = {
    'timestamp': datetime.datetime.utcnow().isoformat() + 'Z',
    'action': 'mark-done',
    'agent': 'claude-sonnet-4-6',
    'target': '$ARGUMENTS',       # story path that was marked done
    'status': 'success',          # change to 'error' if the file wasn't found or update failed
    'error': None,                # set to error message string if status is 'error'
    'files_changed': ['backlog/$ARGUMENTS.md'],
    'notes': ''
}
open('.claude/logs/agent-actions.jsonl', 'a').write(json.dumps(entry) + '\n')
"
```
