CREATE TABLE IF NOT EXISTS products (
    id bigint PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    image_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE
);