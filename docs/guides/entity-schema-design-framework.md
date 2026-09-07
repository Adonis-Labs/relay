# Framework: Designing an Entity's Schema

A working checklist for deciding what an entity *is* and what fields it needs,
built while designing `SKU`. Not a finished methodology — add to it as new
decisions come up on `Product`, `Category`, `Brand`, and beyond.

## The four lenses

For any entity, ask these four questions before writing a single field:

1. **Identity** — what makes one instance distinct from another, in the
   *business's* terms, not just the database's? A surrogate `id` (auto-increment
   PK) is a database detail. Is there also a natural/business identifier that a
   human (customer, warehouse worker, support agent) would actually use to refer
   to this thing?
2. **Invariants** — what must always be true about this entity? What
   breaks — logically, not just in code — if a given field is missing, wrong, or
   changes after creation?
3. **Lifecycle** — is this created once and rarely touched, or does it change
   often? Can an instance ever be truly deleted, or does the business reality
   mean it can only be deactivated/superseded/discontinued?
4. **Relationships** — what does this entity *own* (fields that live on it
   because they only make sense for it) versus what does it merely *reference*
   (a link to something owned elsewhere)? For each reference: is it mandatory or
   optional?

Field lists fall out of answering these four honestly — they're rarely the
starting point.

## Recurring field-level decisions

Patterns that showed up designing SKU and will likely recur on every entity
after it.

### Surrogate key vs. business key

Every entity gets a DB-generated `id` for internal use (joins, FKs). Separately
ask if there's a **business key** — something meaningful outside the database.
SKU's `skuCode` is the example: customers, warehouses, and support tooling refer
to the SKU by that code, never by the internal `id`.

Rule of thumb: if a non-engineer would ever need to say "look up thing X," X is
probably your business key, and it needs its own uniqueness constraint.

### A business key should be stable, not a live description

A "smart" code that encodes meaning (`TSH-RED-M-001`) is fine to *generate*
descriptively, but treat it as **immutable once created**. If the underlying
data it was derived from changes later (color renamed, product recategorized),
the code does not get regenerated — every external system that already recorded
that code (orders, shipping labels, warehouse bins) would otherwise go stale or
break. A business key that quietly mutates isn't an identifier anymore.

### Money is never a plain float

Use an exact decimal type (`BigDecimal` in Java, `decimal` in C#) for any
monetary amount — never `double`/`float`, which introduce binary rounding
error. An amount alone is also incomplete: pair it with a currency. Whether
that's two flat fields (`price`, `currency`) or a dedicated `Money` value
object is a *separate* decision — see "defer shared abstractions" below.

### Enum (in code) vs. lookup table (in the DB)

The deciding question: **does the value drive branching logic in application
code, or is it just data to store/display/filter?**

- **Enum** when the set of values is small, closed, developer-controlled, and
  adding a new value requires new code anyway (you'd need to decide what the
  new value *does*, not just add a row). Example: SKU `status`
  (`DRAFT`/`ACTIVE`/`DISCONTINUED`) — persist with `@Enumerated(EnumType.STRING)`
  so the column stays a readable string.
- **Lookup table** when the set is open-ended or business-managed (an admin
  should be able to add one without a deploy), or the value needs its own
  attributes (name, description, sort order — i.e. it's really its own entity).
  Example: `Category`, `Brand`.

### Hard delete vs. soft delete

Before allowing a row to be deleted, ask what else references it. If deleting
it would orphan historical data that must remain valid (an `OrderItem` pointing
at a SKU that no longer exists), you need a **status/lifecycle field** instead
of a hard delete — and per the enum-vs-table rule above, if there are more than
two meaningful states (e.g. draft / active / discontinued), that's a signal to
reach for an enum status field rather than a single `isDeleted` boolean.

### Audit timestamps are close to a default

`createdAt` / `updatedAt` are cheap to add and expensive to reconstruct after
the fact (you can't backfill "when was this actually created" once it's gone
unrecorded). Default to including them unless there's a specific reason not to.

### Mandatory vs. optional relationships get decided at modeling time, not later

For every `@ManyToOne`/`@OneToOne`, explicitly decide: can this be null? That
answer becomes a `NOT NULL` constraint in the Flyway migration, not an
afterthought. SKU → Product is mandatory: a SKU cannot exist without exactly
one owning Product.

### Defer shared abstractions until there's a second real use

When a concept (e.g. a `Money` value object bundling amount + currency) would
plausibly be reused across modules, it's tempting to build the shared
abstraction immediately. Prefer deferring it: keep the flat fields now, and
extract the shared concept once a second real caller actually needs it. This
mirrors the deferral pattern already used in
[ADR-0002](../adr/deffered/ADR-0002-Catalog-deffered-entities.md) — add
complexity when a concrete use case demands it, not speculatively, and leave a
note (comment or doc) explaining *why* something was deliberately left simple.

## Worked example so far: SKU

Applying the four lenses to SKU produced:

- **Identity**: `id` (surrogate) + `skuCode` (business key, unique, immutable
  after creation).
- **Invariants**: `price` is a non-negative `BigDecimal`; `skuCode` is unique
  and never regenerated; `product` is never null.
- **Lifecycle**: `status` enum (`DRAFT`/`ACTIVE`/`DISCONTINUED`) instead of a
  hard delete or a boolean flag, because purchased SKUs must remain valid for
  historical `OrderItem` references; `createdAt`/`updatedAt` for audit.
- **Relationships**: mandatory `@ManyToOne` to `Product`; `size`/`color` kept
  as flat strings for now (deferred to lookup tables per ADR-0002, Option B)
  rather than modeled as a generic attribute system upfront.

## Open questions to resolve as new entities come up

- How does this framework's identity/business-key guidance apply to `Product`,
  given SKU already owns price/size/color — what's actually left for Product to
  describe?
- Bidirectional vs. unidirectional JPA relationships, and which side owns the
  foreign key — not yet exercised, will come up once `Category`/`Brand` have
  their own `@OneToMany` back-references.
- Naming/placement convention for shared value objects (like the deferred
  `Money`) once a second real use case justifies extracting one.
