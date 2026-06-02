CREATE TABLE IF NOT EXISTS pickup_authorizations (
    id bigint PRIMARY KEY,
    order_id bigint NOT NULL,
    authorized_name VARCHAR(255) NOT NULL,
    authorized_document VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pickup_authorization_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
);

CREATE INDEX idx_pickup_authorizations_order
    ON pickup_authorizations(order_id);