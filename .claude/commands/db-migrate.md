---
description: Run a SQL migration against the local PostgreSQL database
argument: SQL statement or path to .sql file
---

Execute SQL against the RunHub database:

1. If "$ARGUMENTS" ends with `.sql`, read the file and use its contents
2. Otherwise, use "$ARGUMENTS" directly as the SQL statement
3. Run via: `docker compose exec postgres psql -U runhub -d runhub -c "<SQL>"`
4. Show the result
5. Warn if the SQL is destructive (DROP, DELETE, TRUNCATE) and confirm before running

## Logging (always run — even on error)
After the skill completes (success or failure), append one JSON line to `.claude/logs/agent-actions.jsonl`:
```bash
mkdir -p .claude/logs && python3 -c "
import json, datetime
entry = {
    'timestamp': datetime.datetime.utcnow().isoformat() + 'Z',
    'action': 'db-migrate',
    'agent': 'claude-sonnet-4-6',
    'target': '$ARGUMENTS',       # SQL statement or .sql file path
    'status': 'success',          # change to 'error' if psql returned an error
    'error': None,                # set to psql error output if status is 'error'
    'files_changed': [],          # set to ['.sql file path'] if a file was used
    'notes': ''                   # e.g. 'ALTER TABLE', 'CREATE INDEX', destructive=true
}
open('.claude/logs/agent-actions.jsonl', 'a').write(json.dumps(entry) + '\n')
"
```
