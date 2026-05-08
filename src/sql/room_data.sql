-- Rooms for testing GET /rooms endpoint (at least 5 rooms)
insert into eventsync_app.rooms (name)
values
    ('Grand Ballroom'),
    ('Conference Room A'),
    ('Conference Room B'),
    ('Workshop Hall'),
    ('Seminar Room 1'),
    ('Seminar Room 2'),
    ('Exhibition Hall');

-- Test room for DELETE /rooms/{id} endpoint (use this ID to test deletion)
insert into eventsync_app.rooms (id, name)
values
    ('48549c21-6dc0-4e9c-9f57-c0ae88f6544b', 'Room to Delete');