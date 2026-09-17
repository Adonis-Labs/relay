-- Resolves ADR-0003's open "Category shape" question: a Product belongs to exactly
-- one Category (many-to-one), and to exactly one Brand. No backfill: no pre-existing rows.
ALTER TABLE catalog.product
    ADD COLUMN category_id BIGINT NOT NULL REFERENCES catalog.category (id);

ALTER TABLE catalog.product
    ADD COLUMN brand_id BIGINT NOT NULL REFERENCES catalog.brand (id);
