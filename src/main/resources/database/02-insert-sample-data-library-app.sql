--liquibase formatted sql
--changeset test_3R:2


INSERT INTO authors (name) VALUES
('George Orwell'),
('Haruki Murakami'),
('Stephen Hawking'),
('Yuval Noah Harari'),
('Andrew S. Tanenbaum');


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


INSERT INTO books (title, category_fk, page_count, author_fk) VALUES
('Rok 1984', 2, 328, 1),
('Norwegian Wood', 1, 296, 2),
('Krótka historia czasu', 8, 212, 3),
('Sapiens: Od zwierząt do bogów', 6, 443, 4),
('Systemy operacyjne', 9, 1136, 5);


INSERT INTO subscriptions (client_fk, author_fk, category_fk) VALUES
(3, 1, null),
(5, null, 6);

INSERT INTO message_config (code, subject, body) VALUES
('ACCOUNT_ACTIVATION', 'Confirm your e-mail address!',
'Click the link to confirm your e-mail: {{verificationUrl}}{{token}}'),
('NEW_BOOKS', 'New books in the library!',
'Hello {{firstName}},\n\nWe’ve added new books that might interest you:\n\n{{bookList}}\n\nVisit our library to explore them!\n\nBest regards,\nYour Library Team!');
