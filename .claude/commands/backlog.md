---
description: List all backlog stories with their status
---

Find and display all backlog stories:

1. Glob for all `.md` files under `backlog/`
2. For each file, read just the first 5 lines to get the title and status
3. Display a table with columns: Path | Title | Status (DONE if has "Status: DONE", else PENDING)
4. Keep output concise
