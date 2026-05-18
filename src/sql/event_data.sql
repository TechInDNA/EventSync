-- Event for testing GET /events endpoint
insert into eventsync_app.events (title, description, start_date, end_date, location)
values
    ('Tech Conference 2026', 'Annual technology conference featuring latest innovations', '2026-06-15 09:00:00', '2026-06-17 18:00:00', 'Convention Center'),
    ('AI Summit', 'Summit dedicated to artificial intelligence advancements', '2026-07-10 10:00:00', '2026-07-11 17:00:00', 'Tech Hub'),
    ('Web Development Workshop', 'Hands-on workshop for modern web development', '2026-05-20 14:00:00', '2026-05-20 18:00:00', 'Innovation Lab'),
    ('Cloud Computing Expo', 'Exhibition of cloud technologies and services', '2026-08-05 09:00:00', '2026-08-07 18:00:00', 'Expo Center'),
    ('Data Science Meetup', 'Monthly meetup for data science enthusiasts', '2026-05-25 18:00:00', '2026-05-25 21:00:00', 'Data Hub'),
    ('Mobile App Conference', 'Conference focused on mobile application development', '2026-09-12 10:00:00', '2026-09-13 17:00:00', 'Mobile Arena'),
    ('Cybersecurity Symposium', 'Symposium on latest cybersecurity threats and solutions', '2026-10-08 09:00:00', '2026-10-09 18:00:00', 'Security Center'),
    ('DevOps Days', 'Two-day event covering DevOps best practices', '2026-11-03 10:00:00', '2026-11-04 17:00:00', 'DevOps Hall'),
    ('Blockchain Expo', 'Exhibition showcasing blockchain technology', '2026-06-28 09:00:00', '2026-06-29 18:00:00', 'Crypto Center'),
    ('UX/UI Design Summit', 'Summit for user experience and interface designers', '2026-07-22 10:00:00', '2026-07-23 17:00:00', 'Design Studio'),
    ('IoT Conference', 'Internet of Things conference and exhibition', '2026-08-20 09:00:00', '2026-08-21 18:00:00', 'Smart City Hall'),
    ('Python Coding Bootcamp', 'Intensive Python programming bootcamp', '2026-09-05 10:00:00', '2026-09-07 16:00:00', 'Code Academy')
on conflict (title) do nothing;

-- Test event for DELETE /events endpoint (use this ID to test deletion)
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('a7cc7aac-50db-44cd-b8ae-cac7737a4052','Event to Delete', 'Test event for DELETE endpoint', '2026-12-15 10:00:00', '2026-12-15 17:00:00', 'Delete Test Location')
on conflict (id) do nothing;

-- Test event for PUT /events endpoint
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('c1b957ac-bdd7-481a-b8f7-636d43794f82','Event to Update', 'Original description for testing PUT endpoint', '2026-12-01 09:00:00', '2026-12-01 18:00:00', 'Test Location')
on conflict (id) do nothing;

-- Event with attached sessions for testing PUT /events/{id} endpoint (verifies sessions are returned in response)
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'Event with Sessions', 'Event that has multiple sessions attached for PUT testing', '2026-11-10 09:00:00', '2026-11-10 18:00:00', 'Session Test Hall')
on conflict (id) do nothing;

insert into eventsync_app.rooms (id, name)
values
    ('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', 'Room Alpha'),
    ('b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 'Room Beta')
on conflict (id) do nothing;

insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('c3d4e5f6-a7b8-9c0d-1e2f-3a4b5c6d7e8f', 'Morning Session', 'Morning session for PUT event testing', '2026-11-10 09:00:00', '2026-11-10 12:00:00', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', 100, 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b'),
    ('d4e5f6a7-b8c9-0d1e-2f3a-4b5c6d7e8f9a', 'Afternoon Workshop', 'Afternoon workshop for PUT event testing', '2026-11-10 14:00:00', '2026-11-10 17:00:00', 'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 50, 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b')
on conflict (id) do nothing;
