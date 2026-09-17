ALTER TABLE brand
    ADD created_at TIMESTAMPTZ(6);

ALTER TABLE brand
    ADD created_by VARCHAR(255);

ALTER TABLE brand
    ADD last_modified_by VARCHAR(255);

ALTER TABLE brand
    ADD updated_at TIMESTAMPTZ(6);

ALTER TABLE brand
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE product
    ADD created_at TIMESTAMPTZ(6);

ALTER TABLE product
    ADD created_by VARCHAR(255);

ALTER TABLE product
    ADD last_modified_by VARCHAR(255);

ALTER TABLE product
    ADD updated_at TIMESTAMPTZ(6);

ALTER TABLE product
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE sku
    ADD created_at TIMESTAMPTZ(6);

ALTER TABLE sku
    ADD created_by VARCHAR(255);

ALTER TABLE sku
    ADD last_modified_by VARCHAR(255);

ALTER TABLE sku
    ADD updated_at TIMESTAMPTZ(6);

ALTER TABLE sku
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE brand
    ALTER COLUMN created_by SET NOT NULL;

ALTER TABLE product
    ALTER COLUMN created_by SET NOT NULL;

ALTER TABLE sku
    ALTER COLUMN created_by SET NOT NULL;

ALTER TABLE brand
    ALTER COLUMN last_modified_by SET NOT NULL;

ALTER TABLE product
    ALTER COLUMN last_modified_by SET NOT NULL;

ALTER TABLE sku
    ALTER COLUMN last_modified_by SET NOT NULL;

ALTER TABLE brand
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE product
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE sku
    ALTER COLUMN updated_at SET NOT NULL;