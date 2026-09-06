# AGENTS.md — Frontend

SvelteKit SPA (Svelte 5) in TypeScript. See the root `AGENTS.md` for repo-wide rules; they apply here too.

## Nature of this app

`adapter-static` in SPA mode — **no SSR**. Do not add server-side load functions, `+page.server.ts`, form actions, or anything requiring a Node
runtime. The backend is a separate service.

## The backend owns the rules

The frontend holds **no business rules of its own**. It calls the API and reacts to the response:

- `400` → validation feedback
- `401` → session expired, redirect to login
- `403` → not permitted
- `404` → not found
- `409` → conflict (e.g. overlapping reservation, duplicate name)

Never pre-validate a business rule client-side to "save a request", and never reimplement server logic. Role-aware UI (hiding admin controls) is a UX convenience, not a security boundary.

## Conventions

- **Svelte 5 runes** (`$state`, `$derived`, `$effect`, `$props`) — not the legacy store/`export let` API.
- **Types align to the backend contract.** Do not invent frontend-only domain concepts. A cosmetic rename layer is the exception, not the rule.
- **All API access goes through the service layer** (`apiXService` modules) and the shared HTTP client. No `fetch` calls scattered in components.
- **Every request sends `credentials: 'include'`** — auth is cookie-based.
- **Tailwind v4 utility classes** for styling. No component CSS frameworks, no ad-hoc global stylesheets.
- **User-facing strings go through Paraglide** (`m.key()`), never hardcoded in components.
- **Keep `{#each}` blocks keyed**, and keep form labels associated with their controls.

## Datetime

Backend uses `LocalDateTime` / `LocalTime`. The frontend uses native `datetime-local` / `time` inputs and converts in one shared place — do not
scatter format handling across components.

## Checks

Run `pnpm run lint`, `pnpm run format`, and `pnpm run check` (svelte-check)
before finishing. Do not reformat files unrelated to the current task.
