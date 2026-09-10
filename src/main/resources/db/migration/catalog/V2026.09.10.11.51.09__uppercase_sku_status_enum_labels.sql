-- Align sku_status labels with the Java enum's name() output (EnumType.STRING sends "DRAFT", not "draft")
-- Type was created unqualified in V2 (resolved via search_path), so kept unqualified here too.
ALTER TYPE sku_status RENAME VALUE 'draft' TO 'DRAFT';
ALTER TYPE sku_status RENAME VALUE 'active' TO 'ACTIVE';
ALTER TYPE sku_status RENAME VALUE 'discontinued' TO 'DISCONTINUED';