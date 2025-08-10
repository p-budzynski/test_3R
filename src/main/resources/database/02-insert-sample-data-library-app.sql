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


INSERT INTO books (author, title, book_category, page_count, added_date) VALUES
('George Orwell', 'Rok 1984', 2, 328, CURRENT_DATE),
('Haruki Murakami', 'Norwegian Wood', 1, 296, CURRENT_DATE),
('Stephen Hawking', 'Krótka historia czasu', 8, 212, CURRENT_DATE),
('Yuval Noah Harari', 'Sapiens: Od zwierząt do bogów', 6, 443, CURRENT_DATE),
('Andrew S. Tanenbaum', 'Systemy operacyjne', 9, 1136, CURRENT_DATE);


INSERT INTO subscriptions (client_id, subscription_type, subscription_value) VALUES
(3, 'AUTHOR', 'J.K. Rowling'),
(5, 'CATEGORY', 'Fantasy');