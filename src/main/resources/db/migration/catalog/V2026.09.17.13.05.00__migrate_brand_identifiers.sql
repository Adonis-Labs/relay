-- Brand.code was never actually wired up by application code (no setter/constructor
-- existed until now). Replaced with the same public_id (UUID) + slug pattern used by
-- Product. No backfill: this table has no pre-existing rows.
ALTER TABLE catalog.brand
    DROP COLUMN code;

ALTER TABLE catalog.brand
    ADD COLUMN public_id UUID NOT NULL UNIQUE;

ALTER TABLE catalog.brand
    ADD COLUMN slug VARCHAR(60) NOT NULL UNIQUE;
