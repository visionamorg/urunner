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
