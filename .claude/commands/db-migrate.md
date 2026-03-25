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
