-- Test data for PUT /sessions/{id} endpoint
-- Run after data.sql, room_data.sql, and event_data.sql have been executed

-- Room used for the session to update (use a specific ID for the test)
insert into eventsync_app.rooms (id, name)
values
    ('c1c12204-4427-4add-b755-b681719d1685', 'Session Update Room')
on conflict (id) do nothing;

-- Event used for the session to update (use a specific ID for the test)
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('b3b958ac-bdd7-481a-b8f7-636d43794f83', 'Session Test Event', 'Event for testing PUT session endpoint', '2026-12-01 09:00:00', '2026-12-01 18:00:00', 'Test Location')
on conflict (id) do nothing;

-- Session to update via PUT /sessions/{id}
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Session to Update', 'Original description for PUT session test', '2026-06-15 10:00:00', '2026-06-15 12:00:00', 'c1c12204-4427-4add-b755-b681719d1685', 50, 'b3b958ac-bdd7-481a-b8f7-636d43794f83')
on conflict (id) do nothing;

-- Seed sessions for testing GET /sessions endpoint
insert into eventsync_app.sessions (title, description, start_date, end_date, room_id, capacity, event_id)
select 'Keynote: Future of Tech', 'Opening keynote on future technology trends',
       '2026-06-15 09:00:00', '2026-06-15 10:30:00',
       r.id, 500, e.id
from eventsync_app.rooms r, eventsync_app.events e
where r.name = 'Grand Ballroom' and e.title = 'Tech Conference 2026'
and not exists (select 1 from eventsync_app.sessions where title = 'Keynote: Future of Tech');

insert into eventsync_app.sessions (title, description, start_date, end_date, room_id, capacity, event_id)
select 'AI Workshop: ML Basics', 'Hands-on machine learning workshop',
       '2026-06-15 11:00:00', '2026-06-15 13:00:00',
       r.id, 100, e.id
from eventsync_app.rooms r, eventsync_app.events e
where r.name = 'Workshop Hall' and e.title = 'Tech Conference 2026'
and not exists (select 1 from eventsync_app.sessions where title = 'AI Workshop: ML Basics');

insert into eventsync_app.sessions (title, description, start_date, end_date, room_id, capacity, event_id)
select 'Cloud Computing Panel', 'Panel discussion on cloud strategies',
       '2026-06-15 14:00:00', '2026-06-15 15:30:00',
       r.id, 200, e.id
from eventsync_app.rooms r, eventsync_app.events e
where r.name = 'Conference Room A' and e.title = 'Tech Conference 2026'
and not exists (select 1 from eventsync_app.sessions where title = 'Cloud Computing Panel');

insert into eventsync_app.sessions (title, description, start_date, end_date, room_id, capacity, event_id)
select 'Cybersecurity Trends 2026', 'Overview of emerging cybersecurity threats',
       '2026-07-10 10:00:00', '2026-07-10 12:00:00',
       r.id, 150, e.id
from eventsync_app.rooms r, eventsync_app.events e
where r.name = 'Seminar Room 1' and e.title = 'AI Summit'
and not exists (select 1 from eventsync_app.sessions where title = 'Cybersecurity Trends 2026');

insert into eventsync_app.sessions (title, description, start_date, end_date, room_id, capacity, event_id)
select 'React Native Workshop', 'Building mobile apps with React Native',
       '2026-05-20 14:00:00', '2026-05-20 17:00:00',
       r.id, 80, e.id
from eventsync_app.rooms r, eventsync_app.events e
where r.name = 'Workshop Hall' and e.title = 'Web Development Workshop'
and not exists (select 1 from eventsync_app.sessions where title = 'React Native Workshop');

insert into eventsync_app.sessions (title, description, start_date, end_date, room_id, capacity, event_id)
select 'Data Science with Python', 'Introduction to data science using Python',
       '2026-08-05 09:30:00', '2026-08-05 11:30:00',
       r.id, 120, e.id
from eventsync_app.rooms r, eventsync_app.events e
where r.name = 'Conference Room B' and e.title = 'Cloud Computing Expo'
and not exists (select 1 from eventsync_app.sessions where title = 'Data Science with Python');
