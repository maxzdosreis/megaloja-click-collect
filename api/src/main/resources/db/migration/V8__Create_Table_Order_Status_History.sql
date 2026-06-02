CREATE TABLE IF NOT EXISTS order_status_history (
    id bigint PRIMARY KEY,
    order_id bigint NOT NULL,
    status VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_status_history_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
);

CREATE INDEX idx_order_status_history_order
    ON order_status_history(order_id);