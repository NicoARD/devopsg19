# Contributing to Poplulation Tracking System

Thanks for helping improve the population reporting system!

## How to report a bug
1. Search existing Issues and Discussions to avoid duplicates.
2. Open a new issue using “Bug report” and fill in all required fields:
    - Affected component (e.g., SQL queries, API)
    - Report type and scope (e.g., Cities in Continent)
    - Exact input parameters (e.g., N=10, Continent=Asia)
    - Steps to reproduce
    - Expected vs Actual result
    - App version/commit, DB vendor/version, dataset/schema version
3. Include logs, SQL, and screenshots where possible (redact secrets).

Tip: For data accuracy bugs, please paste the SQL you expect and the SQL observed.

## How to request a feature
Use the “Feature request” template and provide:
- Problem to solve, proposed solution, alternatives
- Scope/affected reports
- Acceptance criteria

## Development setup
- Ensure you can run the app and seed the world dataset locally.
- Typical setup:
    - MySQL/MariaDB installed or via Docker
    - Load the world database (e.g., world.sql) or run migrations
    - Configure connection variables (e.g., host, port, user, password, database) via `.env` or app config
- Refer to the repository README for precise steps (or open a Discussion if unclear).

## Branching and PRs
- Create a feature branch from main: `feat/*`, `fix/*`, or `chore/*`
- Keep changes small and focused; include tests when possible
- Link issues with “Fixes #<issue>”
- Update documentation if behavior changes

## Coding and SQL standards
- Prefer parameterized queries; avoid string concatenation
- Keep SQL readable, with clear aliases and comments
- Use EXPLAIN and indexes for heavy queries
- Handle NULLs and edge cases (e.g., ties in rankings)
- For “Top N” queries, validate N and use ORDER BY + LIMIT consistently

## Testing
- Add tests for:
    - Sorting order (descending population)
    - Filtering by scope (continent/region/country/district/city)
    - Top N behavior and off-by-one errors
    - Aggregations (people in cities vs not in cities)
    - Language speaker counts and percentage of world population