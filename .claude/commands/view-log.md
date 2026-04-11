---
description: View the structured agent action log
argument: (optional) filter — "errors", "today", skill name (e.g. "push"), or number of last N entries (e.g. "20")
---

Display the agent action log at `.claude/logs/agent-actions.jsonl`:

1. Check the log file exists: `test -f .claude/logs/agent-actions.jsonl && echo exists || echo empty`
2. If the file doesn't exist, print "No log entries yet." and stop.
3. Parse and display entries using:
```bash
python3 -c "
import json, sys

arg = '$ARGUMENTS'.strip().lower()

with open('.claude/logs/agent-actions.jsonl') as f:
    entries = [json.loads(l) for l in f if l.strip()]

# Apply filter
if arg == 'errors':
    entries = [e for e in entries if e.get('status') == 'error']
elif arg == 'today':
    from datetime import datetime, timezone
    today = datetime.now(timezone.utc).date().isoformat()
    entries = [e for e in entries if e.get('timestamp', '').startswith(today)]
elif arg.isdigit():
    entries = entries[-int(arg):]
elif arg:
    entries = [e for e in entries if e.get('action') == arg]

if not entries:
    print('No matching log entries.')
    sys.exit(0)

# Pretty-print table
print(f'{'TIMESTAMP':<24} {'ACTION':<18} {'TARGET':<35} {'STATUS':<8} ERROR / NOTES')
print('-' * 110)
for e in entries:
    ts    = e.get('timestamp', '')[:19].replace('T', ' ')
    action = e.get('action', '')[:17]
    target = e.get('target', '')[:34]
    status = e.get('status', '')[:7]
    err    = e.get('error') or e.get('notes') or ''
    print(f'{ts:<24} {action:<18} {target:<35} {status:<8} {err}')

print()
print(f'Total: {len(entries)} entr{\"y\" if len(entries)==1 else \"ies\"}')
"
```

4. If "$ARGUMENTS" is not provided, show the last 30 entries.
5. Tip: pass `errors` to see only failed actions, `today` for today's activity, a skill name to filter by action type, or a number N to see the last N entries.
