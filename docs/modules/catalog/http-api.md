# Catalog module: HTTP API

Status: **design, not implemented**. Only `POST /products` (NX-87) and `GET /products/{id}` (NX-88) are in scope right now. Everything else is the planned surface, built when actually needed.

## Conventions

- Base path: `/products`, `/categories`, `/brands` (plural nouns; not `/catalog/product`).
- URL identifiers are the entity's `publicId` (UUID). The DB `Long` id never leaves the module.
- Request/response are DTOs only. Entities never cross the controller boundary.
- Errors: `400` validation failure, `404` unknown id, `409` uniqueness clash (name/slug/skuCode) or optimistic-lock conflict.
- Slugs are generated server-side from `name` (see `Generators.slug`), so clients do not send them.

## Products (aggregate root)

| Method | Path | Purpose | Success | Notes |
|---|---|---|---|---|
| POST | `/products` | Create a product | 201 + `Location` | NX-87. Category and brand referenced by their `publicId`. |
| GET | `/products/{id}` | Fetch one product | 200 | NX-88. 404 with an error body if missing. |
| GET | `/products` | List/search | 200 | Paginated; filter by category, brand. Watch N+1 (`@EntityGraph`). |
| PATCH | `/products/{id}` | Partial update (name, description, category, brand) | 200 | Prefer PATCH; the entity has many optional fields. |
| DELETE | `/products/{id}` | Retire the product | 204 | Soft delete, not a hard delete (see SKUs). |

## SKUs (children of Product)

A `Sku` belongs to a `Product` aggregate, so it is addressed through its parent.

| Method | Path | Purpose | Success |
|---|---|---|---|
| POST | `/products/{productId}/skus` | Add a SKU | 201 |
| GET | `/products/{productId}/skus` | List a product's SKUs | 200 |
| GET | `/products/{productId}/skus/{skuId}` | Fetch one SKU | 200 |
| PATCH | `/products/{productId}/skus/{skuId}` | Update price, colour, size, **status** | 200 |

No `DELETE`. `Product.skus` deliberately excludes `CascadeType.REMOVE` because Orders will reference SKUs. A SKU is retired by `PATCH` with `status: DISCONTINUED` (`SkuStatus`: `DRAFT`, `ACTIVE`, `DISCONTINUED`).

## Categories (tree via `parentCategory`)

| Method | Path | Purpose | Success | Notes |
|---|---|---|---|---|
| POST | `/categories` | Create | 201 | Optional `parentId` for a subcategory; omit for top level. |
| GET | `/categories/{id}` | Fetch one | 200 | |
| GET | `/categories` | List | 200 | Decide flat list vs nested tree when building. |
| PATCH | `/categories/{id}` | Rename / re-parent | 200 | Reject cycles (a category cannot become its own descendant). |
| DELETE | `/categories/{id}` | Remove | 204 | Reject with 409 if it has subcategories or products. |

## Brands

| Method | Path | Purpose | Success |
|---|---|---|---|
| POST | `/brands` | Create | 201 |
| GET | `/brands/{id}` | Fetch one | 200 |
| GET | `/brands` | List | 200 |
| PATCH | `/brands/{id}` | Update name, description, logoUrl | 200 |
| DELETE | `/brands/{id}` | Remove | 204 (409 if products still use it) |

## Open questions

- PUT vs PATCH: this doc assumes PATCH only. PUT (full replace) can be added later if a client needs it.
- Concurrent edits: entities carry a version column. Decide whether PATCH requires `If-Match`/version in the body.
- Category delete: 409 vs. reassigning products, decide when building.

## Not HTTP: the module's Java API

Other modules (Ordering, Inventory) will not call these endpoints. When one needs catalog data, expose an interface in the `com.example.relay.catalog` base package, or publish events. Nothing exists there yet, and it should be added only when a second module actually asks for it.
