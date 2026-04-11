---
description: Stage all changes, commit with a descriptive message, and push to origin
argument: (optional) commit message — if omitted, auto-generate from changes
---

Commit and push current changes:

1. Run `git status` and `git diff --stat` to see what changed
2. If "$ARGUMENTS" is provided, use it as the commit message. Otherwise, generate a concise `feat:` / `fix:` / `refactor:` message from the diff.
3. Stage relevant files (avoid .env, credentials, large binaries)
4. Commit with the message + `Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>`
5. Push to origin on the current branch
6. Show the commit hash and push result

## Logging (always run — even on error)
After the skill completes (success or failure), append one JSON line to `.claude/logs/agent-actions.jsonl`:
```bash
mkdir -p .claude/logs && python3 -c "
import json, datetime
entry = {
    'timestamp': datetime.datetime.utcnow().isoformat() + 'Z',
    'action': 'push',
    'agent': 'claude-sonnet-4-6',
    'target': '$ARGUMENTS',       # commit message used (auto-generated or provided)
    'status': 'success',          # change to 'error' if commit/push failed
    'error': None,                # set to error message string if status is 'error'
    'files_changed': [],          # list every staged file path
    'notes': ''                   # e.g. commit hash after push
}
open('.claude/logs/agent-actions.jsonl', 'a').write(json.dumps(entry) + '\n')
"
```
Fill in the actual values: populate `files_changed` from `git diff --stat`, set `notes` to the commit hash, and set `status`/`error` if the push failed.
