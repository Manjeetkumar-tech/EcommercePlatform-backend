-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    categories VARCHAR(255),
    inventory INTEGER NOT NULL
);

-- Carts table
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Cart Items table
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_cart
        FOREIGN KEY(cart_id)
        REFERENCES carts(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
);

-- Optional: Add indexes for frequently queried columns, especially foreign keys
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);
CREATE INDEX idx_carts_user_id ON carts(user_id);

-- Sequences are handled by BIGSERIAL for PostgreSQL, so no explicit CREATE SEQUENCE needed for IDs.
-- Hibernate typically creates sequences like 'users_id_seq', 'products_id_seq' etc.
-- If GenerationType.SEQUENCE were used with explicit sequence names, they would be defined here.
-- For GenerationType.IDENTITY, BIGSERIAL is the direct equivalent.
