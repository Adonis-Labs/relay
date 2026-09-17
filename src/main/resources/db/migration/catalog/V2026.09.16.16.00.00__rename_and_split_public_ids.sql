-- Rename the opaque, API-facing identifiers to a consistent name and retype
-- them from text to native uuid (previously stored via UUID.randomUUID().toString()).
ALTER TABLE catalog.product
    RENAME COLUMN product_code TO public_id;
ALTER TABLE catalog.product
    ALTER COLUMN public_id TYPE uuid USING public_id::uuid;

ALTER TABLE catalog.sku
    RENAME COLUMN code TO public_id;
ALTER TABLE catalog.sku
    ALTER COLUMN public_id TYPE uuid USING public_id::uuid;

-- Human-readable business identifiers, distinct from the opaque public_id.
-- No default/backfill: this schema has no pre-existing rows yet.
ALTER TABLE catalog.product
    ADD COLUMN slug VARCHAR(60) NOT NULL UNIQUE;

ALTER TABLE catalog.sku
    ADD COLUMN sku_code VARCHAR(64) NOT NULL UNIQUE;