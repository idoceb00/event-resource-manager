# Eventory

Internal management application for live-event companies that rent out
equipment. Eventory tracks events, equipment stock, performances, and
reservations, enforcing the core business rule that **no piece of equipment
can be reserved across overlapping events beyond its available stock**.

Built as a full-stack portfolio project with an emphasis on domain modelling
and business rules over CRUD scaffolding.

> **Status:** functional MVP, wired end-to-end (SvelteKit ↔ Spring Boot ↔
> PostgreSQL). Actively evolving — see [Roadmap](#roadmap).

---

## Table of contents

- [Overview](#overview)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Key design decisions](#key-design-decisions)
- [Getting started](#getting-started)
- [Project structure](#project-structure)
- [Roadmap](#roadmap)

---

## Overview

Companies that stage live events own a shared pool of equipment (sound,
lighting, structures, video) and commit units of it to events that run over
date ranges. The hard part isn't storing that data — it's guaranteeing that
the same physical units are never double-booked across events whose dates
overlap.

Eventory models this directly:

- **Events** have a date range representing the full equipment-occupation
  window (setup + performances + teardown).
- **Equipment** has a category, a catalogue status, and a stock count.
- **Performances** are child entities of an event (start time, duration,
  rehearsal time).
- **Reservations** commit a quantity of a given equipment to an event,
  authored by the user who created them.
- **Users** are the workforce; a user *is* an employee, with a role
  (administrator or employee) and an active/inactive lifecycle.

The central rule — overlapping-event stock availability — is enforced on the
server with open-interval semantics and concurrency-safe checks, and every
other feature is built around protecting it.

## Tech stack

**Backend** — Java 21 (LTS), Spring Boot, Spring Security, Spring Data JPA,
Maven. PostgreSQL in development, H2 in-memory for automated tests.

**Frontend** — SvelteKit (Svelte 5 runes), TypeScript, Tailwind CSS v4,
Paraglide.js (i18n), `adapter-static` (SPA — the backend is the single source
of truth).

**Infrastructure** — Docker Compose (PostgreSQL for local dev), GitHub Actions
CI (separate frontend/backend workflows).

## Architecture

A monorepo with a clear frontend/backend split. The backend applies Clean
Architecture principles pragmatically — logical package separation within a
single Maven module rather than physical hexagonal modules — layered as:

```
domain.model          → entities + enums (the domain)
domain.service        → pure domain logic (date-overlap detection) + exceptions
application.usecase   → transactional orchestration services
infrastructure.persistence → Spring Data JPA repositories
infrastructure.security    → session auth + authorization
infrastructure.web         → REST controllers + DTOs + error handling
```

The frontend is a pure SPA: it holds no business rules of its own and reacts
to the API's responses. The backend is designed so future clients (mobile,
integrations) could consume the same contract without changing business logic.

## Key design decisions

The point of this project is the *reasoning*, not the feature count. A few
decisions worth calling out:

- **The backend is the single source of truth for business rules.** The UI
  reacts to `400/403/404/409`; it never reimplements a rule client-side.
  Role-aware UI (hiding admin controls) is a convenience — the real boundary
  is the server's `403`.

- **Concurrency is chosen per problem, not applied uniformly.** Reserving
  equipment and reducing stock are check-then-write operations against a
  shared resource, so they take a `PESSIMISTIC_WRITE` lock. A plain stock
  increment is a single atomic `UPDATE`, which needs no lock. Using the right
  tool for each case is deliberate.

- **Overlap uses open-interval semantics** (`startA < endB && startB < endA`):
  an event ending exactly when another begins does not count as a conflict.
  Reducing stock or decataloguing equipment is blocked if it would drop
  availability below the *peak concurrent* quantity already committed to
  active/future reservations (computed with a sweep-line over overlapping
  events).

- **No physical deletion of users or equipment.** Both use a lifecycle flag
  (a user is deactivated, equipment is decatalogued) so that historical
  reservations keep their referential integrity. This mirrors the real domain:
  a seasonal technician rehired every year is *reactivated*, not recreated.

- **A user is an employee.** The workforce
  is the user table with a role. Reservations are authored by the
  authenticated session user, never by a client-supplied selector.

- **Frontend types align to the backend contract.**

- **Session-based authentication** (Spring Security, `HttpSession`) was chosen
  over JWT for the current single-instance scale — minimal moving parts, no
  token-revocation complexity. JWT is a documented future option if multiple
  instances or native clients arrive.

## Getting started

### Prerequisites

- Java 21 (Temurin/LTS)
- Node.js (LTS) + [pnpm](https://pnpm.io/)
- Docker + Docker Compose

### 1. Start the database

```bash
docker compose -f docker/docker-compose.yml up -d
```

### 2. Run the backend

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

> The `dev` profile is required locally: it serves the session cookie over
> plain HTTP. Without it the browser drops the cookie and login silently fails.

The API runs on `http://localhost:8080`.

### 3. Run the frontend

```bash
cd frontend
cp .env.example .env   # sets PUBLIC_API_BASE_URL
pnpm install
pnpm run dev
```

The app runs on `http://localhost:5173`.

### 4. Create a user

There is no public registration (user management is administrator-only), so
seed a first administrator directly in the database. Generate a BCrypt hash
for your password and insert:

```sql
INSERT INTO users (username, password_hash, name, role, active)
VALUES ('admin', '<bcrypt-hash>', 'Admin', 'ADMINISTRATOR', true);
```

> A development seeder that bootstraps a default administrator is on the
> roadmap.

## Project structure

```
event-resource-manager/
├── backend/     Spring Boot API (Java 21, Maven)
├── frontend/    SvelteKit SPA (Svelte 5, TypeScript)
└── docker/      Docker Compose (PostgreSQL for local dev)
```

## Roadmap

Shipped: session-based auth + role authorization, full equipment lifecycle
(bidirectional stock, decatalogue/recatalogue), events + performances (CRUD,
reservation-aware date updates, cascading deletion), reservations with the
overlap/stock rule, and administrator user management.

Planned:

- Automated tests for the security layer
- Flyway migrations (replacing `ddl-auto=update`)
- A development seeder for a bootstrap administrator
- Reduction of i18n to a single language
- Testcontainers for integration tests
- A full-stack Docker Compose for one-command deployment
- A logistical-feasibility rule (teardown + travel + setup time between
  consecutive events at different locations)

---

*Eventory is a personal portfolio project and a work in progress.*