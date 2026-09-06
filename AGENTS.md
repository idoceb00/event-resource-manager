# AGENTS.md
Instructions for AI coding agents working in this repository.

## Project
Eventory — an internal management application for live-event companies that rent out equipment. Monorepo: `backend/` (Spring Boot), `frontend/` (SvelteKit), `docker/` (local PostgreSQL).

Each subproject has its own `AGENTS.md` with stack-specific rules. Read the relevant one before making changes.

## Hard constraints
- **Never run `git` commands.** No `add`, `commit`, `push`, `checkout`,
  `merge`, or anything else. All version control is handled manually.
- **Never modify `temp/`.** It holds untracked legacy code from the original
  prototype. It is read-only reference material.
- **Stay inside the requested scope.** Do not refactor, rename, reformat, or
  "improve" code outside what was explicitly asked for.
- **Do not write tests unless asked.**
- **Do not add dependencies without being asked.** If a task seems to require one, say so and wait.

## Before changing existing code
Start with reconnaissance: read the actual files involved and confirm the current state before implementing. Do not assume how something works based on naming or on prior context — verify it in the code. If what you find contradicts the task description, stop and report the discrepancy instead of guessing.

## Architectural authority
- **The backend is the single source of truth for business rules.** The frontend reacts to HTTP status codes; it never reimplements a rule.
- **Prefer the simplest solution that solves the problem.** Additional complexity needs a concrete justification.
- **Follow existing patterns in the codebase** over introducing new ones. If an existing pattern seems wrong, raise it rather than silently diverging.

## Language
- All code, comments, commit messages, and documentation: **English**.
- Chat/discussion with the developer: **Spanish**.

## Commit messages
When asked to suggest one, use Conventional Commits: `feat:`, `fix:`,
`build:`, `docs:`, `test:`, `style:`, `refactor:`. 