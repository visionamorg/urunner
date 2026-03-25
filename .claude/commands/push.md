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
