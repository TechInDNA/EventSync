-- Room for testing POST /sessions endpoint
insert into eventsync_app.rooms (id, name)
values
    ('d4e1798f-d3cf-4114-9e74-aa6b091f8ff5', 'Session POST Room')
on conflict (id) do nothing;


-- Event for testing POST /sessions endpoint
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('8e354819-2e18-4955-88ce-0ab61e7e8ca3', 'Session POST Event', 'Event for testing POST session endpoint', '2026-06-15 09:00:00', '2026-06-15 18:00:00', 'POST Test Location')
    on conflict (id) do nothing;


-- Room used for the session to delete
insert into eventsync_app.rooms (id, name)
values
    ('c1c12204-4427-4add-b755-b681719d1685', 'Session Delete Room')
on conflict (id) do nothing;

-- Event used for the session to delete
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('b3b958ac-bdd7-481a-b8f7-636d43794f83', 'Session Delete Event', 'Event for testing DELETE session endpoint', '2026-12-01 09:00:00', '2026-12-01 18:00:00', 'Delete Test Location')
on conflict (id) do nothing;

-- Session to delete via DELETE /sessions/{id}
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('86ee1de3-b078-404e-9ee5-a55f2b3ff4a5', 'Session to Delete', 'Session for testing DELETE endpoint', '2026-06-15 10:00:00', '2026-06-15 12:00:00', 'c1c12204-4427-4add-b755-b681719d1685', 50, 'b3b958ac-bdd7-481a-b8f7-636d43794f83')
on conflict (id) do nothing;


-- Room for testing PUT /sessions/{id} endpoint
insert into eventsync_app.rooms (id, name)
values
    ('f1a1b2c3-d4e5-6789-0abc-def123456789', 'Session PUT Room')
on conflict (id) do nothing;

-- Event for testing PUT /sessions/{id} endpoint
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('a2b3c4d5-e6f7-8901-2345-6789abcdef01', 'Session PUT Event', 'Event for testing PUT session endpoint', '2026-07-01 09:00:00', '2026-07-01 18:00:00', 'PUT Test Location')
on conflict (id) do nothing;

-- Session to update via PUT /sessions/{id}
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('4e64d605-7a24-40aa-aea1-87c183fa4036', 'Session to Update via PUT', 'Session for testing PUT endpoint', '2026-06-15 10:00:00', '2026-06-15 12:00:00', 'f1a1b2c3-d4e5-6789-0abc-def123456789', 30, 'a2b3c4d5-e6f7-8901-2345-6789abcdef01')
on conflict (id) do nothing;


-- Rooms for testing GET /sessions endpoint
insert into eventsync_app.rooms (id, name)
values
    ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Main Hall'),
    ('9d1e9a8e-7a5e-4b0c-8e1a-2b3c4d5e6f7a', 'Workshop Room A'),
    ('7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', 'Workshop Room B')
on conflict (id) do nothing;

-- Events for testing GET /sessions endpoint
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'Summer Tech Fest', 'Annual summer technology festival', '2026-05-01 09:00:00', '2026-06-30 18:00:00', 'Tech Park'),
    ('2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e', 'Fall Developer Conference', 'Conference for developers in fall', '2026-05-01 09:00:00', '2026-08-31 18:00:00', 'Dev Center')
on conflict (id) do nothing;

-- Sessions for testing GET /sessions endpoint (past, live, future)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f', 'Keynote: Future of Tech', 'Opening keynote about technology trends', '2026-05-10 10:00:00', '2026-05-10 12:00:00', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 200, '1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d'),
    ('4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a', 'Web Development Basics', 'Introduction to modern web development', '2026-05-14 09:00:00', '2026-05-14 17:00:00', '9d1e9a8e-7a5e-4b0c-8e1a-2b3c4d5e6f7a', 50, '1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d'),
    ('5e6f7a8b-9c0d-1e2f-3a4b-5c6d7e8f9a0b', 'AI in Practice', 'Hands-on workshop on AI implementation', '2026-06-20 14:00:00', '2026-06-20 16:00:00', '7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', 30, '1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d'),
    ('6f7a8b9c-0d1e-2f3a-4b5c-6d7e8f9a0b1c', 'Cloud Architecture Deep Dive', 'Deep dive into cloud architecture patterns', '2026-05-05 10:00:00', '2026-05-05 11:30:00', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 100, '2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e'),
    ('7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', 'Testing Strategies', 'Testing strategies for Spring Boot applications', '2026-05-14 10:00:00', '2026-05-14 15:00:00', '7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', 40, '2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e'),
    ('8b9c0d1e-2f3a-4b5c-6d7e-8f9a0b1c2d3e', 'Advanced PostgreSQL', 'Advanced PostgreSQL features and optimization', '2026-07-15 09:00:00', '2026-07-15 12:00:00', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 60, '2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e')
on conflict (id) do nothing;

-- Link speakers to sessions via intervene table for GET /sessions speaker filtering
insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f', '10:00:00+02', '10:45:00+02'),
    ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', '4f17fb62-25cc-4758-a747-8f7df562d425', '4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a', '09:00:00+02', '12:00:00+02'),
    ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', '0a635d21-c174-4525-9031-19848bed99a4', '4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a', '13:00:00+02', '15:00:00+02'),
    ('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', 'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e', '5e6f7a8b-9c0d-1e2f-3a4b-5c6d7e8f9a0b', '14:00:00+02', '16:00:00+02'),
    ('e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'd3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f7a', '7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', '10:00:00+02', '12:00:00+02'),
    ('f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0c', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', '6f7a8b9c-0d1e-2f3a-4b5c-6d7e8f9a0b1c', '10:00:00+02', '11:30:00+02');

-- Room for testing DELETE session with cascade (intervene + question)
insert into eventsync_app.rooms (id, name)
values
    ('e9f8a7b6-c5d4-4321-9876-543210fedcba', 'Cascade Delete Room')
on conflict (id) do nothing;

-- Event for testing DELETE session with cascade
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('f1e2d3c4-b5a6-4789-0123-456789abcdef', 'Cascade Delete Event', 'Event for testing DELETE session with cascade', '2026-12-05 09:00:00', '2026-12-05 18:00:00', 'Cascade Test Location')
on conflict (id) do nothing;

-- Session with related intervene and question records for cascade delete testing
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('4d70c642-744e-41bf-9140-fae49af31269', 'Cascade Delete Session', 'Session for testing cascade delete with intervene and question', '2026-12-05 10:00:00', '2026-12-05 12:00:00', 'e9f8a7b6-c5d4-4321-9876-543210fedcba', 75, 'f1e2d3c4-b5a6-4789-0123-456789abcdef')
on conflict (id) do nothing;

-- Intervene record linked to the cascade delete session
insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('aa11bb22-cc33-44dd-55ee-66ff77889900', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', '4d70c642-744e-41bf-9140-fae49af31269', '10:00:00+02', '11:00:00+02')
on conflict (id) do nothing;

-- Question record linked to the cascade delete session
insert into eventsync_app.question (id, title, content, created_at, session_id, user_id, anonymous)
values
    ('bb22cc33-dd44-55ee-66ff-778899001122', 'Cascade test question', 'Will this session be deleted along with its related records?', '2026-12-05 10:05:00', '4d70c642-744e-41bf-9140-fae49af31269', '43005ca0-37f7-4942-86ac-695a29a6a3b1', false)
on conflict (id) do nothing;
