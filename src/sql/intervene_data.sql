-- Test data for POST and PUT /sessions/{sessionId}/speaker/{speakerId} endpoints
-- Self-contained: uses completely unique IDs (no conflicts with other seed files)

-- Rooms
insert into eventsync_app.rooms (id, name)
values
    ('550e8400-e29b-41d4-a716-446655440001', 'Intervene Test Room A'),
    ('550e8400-e29b-41d4-a716-446655440002', 'Intervene Test Room B')
on conflict (id) do nothing;

-- Events
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('550e8400-e29b-41d4-a716-446655440010', 'Intervene Test Event A', 'Event for testing POST intervene endpoint', '2026-06-15 09:00:00', '2026-06-15 18:00:00', 'Intervene Location A'),
    ('550e8400-e29b-41d4-a716-446655440011', 'Intervene Test Event B', 'Event for testing POST intervene with existing speakers', '2026-07-01 09:00:00', '2026-07-01 18:00:00', 'Intervene Location B'),
    ('550e8400-e29b-41d4-a716-446655440012', 'Intervene Test Event C', 'Event for testing POST intervene not found', '2026-08-01 10:00:00', '2026-08-01 17:00:00', 'Intervene Location C')
on conflict (id) do nothing;

-- Speakers (users with role 'speaker')
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('550e8400-e29b-41d4-a716-446655440020', 'Alice', 'Intervene', 'alice.intervene@eventsync.com', 'Speaker for testing POST intervene endpoint.', 'https://i.pravatar.cc/150?img=41', 'speaker'),
    ('550e8400-e29b-41d4-a716-446655440021', 'Bob', 'Intervene', 'bob.intervene@eventsync.com', 'Second speaker for testing POST intervene endpoint.', 'https://i.pravatar.cc/150?img=42', 'speaker'),
    ('550e8400-e29b-41d4-a716-446655440022', 'Charlie', 'Intervene', 'charlie.intervene@eventsync.com', 'Third speaker for testing POST intervene endpoint.', 'https://i.pravatar.cc/150?img=43', 'speaker')
on conflict (id) do nothing;

-- Session A: no speaker yet (ideal for testing POST intervene: link a speaker)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('550e8400-e29b-41d4-a716-446655440030', 'Intervene Test Session A', 'Session without speaker for POST intervene testing', '2026-06-15 10:00:00', '2026-06-15 12:00:00', '550e8400-e29b-41d4-a716-446655440001', 50, '550e8400-e29b-41d4-a716-446655440010')
on conflict (id) do nothing;

-- Session B: already has 2 speakers (Alice from 10:00 to 11:00, Bob from 11:00 to 12:00)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('550e8400-e29b-41d4-a716-446655440031', 'Intervene Test Session B', 'Session with existing speakers for POST intervene testing', '2026-07-01 10:00:00', '2026-07-01 12:00:00', '550e8400-e29b-41d4-a716-446655440002', 40, '550e8400-e29b-41d4-a716-446655440011')
on conflict (id) do nothing;

insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('550e8400-e29b-41d4-a716-446655440040', '550e8400-e29b-41d4-a716-446655440020', '550e8400-e29b-41d4-a716-446655440031', '2026-07-01 10:00:00', '2026-07-01 11:00:00'),
    ('550e8400-e29b-41d4-a716-446655440041', '550e8400-e29b-41d4-a716-446655440021', '550e8400-e29b-41d4-a716-446655440031', '2026-07-01 11:00:00', '2026-07-01 12:00:00')
on conflict (id) do nothing;

-- Session C: no speaker, used for negative tests
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('550e8400-e29b-41d4-a716-446655440032', 'Intervene Test Session C', 'Session for negative test cases', '2026-08-01 14:00:00', '2026-08-01 16:00:00', '550e8400-e29b-41d4-a716-446655440001', 30, '550e8400-e29b-41d4-a716-446655440012')
on conflict (id) do nothing;

-- Session D: has Charlie already linked; used for testing PUT intervene (update existing link)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('550e8400-e29b-41d4-a716-446655440033', 'Intervene Test Session D', 'Session with speaker for PUT intervene testing', '2026-06-20 10:00:00', '2026-06-20 16:00:00', '550e8400-e29b-41d4-a716-446655440001', 50, '550e8400-e29b-41d4-a716-446655440010')
on conflict (id) do nothing;

insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('550e8400-e29b-41d4-a716-446655440042', '550e8400-e29b-41d4-a716-446655440022', '550e8400-e29b-41d4-a716-446655440033', '2026-06-20 14:00:00', '2026-06-20 15:00:00')
on conflict (id) do nothing;
