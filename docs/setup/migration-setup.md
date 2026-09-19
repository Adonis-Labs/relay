# Migration Setup (Flyway)

How Flyway is wired up in Relay, and what is still open. Status: **Gradle side in progress**.

## Two separate configs

Flyway runs in two places, and they read **different** config files:

| Where | Trigger | Config source |
|---|---|---|
| Spring app | `CatalogFlywayConfig` bean, runs `migrate()` on startup | `application-local.properties` (datasource) |
| Gradle plugin | `./gradlew flywayInfo / flywayMigrate / flywayClean` | `flyway.conf` in project root |

The Gradle plugin does **not** read Spring's `application*.properties`, and the app does not read `flyway.conf`.

## Files (all gitignored, hold secrets)

- `flyway.conf` (project root): DB url/user/password for Gradle tasks.
- `src/main/resources/application-local.properties`: datasource for `bootRun`.
- `src/test/resources/application-local.properties`: datasource for tests.

`spring.profiles.active=local` in `application.properties` loads the `-local` files automatically.

Use the same DB credentials in all three. Never hardcode them in `build.gradle` (an earlier plaintext Aiven URL was found there and removed).

## Current `flyway.conf` shape

```properties
flyway.url=jdbc:postgresql://<host>:<port>/<db>?ssl=require
flyway.user=<user>
flyway.password=<password>

# Module-specific (currently catalog only, to be removed, see "Open work")
flyway.schemas=catalog
flyway.defaultSchema=catalog
flyway.locations=filesystem:src/main/resources/db/migration/catalog
flyway.baselineOnMigrate=true
flyway.baselineVersion=2026.09.10.11.51.09
```

Notes:
- Use `filesystem:` in `locations`. Gradle plugin tasks don't see the app classpath, so `classpath:` won't work.
- `schemas`/`locations` must match `CatalogFlywayConfig`, otherwise Gradle and the app use different schemas/history tables.
- `flyway.cleanDisabled` defaults to `true`. `flywayClean` drops everything in the configured schemas, and the DB is remote (Aiven), so only enable it deliberately.

## Build wiring (`build.gradle`)

- Plugin `org.flywaydb.flyway` 13.5.0.
- `buildscript` classpath has `org.flywaydb:flyway-database-postgresql:13.5.0` (Postgres support for the plugin).
- App dependencies: `spring-boot-starter-flyway`, `flyway-database-postgresql`.
- `spring.flyway.enabled=false`: Boot auto-config is off, each module runs its own Flyway bean.

## Layout convention

- Migrations: `src/main/resources/db/migration/<module>/`
- Schema: one per module, same name as the module (`catalog`).
- Each module has its own `flyway_schema_history` (inside its schema).
- Shared infra tables (e.g. Modulith `event_publication`) stay in `public`.
- No cross-schema FKs/joins in migrations. ArchUnit can't catch this at SQL level, so it is still an open gap.

## Open work: no hardcoded schema/location (multi-module)

Goal: `flyway.conf` holds only credentials, and schema/location derive from the module name.

**Option 1 (planned first): `-Pmodule=<name>`**
`flyway { }` block in `build.gradle` reads `findProperty('module')` and derives:
- `schemas = [module]`
- `locations = ["filesystem:src/main/resources/db/migration/${module}"]`

```
./gradlew flywayInfo -Pmodule=catalog
./gradlew flywayMigrate -Pmodule=orders
```

**Option 2 (later, if modules grow): per-module tasks**
Loop over a module list in `build.gradle`, register a `FlywayInfoTask`/`FlywayMigrateTask` per module (`flywayInfoCatalog`, `flywayMigrateOrders`, ...). Allows running all modules at once.

Things to handle when doing this:
- Remove `schemas`, `defaultSchema`, `locations`, `baselineVersion` from `flyway.conf` so conf and build.gradle don't conflict. `baselineVersion` is catalog-specific, so it needs to be per-module too.
- App side has the same duplication: `CatalogFlywayConfig` hardcodes `catalog`. Each new module would need a copy. Fix later with a shared helper that builds a Flyway bean from a module name.

## Checklist

- [x] `flyway.conf` created with credentials
- [x] `application-local.properties` (main + test) created with credentials
- [x] Verify `./gradlew flywayInfo` works end to end (task name is one word, `flywayInfo`, not `flyway info`)
- [ ] Implement `-Pmodule` (Option 1) in `build.gradle`
- [ ] Trim module-specific keys out of `flyway.conf`
- [ ] Shared Flyway bean helper for the app side
- [ ] CI SQL-lint or DB permissions to block cross-schema FKs in migrations
