ALTER TABLE catalog.sku
    ADD stock INTEGER NOT NULL DEFAULT 0
    CONSTRAINT stock_non_negative CHECK(stock >= 0);

ALTER TABLE catalog.sku
    ADD version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE catalog.brand
    ADD version INTEGER NOT NULL DEFAULT 0;

ALTER TABLE catalog.product
    ADD version INTEGER NOT NULL DEFAULT 0;