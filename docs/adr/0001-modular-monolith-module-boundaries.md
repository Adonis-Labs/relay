# ADR-001: Enforce modular monolith boundaries with Spring Modulith + custom ArchUnit rules

- **Status:** Accepted
- **Date:** 2026-09-05
- **Related:** NX-77 (wire `ApplicationModules.verify()`), NX-78 (CI green on PR + trunk-based branching), this ticket's parent (ArchUnit rules for cross-module entity and cross-schema join boundaries)

## Context

`relay` is being built as a modular monolith: a single Spring Boot deployable, internally decomposed into modules by business capability (e.g. `ordering`, `catalog`), each owning its own package tree and its own database schema. The goal is to get the seams of a service-oriented system — clear ownership, no reaching into another module's internals, no shared data model — without paying for network calls, distributed transactions, or multi-repo overhead before the domain is well understood.

The risk with any monolith is that these seams are conventions, not compiler-enforced boundaries: nothing stops a class in `ordering` from importing a class in `catalog` directly. Conventions erode under deadline pressure unless something automated catches the drift.

We already had one layer of enforcement (NX-77): Spring Modulith's `ApplicationModules.of(RelayApplication.class).verify()`, wired as a JUnit test (`ModularityTests`). Modulith derives "modules" from the top-level packages under the application's base package and fails the build if a module reaches into another module's `internal` sub-package, or if there's a package cycle between modules.

That check operates at the Java type/package level and is necessarily generic — it knows nothing about JPA or the database. Two gaps remained:

1. **Entity-level coupling.** A class could still hold a direct reference to another module's `@Entity` type. This is a specific, high-value smell (it's exactly how modules end up sharing a data model) and deserved its own rule with a message that tells the engineer what to do instead ("reference the other module's aggregate by id"), rather than a generic "illegal dependency" failure.
2. **Schema-level coupling.** Nothing verified that our database schema separation actually matches our module separation. Two entities mapped to different `@Table(schema = ...)` values could still be linked by a JPA relationship annotation (`@ManyToOne`, `@OneToMany`, etc.), silently reintroducing tight coupling at the persistence layer — Hibernate would happily generate the join — even while satisfying the Java-level module rule.

## Decision

Add two custom, framework-agnostic ArchUnit rules (`com.example.relay.architecture.ArchitectureRules`), wired into `ModuleBoundaryArchitectureTest` and run in CI on every PR and push to `main` via `./gradlew check`:

1. **`noCrossModuleEntityImports(basePackage)`** — no class may have a direct dependency on an `@Entity` class belonging to a different module, where "module" is the package segment immediately after `basePackage` (e.g. `com.example.relay.ordering` vs. `com.example.relay.catalog`).
2. **`noCrossSchemaJoins()`** — every `@Entity`'s relationship fields (`@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`, and defensively `@JoinColumn`/`@JoinTable`) must target an `@Entity` mapped to the same `@Table(schema = ...)` value. Resolution walks ArchUnit's bytecode-level generic type model (so `List<OrderLine>` resolves to `OrderLine`, not `List`) without loading or reflecting on the target classes.

Both rules are pure ArchUnit — they don't require a Spring context — so they run fast and are independently unit-testable. Each is exercised against small, purpose-built "clean" and "violating" fixture packages (`archunitfixtures.nocrossmoduleentityimports`, `archunitfixtures.nocrossschemajoins`) before being pointed at the real `com.example.relay` base package. `allowEmptyShould(true)` is set on both rules so they don't fail on vacuous truth while the real codebase has no modules or entities yet — the rule is proven correct against fixtures now, and will start actually gating real code the moment the first module/entity lands.

## Consequences

**Positive**

- Three independent layers now guard modularity, each catching a different failure mode: Modulith's package/bean boundary (Java access), the entity-import rule (data-model coupling smuggled through normal code dependencies), and the schema-join rule (data-model coupling smuggled through the persistence mapping, invisible at the Java-dependency level).
- Violations are caught at build time, with a message that names the correct fix, rather than being caught in review (or not at all).
- Because the rules don't need a Spring context, they're cheap to run and were fully proven against fixtures before ever touching real code, so we have confidence they fire on true positives and stay quiet on true negatives.

**Costs / follow-ups**

- This is another piece of test infrastructure to maintain, and its correctness rests on two conventions rather than a first-class framework guarantee: "module = first package segment after the base package," and "schema ownership is expressed via `@Table(schema = ...)`." If either convention is violated elsewhere in the codebase, the rule can't help.
- As real modules land, watch for false negatives from the module-naming heuristic (e.g. a genuinely shared/common package sitting at the same level as real modules would be misclassified as its own "module").
- Worth revisiting whether Spring Modulith's own named-interface / explicit allowed-dependency mechanism could eventually subsume the entity-import rule, once we have real modules to test that against.

## Alternatives considered

- **Rely solely on `ApplicationModules.verify()`.** Rejected: it has no notion of JPA/schema semantics, so it can pass while two modules' data models are directly joined at the persistence layer.
- **Enforce boundaries by code review / team convention only.** Rejected: conventions aren't self-enforcing, and boundary erosion is exactly the kind of thing that's easy to approve in a rushed review and expensive to unwind later.
- **Enforce schema separation via Flyway migration layout alone (one schema per module), with no ArchUnit check.** Rejected: clean migrations don't prevent application code from mapping a cross-schema JPA relationship on top of them — the schemas can be perfectly separated in the database while the entity graph reunites them.
