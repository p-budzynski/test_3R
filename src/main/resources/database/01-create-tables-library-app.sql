--liquibase formatted sql
--changeset test_3R:1

CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);


CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category_fk BIGINT NOT NULL,
    page_count INT,
    author_fk BIGINT NOT NULL,
    CONSTRAINT fk_books_author FOREIGN KEY (author_fk) REFERENCES authors(id),
    CONSTRAINT fk_books_category FOREIGN KEY (category_fk) REFERENCES categories(id)
);

CREATE INDEX idx_book_author ON books(author_fk);
CREATE INDEX idx_book_category ON books(category_fk);


CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    city VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(255)
);


CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    client_fk BIGINT NOT NULL,
    author_fk BIGINT,
    category_fk BIGINT,
    CONSTRAINT fk_subscriptions_client FOREIGN KEY (client_fk) REFERENCES clients(id),
    CONSTRAINT fk_subscriptions_author FOREIGN KEY (author_fk) REFERENCES authors(id),
    CONSTRAINT fk_subscriptions_category FOREIGN KEY (category_fk) REFERENCES categories(id)
);

CREATE INDEX idx_subscription_client ON subscriptions(client_fk);
CREATE INDEX idx_subscription_author ON subscriptions(author_fk);
CREATE INDEX idx_subscription_category ON subscriptions(category_fk);


CREATE TABLE subscription_notifications (
    id BIGSERIAL PRIMARY KEY,
    client_fk BIGINT NOT NULL,
    book_fk BIGINT NOT NULL,
    CONSTRAINT fk_subscription_notifications_client FOREIGN KEY (client_fk) REFERENCES clients(id),
    CONSTRAINT fk_subscription_notifications_book FOREIGN KEY (book_fk) REFERENCES books(id)
);


CREATE TABLE message_config (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    subject VARCHAR(255) NOT NULL,
    body VARCHAR(255) NOT NULL
);

