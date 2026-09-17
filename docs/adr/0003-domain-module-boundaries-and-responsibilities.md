# ADR-0003: Domain Module Boundaries and Responsibilities

- **Status:** Accepted
- **Date:** 2026-09-17
- **Deciders:** Solo project — revisit whenever a new module or a shared concept (like stock, price, or notifications) starts to feel ambiguous
- **Related:** ADR-0001 (how boundaries are technically enforced), ADR-0002 / deferred catalog entities

## Table of Contents

1. [Context](#context)
2. [Decision](#decision)
3. [Module Responsibility Table](#module-responsibility-table)
4. [Module Details](#module-details)
   - [identity](#identity)
   - [catalog](#catalog)
   - [inventory](#inventory)
   - [ordering](#ordering)
   - [payment](#payment)
   - [fulfillment](#fulfillment)
   - [notification](#notification)
   - [integration](#integration)
5. [Consequences](#consequences)
6. [Open Questions](#open-questions)
7. [Action Items](#action-items)

## Context

Relay is an omnichannel Order & Fulfillment Platform, built as a Spring Modulith monolith with 8 modules. Before writing more code inside any module, it is easy to accidentally put a piece of data or logic in the wrong place — for example, a `stock` count ended up living on the `Sku` entity in `catalog`, even though "how much is available" is really an `inventory` concern, not a `catalog` concern.

This kind of mistake is hard to catch with tools. ADR-0001 already enforces boundaries at the *code* level (no importing another module's entities, no cross-schema joins). But nothing stops a developer from putting the *wrong field* inside the *right entity*, inside the *right module* — because that mistake is a modeling problem, not an import problem.

The fix is to first agree, in plain language, on one question that each module answers. Once that question is written down, it becomes easy to check any new field or entity against it: "does this piece of data answer *this* module's question, or some other module's question?"

This document is that reference. It follows the project's own principle: decide the "seams" (module responsibilities and contracts) early, and decide the "interiors" (exact tables, columns, endpoints) just-in-time, per module, when that module is actually being built.

## Decision

Each module is defined by one plain-language question it answers. If a piece of data or behavior does not help answer that module's question, it probably belongs in a different module.

## Module Responsibility Table

| Module | Question it answers | What it does NOT do |
|---|---|---|
| `identity` | Who is this user, and what are they allowed to do? | Does not hold business data like orders or products — only who/what-is-allowed |
| `catalog` | What is this product/SKU, and what does it sell for? | Does not know how much stock is available — that is `inventory`'s question |
| `inventory` | How much of this SKU do we have on hand, and how much is already promised to someone (reserved / available-to-promise)? | Does not decide what can be sold (that's `catalog`) or where an order ships from (that's `fulfillment`) |
| `ordering` | What state is this order in right now, and what happens next? | Does not collect payment itself, and does not reserve stock itself — it asks `payment` and `inventory` to do those things |
| `payment` | Has this order been paid for, and can we guarantee it is only charged once? | Does not know what is in the order or where it ships — only whether money was captured |
| `fulfillment` | Where does this order physically come from (store or warehouse), and how does it reach the customer? | Does not check whether stock numbers are correct (that's `inventory`), and does not know about payment |
| `notification` | Given a message someone else decided to send, which channel (email / push / WebSocket) should deliver it, and did it arrive? | Does not decide *when* a notification is needed — that decision belongs to the module that owns the event (e.g. `inventory` decides "stock is low", `ordering` decides "order was placed") |
| `integration` | How do we translate data to and from an outside system (like a partner feed), and how do we survive that system being unreliable? | Does not connect Relay's own 8 modules to each other — that already happens through normal in-process events and interfaces. `integration` is only about the outside world |

## Module Details

### identity

Owns authentication (who is this user) and authorization (what can they do). This includes OAuth2 login (Google, Microsoft), JWT issuance, and role-based access control (RBAC) for roles like `customer`, `store_associate`, `store_manager`, `warehouse_picker`, and `admin`. Customer profile data (name, contact info) also lives here, since a profile is part of "who this user is."

### catalog

Owns the *sellable definition* of a product: its name, description, category, brand, and the specific variants (SKUs) a customer can buy, along with their list price. Think of catalog as answering "what exists and what does it cost" — not "how many do we have" (that's inventory) and not "what will the customer actually pay after a promotion" (pricing/promotions is a related but separate concern worth revisiting once the catalog module is more built out).

Current known issue: a `stock` field currently lives on the `Sku` entity. Based on the table above, this does not belong here — it answers inventory's question, not catalog's. See [Open Questions](#open-questions).

### inventory

Owns the *count* of each SKU: how much exists on-hand, how much is reserved for pending orders, and how much is actually available to promise (ATP) to a new order. This is a high-write, concurrency-sensitive module (many orders can try to reserve the same SKU at once), which is why it is planned as one of the three modules extracted into its own service later.

### ordering

Owns the lifecycle of an order, from a draft (cart) through confirmation, and any state after that (shipped, cancelled, etc.). Ordering coordinates with other modules to get things done — it asks `payment` to charge the customer and `inventory` to reserve stock — but it does not do those things itself. If a step fails partway through (e.g. payment fails after inventory was reserved), ordering is also responsible for making sure the earlier steps get undone (this is what the planned saga/compensation logic is for).

### payment

Owns the answer to one question: has this order been paid for, safely and exactly once? In this project, payment is a stub/mock payment gateway, acting as a facade in front of where a real payment provider would sit. Payment does not need to know what is being bought or how it will be delivered — only whether the charge succeeded, and it must guarantee that retries or duplicate requests never charge a customer twice (idempotency).

### fulfillment

Owns the decision of *where* an order ships from (a nearby store, or a warehouse/DC) and the physical steps to get it there — picking, packing, and handing off to a carrier. With multiple warehouses in play, fulfillment also owns the routing logic that picks the best source for a given order. Fulfillment trusts that inventory numbers are correct; it does not re-verify stock counts itself.

### notification

Owns delivery, not decision-making. Notification's job is: given a request from another module ("tell this customer their order shipped", "tell this seller their stock is low"), pick the right channel (email, push, WebSocket) and deliver the message. The decision of *when* something is notification-worthy belongs to the module that knows about the underlying event — `ordering` decides an order was placed, `inventory` decides stock is low — and that module publishes an event or calls notification; notification does not watch for these conditions itself.

### integration

Owns translation and resilience at the boundary with the outside world — for example, an inbound feed from a partner system. This is the module most directly modeled on the real-world OMS/WMS integration work this project is inspired by: taking an external system's data shape, converting it into Relay's own canonical model (an anti-corruption layer), and handling delivery problems (retries, dead-letter queues, poison messages) that come from talking to something outside your control. Integration is not responsible for wiring Relay's own 8 modules together — that already happens through normal in-process communication (events or exposed interfaces), which is a much simpler problem than talking to an external system.

## Consequences

**Positive**

- Any new field or entity can be checked against a simple test: "does this answer this module's question?" This makes boundary mistakes (like `stock` on `Sku`) visible during design, not just during a code review or an ArchUnit failure.
- Notification and integration, the two vaguest modules going in, now have a clear, narrow scope: notification is a pure message-delivery layer, and integration is strictly about the outside world.
- This document gives a fast answer during an interview to "why does your system look like this" for each module, without having to re-derive the reasoning on the spot.

**Costs / follow-ups**

- This is a living document. As modules get built out, some responsibilities may need to be split further (for example, pricing and promotions may eventually deserve their own line of thinking within `catalog`).
- Writing this down does not fix the `stock` field by itself — that still needs an actual migration and code change, tracked as an action item below.

## Open Questions

- **`stock` on `Sku`:** Was this a deliberate reversal of ADR-0002 (which explicitly deferred stock/inventory concerns out of catalog), or did it get added by accident while working on something else? Needs a decision before more catalog work is built on top of it.
- **Category shape:** Should `Category` be a tree (a merchandising hierarchy like Department > Class > Subclass) or a flat list? And is a product's relationship to category one-to-many or many-to-many?
- **Pricing vs. catalog:** The current `Sku.price` is a single value. Real retail systems usually separate a catalog list price from a time/channel-scoped selling price (promotions, price lists). Worth deciding whether that separation matters for this project or is out of scope.
- **External identifiers:** Where should identifiers like UPC/GTIN or a vendor's own item code live — directly on `Sku`, or in a separate identifier-mapping concept (which would naturally connect to the `integration` module's anti-corruption layer)?

## Action Items

1. [ ] Decide the fate of the `stock` field on `Sku` (move to `inventory`, or explicitly re-accept it in `catalog` with a written reason)
2. [ ] Use this document's module table as the seed for the planned `relay-domain-spine.md` (end-to-end flows, module contracts, event catalogue, aggregate list)
3. [ ] Resolve the Category shape question when the `catalog` module's vertical slice reaches `Category`
4. [ ] Revisit the pricing/promotions boundary once a real use case (a promotion, a channel-specific price) comes up
