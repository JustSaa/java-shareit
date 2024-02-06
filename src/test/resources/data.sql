DELETE FROM bookings;
DELETE FROM comments;
DELETE FROM items;
DELETE FROM requests;
DELETE FROM users;

ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;
ALTER TABLE items    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE requests ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users    ALTER COLUMN id RESTART WITH 1;

INSERT INTO users (name, email)
    VALUES ('Petr', 'petr@ya.ru'),
           ('Anna', 'anna@ya.ru'),
           ('Maria', 'maria@ya.ru');

INSERT INTO requests (description, created, requester_id)
    VALUES ('Нужна крестовая отвертка', '2022-09-10T07:52:17', 3),
           ('Велосипед на выходные', '2022-09-13T12:42:17', 1);

INSERT INTO items (name, description, available, owner_id, request_id)
    VALUES ('Отвертка-мультитул', 'У отвертки разные виды насадок', true, 2, 1),
           ('Велосипед', 'Скоростной велик в хорошем состоянии', false, 3, null),
           ('Велик', 'Старый велосипед, требуется замена цепи', true, 2, null),
           ('Дрель', 'На аккумуляторе', true, 3, null),
           ('Чайник электрический', 'На 1.5 литров', true, 3, null),
           ('Мультиварка', 'В хорошем состоянии', true, 3, null),
           ('Фотоаппарат', 'Пленочный', true, 3, null),
           ('Фото- и видеокамера', 'Nikon', true, 1, null);

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
    VALUES (TIMESTAMPADD(DAY, -7, CURRENT_TIMESTAMP), TIMESTAMPADD(DAY, 7, CURRENT_TIMESTAMP), 2, 2, 'APPROVED'),
           (TIMESTAMPADD(DAY, 7, CURRENT_TIMESTAMP), TIMESTAMPADD(DAY, 8, CURRENT_TIMESTAMP), 3, 1, 'APPROVED'),
           ('2022-09-15T07:52:17', '2022-09-15T12:52:17', 1, 3, 'WAITING'),
           (TIMESTAMPADD(DAY, -8, CURRENT_TIMESTAMP), TIMESTAMPADD(DAY, -7, CURRENT_TIMESTAMP), 4, 1, 'REJECTED'),
           (TIMESTAMPADD(DAY, -8, CURRENT_TIMESTAMP), TIMESTAMPADD(DAY, -7, CURRENT_TIMESTAMP), 5, 1, 'CANCELED'),
           (TIMESTAMPADD(DAY, 7, CURRENT_TIMESTAMP), TIMESTAMPADD(DAY, 8, CURRENT_TIMESTAMP), 6, 1, 'APPROVED'),
           (TIMESTAMPADD(DAY, -7, CURRENT_TIMESTAMP), TIMESTAMPADD(DAY, 7, CURRENT_TIMESTAMP), 6, 1, 'APPROVED');