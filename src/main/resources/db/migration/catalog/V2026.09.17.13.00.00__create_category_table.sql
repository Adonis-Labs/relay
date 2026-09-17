-- Category forms a tree via a self-referencing parent_id (adjacency list).
-- A null parent_id means a top-level category.
CREATE TABLE catalog.category (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    public_id UUID NOT NULL UNIQUE,
    name VARCHAR(256) NOT NULL UNIQUE,
    slug VARCHAR(60) NOT NULL UNIQUE,
    parent_id BIGINT REFERENCES catalog.category (id),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ(6) NOT NULL,
    updated_at TIMESTAMPTZ(6) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_modified_by VARCHAR(255) NOT NULL
);
