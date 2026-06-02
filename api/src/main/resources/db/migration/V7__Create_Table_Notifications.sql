CREATE TABLE IF NOT EXISTS notifications (
    id bigint PRIMARY KEY,
    order_id bigint NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_notification_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
);

CREATE INDEX idx_notifications_order
    ON notifications(order_id);