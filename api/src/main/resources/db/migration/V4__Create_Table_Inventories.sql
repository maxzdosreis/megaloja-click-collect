CREATE TABLE IF NOT EXISTS inventories (
    id bigint PRIMARY KEY,
    store_id bigint NOT NULL,
    product_id bigint NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_inventory_store
        FOREIGN KEY (store_id)
        REFERENCES stores(id),

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),

    CONSTRAINT uk_inventory_store_product
        UNIQUE (store_id, product_id)
);

CREATE INDEX idx_inventory_store
    ON inventories(store_id);

CREATE INDEX idx_inventory_product
    ON inventories(product_id);