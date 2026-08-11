# Local development infrastructure

This folder contains the Docker Compose setup for local development
dependencies. Currently: PostgreSQL only.

## Setup

1. Copy the environment template and adjust if needed:

```bash
   cp .env.example .env
```

2. Start PostgreSQL:

```bash
   docker compose up -d
```

3. Verify it's running and healthy:

```bash
   docker compose ps
```

## Running the backend against it

From the repository root:

```bash
mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=dev
```

This activates `backend/src/main/resources/application-dev.yml`, which points
the datasource at `localhost:5432` using the credentials from `.env`.

## Notes

- The schema is currently created/updated automatically by Hibernate
  (`ddl-auto: update`). This is a temporary convenience while the domain
  model is still evolving. It will be replaced with versioned Flyway
  migrations once the schema stabilizes.
- Data persists across container restarts via the `postgres_data` named
  volume. To fully reset the database:

```bash
  docker compose down -v
```

## Scope

This compose file intentionally runs **only** the database. Backend and
frontend are run natively (`mvn spring-boot:run`, `pnpm dev`) during
day-to-day development, to keep hot-reload and debugger support. A separate
full-stack Docker Compose setup (backend + frontend + database, each
containerized) is planned for later, for demo/deployment purposes — not for
daily development.