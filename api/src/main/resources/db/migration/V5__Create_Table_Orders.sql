CREATE TABLE IF NOT EXISTS orders (
    id bigint PRIMARY KEY,
    customer_id bigint NOT NULL,
    store_id bigint NOT NULL,
    status VARCHAR(50) NOT NULL,
    pickup_code VARCHAR(50) NOT NULL,
    total_amount NUMERIC(10,2) NOT NULL,
    pickup_deadline TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_customer
        FOREIGN KEY (customer_id)
        REFERENCES users(id),

    CONSTRAINT fk_order_store
        FOREIGN KEY (store_id)
        REFERENCES stores(id)
);

CREATE INDEX idx_orders_customer
    ON orders(customer_id);

CREATE INDEX idx_orders_store
    ON orders(store_id);

CREATE INDEX idx_orders_status
    ON orders(status);