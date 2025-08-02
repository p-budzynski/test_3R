--liquibase formatted sql
--changeset test_3R:1

CREATE TABLE categories (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL UNIQUE
            );


CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    city VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(255)
);

CREATE INDEX idx_client_email_verified ON clients(id, email_verified);


CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    author VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    book_category BIGINT NOT NULL,
    page_count INTEGER,
    CONSTRAINT fk_books_category FOREIGN KEY (book_category) REFERENCES categories(id)
);

CREATE INDEX idx_book_author ON books(author);
CREATE INDEX idx_book_category ON books(book_category);


CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    subscription_type VARCHAR(50) NOT NULL,
    subscription_value VARCHAR(255) NOT NULL,
    CONSTRAINT fk_subscriptions_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT uk_client_subscription_value UNIQUE (client_id, subscription_type, subscription_value)
);

CREATE INDEX idx_subscription_type_value ON subscriptions(subscription_type, subscription_value);
CREATE INDEX idx_subscription_client ON subscriptions(client_id);
CREATE INDEX idx_subscription_client_type_value ON subscriptions(client_id, subscription_type, subscription_value);
