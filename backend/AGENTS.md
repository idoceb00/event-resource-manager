# AGENTS.md — Backend
Spring Boot 4.1.0 API on Java 21. See the root `AGENTS.md` for repo-wide rules; they apply here too.

## Package structure

```
com.idoceb00.eventory.backend
├── domain.model                 → JPA entities + enums
├── domain.service               → pure domain logic + domain exceptions
├── application.usecase          → @Service / @Transactional orchestration
├── infrastructure.persistence   → Spring Data JPA repositories
├── infrastructure.security      → SecurityConfig, UserDetailsService
├── infrastructure.web           → REST controllers
├── infrastructure.web.dto       → request/response DTOs
└── infrastructure.web.exception → @RestControllerAdvice + mapping
```

Clean Architecture applied pragmatically: logical package separation inside a single Maven module, not hexagonal modules. Dependencies point inward — `domain` must not import from `infrastructure`.

Entities double as JPA persistence models and domain model. This is a deliberate trade-off; do not introduce a separate persistence layer.

## Conventions
- **DTOs are Java records.** Entities are never exposed directly from controllers.
- **Mapping is manual.** No MapStruct — transparency over less boilerplate.
- **Enums are domain-pure**, with technical identifiers only. No display strings, no i18n; labels are resolved in the frontend.
- **Lombok is used selectively**, not applied reflexively to every class.
- **Business rules live in the domain/application layers**, never in controllers.
- **Errors map to HTTP in `@RestControllerAdvice`**, not in controllers. Throw a meaningful domain exception; do not reuse an unrelated one because it happens to produce the right status code.
- **Authorization is centralized** in `SecurityFilterChain` via `requestMatchers`. Do not add `@PreAuthorize`.

## Concurrency
Concurrency control is chosen per problem, not applied uniformly:

- **Check-then-write over shared state** (reservations, stock decrease, decatalogue) uses `PESSIMISTIC_WRITE` locking.
- **Plain increments** use a single atomic JPQL `UPDATE`; no lock needed.

Do not "standardize" these into one approach.

## Formatting
Spotless with google-java-format. Run `mvn spotless:apply` before finishing.
Do not reformat files unrelated to the current task.