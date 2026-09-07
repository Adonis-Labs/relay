# ADR-001: Catalog Module — Scope for v1 and Deferred Entities

**Status:** Accepted (v1 scope) / Open items Proposed for later review
**Date:** 2026-09-07
**Deciders:** Solo project — revisit when catalog module is built out further

## Context

The catalog module is being designed as a learning project alongside Spring Modulith
patterns. A full e-commerce catalog can include many entities (Product, SKU, Category,
Brand, Images, generic Attributes, Price History, Inventory Reservations, etc.), but
building all of them upfront risks over-engineering before the core relationships and
module boundaries are even proven out.

A separate **Inventory module** already exists (or is planned) in this project, which
matters for scoping: anything about *stock levels, reservations, or warehouse state*
is arguably Inventory's responsibility, not Catalog's — Catalog should describe
*what can be sold*, Inventory should track *how much of it exists*. This is a direct
application of bounded-context thinking (Part 1.2 of the modular-monolith roadmap):
don't let Catalog's `SKU` become a dumping ground for concerns that belong to a
different bounded context.

## Decision

**v1 scope (build now):** `Product`, `SKU`, `Category`, `Brand`.

This gives a complete, realistic catalog module — product variants, categorization,
and brand grouping — without pulling in complexity that isn't needed yet to learn
the core Spring Modulith / JPA relationship patterns (`@OneToMany`, `@ManyToOne`,
owning vs inverse side, unidirectional vs bidirectional).

**Deferred (documented below, decide later):** `ProductImage`, generic
Attribute/AttributeValue modeling, `PriceHistory`, `InventoryReservation` /
stock-related concerns.

## Options Considered — Deferred Entities

### Option A: `ProductImage`
| Dimension | Assessment |
|---|---|
| Complexity | Low — straightforward `@OneToMany` from Product or SKU |
| Belongs in Catalog? | Yes — image is a catalog-display concern |
| When to add | As soon as you want another `@OneToMany` practice rep, or when the UI needs real product photos |

**Pros:** Cheap to add, reinforces Topic 1 patterns, no cross-module coupling.
**Cons:** None significant — mainly just "not urgent yet."

### Option B: Generic Attribute / AttributeValue model
(replacing hardcoded `size`/`color` columns on `SKU` with a flexible key-value model)

| Dimension | Assessment |
|---|---|
| Complexity | Medium-High — requires a more abstract schema and query pattern |
| Belongs in Catalog? | Yes |
| When to add | Only if the catalog needs to support product types with genuinely different variant dimensions (e.g., shoes vs. electronics vs. books in the same catalog) |

**Pros:** Scales to arbitrary product types without schema changes.
**Cons:** Harder to query, harder to validate, more indirection — overkill if every
product in your domain has the same shape of variants (size/color).

### Option C: `PriceHistory`
| Dimension | Assessment |
|---|---|
| Complexity | Low-Medium — an append-only table tied to SKU |
| Belongs in Catalog? | Yes — price is a Catalog/SKU attribute |
| When to add | Only if a real feature needs it (price-drop display, analytics, audit trail) |

**Pros:** Clean audit trail, enables price-drop UI, useful for future analytics.
**Cons:** Pure overhead if nothing in the product ever consumes the history.

### Option D: `InventoryReservation` / stock-holding concerns
| Dimension | Assessment |
|---|---|
| Complexity | Medium — needs to coordinate with checkout flow, has concurrency implications |
| Belongs in Catalog? | **No — this likely belongs in the Inventory module, not Catalog.** |
| When to add | When checkout/ordering flow is designed, and needs a home in Inventory |

**Pros of keeping it out of Catalog:** Preserves bounded-context separation — Catalog
shouldn't own transactional stock-holding logic. Matches the modular-monolith roadmap's
guidance (1.2, 1.8) that a module should own its own transactional boundary and not
absorb another module's concerns just because the data feels related.
**Cons:** Will require a cross-module relationship (Catalog's `SKU` referenced by
Inventory's `InventoryReservation`, likely by ID only, not a direct entity reference —
worth revisiting once you've covered Part 1.6 (communication patterns) and 1.7
(anti-corruption layer) from the roadmap.

## Trade-off Analysis

The common thread across A–C is: **add it when a concrete use case demands it, not
speculatively.** This mirrors the unidirectional-vs-bidirectional lesson from Topic 3 —
every extra entity/relationship is something to keep consistent and reason about, so
the bar for adding it upfront should be "I have a real reason," not "it might be
useful someday."

Option D is different in kind, not just timing: it's a signal to actively route the
concern to the Inventory module rather than defer it inside Catalog. Revisit this once
both modules exist and you're designing the cross-module contract between them
(Catalog: "here is SKU X" / Inventory: "here is how many of SKU X we have").

## Consequences

- Catalog v1 stays lean: `Product`, `SKU`, `Category`, `Brand` — all straightforward
  `@ManyToOne`/`@OneToMany` patterns, no `@ManyToMany` or cross-module concerns yet.
- Stock/inventory questions are explicitly deferred to the Inventory module, avoiding
  a bounded-context leak early in the project.
- `ProductImage`, generic attributes, and `PriceHistory` remain easy "next reps" to add
  incrementally without redesigning the core schema.

## Action Items
1. [ ] Build `Product`, `SKU`, `Category`, `Brand` (v1 scope)
2. [ ] Add `ProductImage` once comfortable with basic `@OneToMany`, as extra practice
3. [ ] Revisit generic Attribute modeling only if a second product type is added that doesn't fit size/color
4. [ ] Design the Catalog ↔ Inventory contract when the Inventory module is built out (likely SKU-id-based reference, not a direct JPA relationship across modules)
5. [ ] Add `PriceHistory` only when a feature (price-drop display, analytics) actually needs it