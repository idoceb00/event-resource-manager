# Eventory

[![Backend CI](https://github.com/idoceb00/event-resource-manager/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/idoceb00/event-resource-manager/actions/workflows/backend-ci.yml)
[![Frontend CI](https://github.com/idoceb00/event-resource-manager/actions/workflows/frontend-ci.yml/badge.svg)](https://github.com/idoceb00/event-resource-manager/actions/workflows/frontend-ci.yml)
[![Status](https://img.shields.io/badge/status-in%20development-yellow)](#current-status)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![SvelteKit](https://img.shields.io/badge/SvelteKit-Svelte%205-FF3E00?logo=svelte&logoColor=white)](https://kit.svelte.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)

Internal management application for live-event companies that rent out equipment. Eventory tracks events, equipment stock, performances, and reservations, enforcing the core business rule that **no piece of equipment can be reserved across overlapping events beyond its available stock**.

Built as a full-stack portfolio project with an emphasis on domain modelling and business rules over CRUD scaffolding.

> **Status:** functional MVP, wired end-to-end (SvelteKit ↔ Spring Boot ↔ PostgreSQL), under active development. Not yet deployed and not
> feature-complete — see [Current status](#current-status) for what's solid and what's still rough.

---

## Table of contents

- [Overview](#overview)
- [Current status](#current-status)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Development workflow](#development-workflow)
- [Getting started](#getting-started)
- [Project structure](#project-structure)
- [Roadmap](#roadmap)

---

## Overview
Companies that stage live events own a shared pool of equipment (sound, lighting, structures, video) and commit units of it to events that run over date ranges. The hard part isn't storing that data, it's guaranteeing that the same physical units are never double-booked across events whose dates overlap.

Eventory models this directly:
- **Events** have a date range representing the full equipment-occupation window (setup + performances + teardown).
- **Equipment** has a category, a catalogue status, and a stock count.
- **Performances** are child entities of an event (start time, duration, rehearsal time).
- **Reservations** commit a quantity of a given equipment to an event, authored by the user who created them.
- **Users** are the workforce; a user *is* an employee, with a role (administrator or employee) and an active/inactive lifecycle.

The central rule: overlapping-event stock availability is enforced on the server with open-interval semantics and concurrency-safe checks, and every other feature is built around protecting it.

## Current status
Eventory is under active, iterative development, not a finished product. The core domain logic (the overlap/stock rule and everything protecting it) is the most mature part of the codebase; a few peripheral pieces are still rough while that work continues.

**Working end-to-end:** authentication & authorization, equipment lifecycle, events & performances, reservations with the overlap/stock rule, and administrator user management.

**Known limitations:**
- The equipment restock UI still sends a stale field name, so restocking from the UI is currently broken (fix in progress).
- The guard blocking reservations of decatalogued equipment throws a misleadingly-named exception.
- Not deployed yet, no automated tests for the security layer, and still on `ddl-auto=update` (Flyway migrations planned).

## Tech stack
**Backend** — Java 21 (LTS), Spring Boot 4.1.0, Spring Security, Spring Data JPA, Maven, Lombok (used selectively). PostgreSQL in development, H2 in-memory for automated tests.

**Frontend** — SvelteKit (Svelte 5 runes), TypeScript, Tailwind CSS v4, Paraglide.js (i18n), `adapter-static` (SPA — the backend is the single source of truth).

**Infrastructure** — Docker Compose (PostgreSQL for local dev), GitHub Actions CI (separate frontend/backend workflows).

## Architecture
A monorepo with a clear frontend/backend split. The backend applies Clean Architecture principles pragmatically, logical package separation within a single Maven module rather than physical hexagonal modules layered as:

```
domain.model          → entities + enums (the domain)
domain.service        → pure domain logic (date-overlap detection) + exceptions
application.usecase   → transactional orchestration services
infrastructure.persistence → Spring Data JPA repositories
infrastructure.security    → session auth + authorization
infrastructure.web         → REST controllers + DTOs + error handling
```

The frontend is a pure SPA: it holds no business rules of its own and reacts to `400/403/404/409` responses rather than reimplementing rules client-side. The backend is designed so future clients (mobile, integrations) could consume the same contract without changing business logic.

**Implementation notes:** overlap detection uses open-interval semantics (`startA < endB && startB < endA`), and reducing stock or decataloguing equipment is blocked if it would drop availability below the peak concurrent quantity already committed to active/future reservations. Users and equipment are never physically deleted, both use a lifecycle flag instead, so historical reservations keep their referential integrity.

## Development workflow
Architecture and business-rule decisions are made deliberately before any code is written. Implementation is AI-assisted (scoped, phase-by-phase prompts against a defined architecture, with the constraints in the `AGENTS.md` files), but every change is reviewed and committed manually; AI tooling is never allowed to run `git` commands.

Code quality is enforced locally and in CI:

- **[Lefthook](https://github.com/evilmartians/lefthook)** — pre-commit hooks run linting/formatting; pre-push hooks run tests and `svelte-check`.
- **Conventional Commits**, enforced via a PR title linter, with squash merges to `main`.
- **GitHub Actions** — separate frontend/backend CI workflows.

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

> The `dev` profile is required locally: it serves the session cookie over plain HTTP. Without it the browser drops the cookie and login silently fails.

The API runs on `http://localhost:8080`.

### 3. Run the frontend

```bash
cd frontend
cp .env.example .env   # sets PUBLIC_API_BASE_URL
pnpm install
pnpm run dev
```

The app runs on `http://localhost:5173`.

### 4. Log in

There is no public registration, user management is administrator-only. When the backend starts under the `dev` profile with an empty database, it seeds a default administrator automatically:

```
username: admin
password: admin
```

These credentials are **development-only** and are overridable via `application-dev.yml`. The seeder is disabled outside the `dev` profile and
does nothing if any user already exists.

### Configuration

The backend is configured through Spring profiles (`application.yml` plus `application-dev.yml`); values that differ per environment use
`${VAR:default}` placeholders so they can be overridden by environment variables at deploy time. The frontend uses a `.env` file (see `.env.example`), as is idiomatic for Vite/SvelteKit.

## Project structure

```
event-resource-manager/
├── backend/     Spring Boot API (Java 21, Maven)
├── frontend/    SvelteKit SPA (Svelte 5, TypeScript)
└── docker/      Docker Compose (PostgreSQL for local dev)
```

## Roadmap

Shipped: session-based auth + role authorization, full equipment lifecycle (bidirectional stock, decatalogue/recatalogue), events + performances (CRUD, reservation-aware date updates, cascading deletion), reservations with the overlap/stock rule, administrator user management, and a dev-profile seeder for local bootstrapping.

In progress / planned (see [Current status](#current-status) for the immediately visible issues):

- Fix the restock UI and the decatalogue-reservation exception
- Automated tests for the security layer
- Flyway migrations (replacing `ddl-auto=update`)
- Reduction of i18n to a single language
- Testcontainers for integration tests
- A full-stack Docker Compose for one-command deployment
- Deployment (Render + managed PostgreSQL)
- A logistical-feasibility rule (teardown + travel + setup time between
  consecutive events at different locations)

---

*Eventory is a personal portfolio project and a work in progress.*