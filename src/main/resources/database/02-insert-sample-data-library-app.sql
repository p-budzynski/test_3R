--liquibase formatted sql
--changeset test_3R:2

INSERT INTO categories (name) VALUES
    ('Fiction'),
    ('Science Fiction'),
    ('Fantasy'),
    ('Mystery'),
    ('Romance'),
    ('Biography'),
    ('History'),
    ('Science'),
    ('Technology'),
    ('Horror');


INSERT INTO clients (first_name, last_name, email, city, email_verified, verification_token) VALUES
('Anna', 'Kowalska', 'anna.kowalska@example.com', 'Warszawa', true, NULL),
('Jan', 'Nowak', 'jan.nowak@example.com', 'Kraków', false, 'abc123def456'),
('Maria', 'Wiśniewska', 'maria.wisniewska@example.com', 'Gdańsk', true, NULL),
('Tomasz', 'Zieliński', 'tomasz.zielinski@example.com', 'Wrocław', false, 'token789xyz'),
('Katarzyna', 'Mazur', 'katarzyna.mazur@example.com', 'Poznań', true, NULL);


INSERT INTO books (author, title, book_category, page_count) VALUES
('George Orwell', 'Rok 1984', 2, 328),
('Haruki Murakami', 'Norwegian Wood', 1, 296),
('Stephen Hawking', 'Krótka historia czasu', 8, 212),
('Yuval Noah Harari', 'Sapiens: Od zwierząt do bogów', 6, 443),
('Andrew S. Tanenbaum', 'Systemy operacyjne', 9, 1136);


INSERT INTO subscriptions (client_id, subscription_type, subscription_value) VALUES
(3, 'AUTHOR', 'J.K. Rowling'),
(5, 'CATEGORY', 'Fantasy');