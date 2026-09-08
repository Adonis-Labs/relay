-- Add product_id field to SKU
ALTER TABLE catalog.sku
    ADD COLUMN product_id BIGINT;

-- Add SKU foreign key constraint
ALTER TABLE catalog.sku
    ADD CONSTRAINT fk_skus_product
        FOREIGN KEY (product_id)
        REFERENCES catalog.product (id);