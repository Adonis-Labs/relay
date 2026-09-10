-- PRODUCT Table
CREATE TABLE catalog.product(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_code VARCHAR(36) UNIQUE NOT NULL ,
    name VARCHAR(256) UNIQUE NOT NULL ,
    description TEXT
);

-- SKU Status
CREATE TYPE sku_status AS ENUM(
    'draft',
    'active',
    'discontinued'
);

-- SKU Table
CREATE TABLE catalog.sku(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
    code VARCHAR(36) UNIQUE NOT NULL ,
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0) ,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'USD',
    status sku_status NOT NULL DEFAULT 'draft',
    color VARCHAR(30),
    size VARCHAR(10)
);