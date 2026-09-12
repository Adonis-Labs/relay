# Project Relay

A Spring Boot modular monolith (catalog module in progress: `Product`/`Sku` domain). This file is the durable, git-synced record of standing conventions and completed context for this repo — promoted from local Claude Code memory so it travels across machines.

## How the user wants to work with this codebase

This is a self-directed learning curriculum, tracked via Notion checklists (Java Learning Hub — Modules M8 "Spring Data JPA" and M16 "Database Migrations"; source tickets NX-82, NX-86, NX-23). Sessions are conducted in Hinglish.

**Teaching style (always follow when the user is learning a topic, not just asking for a fix):**
- Don't front-load full explanations, code, SQL, or exercises in one turn — that's lecturing, not teaching.
- Explain the core concept briefly, then tell the user what to try next (which file, which annotation/change) rather than writing it for them.
- Let them attempt it and show you before critiquing.
- Only write full code/SQL on their behalf if explicitly asked, or after they've attempted it and want a comparison.
- Go one sub-topic at a time in a multi-part lesson; don't jump ahead.
- At the end of a completed lesson, update the corresponding Notion page (Status → Done, checklist ticked, add a revision-notes section: tables/snippets/decision matrix + session-specific concerns) rather than leaving the summary only in chat.

## Repo facts worth knowing before making assumptions

- **ArchUnit is already set up** — `com.tngtech.archunit:archunit-junit5` in `build.gradle`, with custom rules `noCrossModuleEntityImports` and `noCrossSchemaJoins` in `com.example.relay.architecture.ArchitectureRules` (test fixtures under `com.example.archunitfixtures.*`). Grep before assuming it's absent.
- **Spring Boot 4.1**: `EntityManagerFactoryDependsOnPostProcessor` lives in `org.springframework.boot.jpa.autoconfigure`, not the old `org.springframework.boot.autoconfigure.orm.jpa` (Boot 4 split JPA autoconfig into its own module).
- **Schema-per-module**: migrations live under `db/migration/<module>/` (e.g. `db/migration/catalog/`), not a flat `db/migration/`. Catalog uses Option B — its own `Flyway` bean (`CatalogFlywayConfig`) with its own schema (`catalog`) and its own `flyway_schema_history`, chosen over a single shared bean/history to avoid cross-module version-sequence clashes and to support module-scoped Testcontainers later.
- **No cross-schema FKs/joins in migrations.** ArchUnit's `noCrossSchemaJoins` only catches this at the JPA-entity/bytecode level — it can't see raw `.sql` migration text. DB-level permissions or a CI SQL-lint are still an open gap for the migration layer itself.
- Shared/cross-module infra tables (e.g. Spring Modulith's `event_publication`) intentionally stay in the default/public schema.
- `sku_status` Postgres enum previously had lowercase labels vs. Java's uppercase `SkuStatus` constants (broke every insert) — fixed via `V2026.09.10.11.51.09__uppercase_sku_status_enum_labels.sql`. Verify it's still in place before assuming this needs redoing.
- `Product.skus` cascade is `{PERSIST, MERGE}` + `orphanRemoval = true` — `CascadeType.REMOVE` deliberately excluded (future Orders-module references to `Sku` would break on cascade delete; prefer soft-delete via `SkuStatus` instead).
- Product/Sku are intentionally flat (no JPA inheritance) — YAGNI decision, revisit only if real behavioral branching (not just data differences) emerges, e.g. a polymorphic `fulfill()` for physical vs. digital goods. If revisited, JOINED was the user's chosen default strategy.
- A hardcoded plaintext Aiven Postgres URL/user/password was previously found in `build.gradle`'s Flyway plugin block (never reached git history) — removed in favor of the Gradle Flyway plugin's default `flyway.conf` autoload, with `flyway.conf` gitignored. Watch for the same pattern recurring elsewhere (e.g. `application-local.properties`, already gitignored correctly).

## Migration/Flyway curriculum status (M16)

Completed, in order: Fundamentals → Migration Types (Versioned/Repeatable/Undo) → Running Flyway (startup/CLI/plugin/baseline-on-migrate) → Schema per Module → Migration Drift (checksum mismatch, `flyway repair`, failed migrations, out-of-order, manual drift). Repeatable-migration (`R__`) hands-on was explicitly skipped by user choice (covered conceptually only) — revisit only if brought up.

Key retained facts:
- Postgres rolls back a failed migration *and* its history-table write together (transactional DDL) — MySQL doesn't, leaving partial schema + a `success=false` row.
- `flyway repair` only fixes bookkeeping (checksums, failed-migration rows) — never touches schema.
- Out-of-order migrations throw by default; re-timestamp before merge rather than enabling out-of-order.
- Manual DB drift is invisible to `validate()` (no live-schema introspection) — reconciliation needs a manual migration + hand-edited history.

Next planned: NX-83 deliberate-breakage spike (checksum mismatch / failed migration / out-of-order, live on the catalog schema); Notion's "Rollback Strategy | Forward-Fix vs Undo Migrations" is a natural following lesson.

## JPA curriculum status (M8)

Completed: Inheritance mapping strategies (SINGLE_TABLE/JOINED/TABLE_PER_CLASS/@MappedSuperclass — see decision above), Validation (Bean/Schema/Flyway validation — see enum bug above), Cascade Types & Orphan Removal (see cascade config above).
