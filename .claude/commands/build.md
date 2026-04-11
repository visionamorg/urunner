---
description: Build frontend and/or backend to check for errors
argument: (optional) "frontend", "backend", or "all" (default: all)
---

Build the project to verify compilation:

- If "$ARGUMENTS" is "frontend" or "all" or empty:
  Run `cd /Users/macbook/Documents/CODE/urunner/runhub/frontend && npx ng build --configuration=development 2>&1 | tail -5` and check for TypeScript errors (grep for "error TS"). Warnings (NG8107 etc.) are acceptable.

- If "$ARGUMENTS" is "backend" or "all" or empty:
  Run `docker compose build backend 2>&1 | tail -10` from the project root. If Docker isn't running, note it and skip.

Report: success/failure + any actual errors found.

## Logging (always run — even on error)
After the skill completes (success or failure), append one JSON line to `.claude/logs/agent-actions.jsonl`:
```bash
mkdir -p .claude/logs && python3 -c "
import json, datetime
entry = {
    'timestamp': datetime.datetime.utcnow().isoformat() + 'Z',
    'action': 'build',
    'agent': 'claude-sonnet-4-6',
    'target': '$ARGUMENTS' or 'all',   # frontend / backend / all
    'status': 'success',               # change to 'error' if build produced TS/Java errors
    'error': None,                     # set to first error line(s) if status is 'error'
    'files_changed': [],
    'notes': ''                        # e.g. 'frontend OK, backend skipped (Docker down)'
}
open('.claude/logs/agent-actions.jsonl', 'a').write(json.dumps(entry) + '\n')
"
```
