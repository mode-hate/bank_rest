-- ROLES
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- USERS
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

-- CARDS
CREATE TABLE cards (
    id SERIAL PRIMARY KEY,
    encrypted_number VARCHAR(255) NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- TRANSFERS
CREATE TABLE transfers (
    id SERIAL PRIMARY KEY,
    from_card_id BIGINT NOT NULL,
    to_card_id BIGINT NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_transfer_from_card FOREIGN KEY (from_card_id) REFERENCES cards(id) ON DELETE CASCADE,
    CONSTRAINT fk_transfer_to_card FOREIGN KEY (to_card_id) REFERENCES cards(id) ON DELETE CASCADE
);
