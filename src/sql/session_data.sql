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


-- Test data for GET /sessions endpoint (self-contained, independent of other seed files)

-- Rooms for GET sessions testing
insert into eventsync_app.rooms (id, name)
values
    ('05135b86-f3b7-4a27-8736-8415fe95ddf8', 'GET Sessions Room Alpha'),
    ('54116228-a730-42d6-a792-b31080a0142d', 'GET Sessions Room Beta')
on conflict (id) do nothing;

-- Events for GET sessions testing
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('f09aa72b-4c96-4c8b-880b-f39f0bc5f50e', 'GET Sessions Event Alpha', 'Event Alpha for GET sessions testing', '2026-06-15 09:00:00', '2026-06-15 18:00:00', 'GET Sessions Location'),
    ('8819ef47-f477-4133-9da2-d387cc7c9b1a', 'GET Sessions Event Beta', 'Event Beta for GET sessions testing', '2026-07-10 10:00:00', '2026-07-10 17:00:00', 'GET Sessions Location')
on conflict (id) do nothing;

-- Speakers for GET sessions testing
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', 'John', 'Doe', 'john.getsession@eventsync.com', 'Speaker for GET sessions endpoint testing.', 'https://i.pravatar.cc/150?img=1', 'speaker'),
    ('4f17fb62-25cc-4758-a747-8f7df562d425', 'Jane', 'Smith', 'jane.getsession@eventsync.com', 'Speaker for GET sessions endpoint testing.', 'https://i.pravatar.cc/150?img=2', 'speaker')
on conflict (id) do nothing;

-- 5 valid sessions (attached to a room and an event)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('b31545f4-7357-4353-998c-c336dae99e31', 'GET Sessions Valid 1', 'First valid session for GET endpoint testing', '2026-05-24 00:00:00', '2026-05-24 23:59:00', '05135b86-f3b7-4a27-8736-8415fe95ddf8', 50, 'f09aa72b-4c96-4c8b-880b-f39f0bc5f50e'),
    ('5baf154b-bdb8-4e0b-8569-7966fa313076', 'GET Sessions Valid 2', 'Second valid session for GET endpoint testing', '2026-06-15 11:00:00', '2026-06-15 12:00:00', '05135b86-f3b7-4a27-8736-8415fe95ddf8', 40, 'f09aa72b-4c96-4c8b-880b-f39f0bc5f50e'),
    ('dffe658d-0ad9-43cc-b54b-1d7e8600fa9b', 'GET Sessions Valid 3', 'Third valid session for GET endpoint testing', '2026-06-15 12:00:00', '2026-06-15 13:00:00', '54116228-a730-42d6-a792-b31080a0142d', 30, 'f09aa72b-4c96-4c8b-880b-f39f0bc5f50e'),
    ('f5368396-149e-4e0f-bb37-e670162c8605', 'GET Sessions Valid 4', 'Fourth valid session for GET endpoint testing', '2026-06-15 13:00:00', '2026-06-15 14:00:00', '54116228-a730-42d6-a792-b31080a0142d', 60, 'f09aa72b-4c96-4c8b-880b-f39f0bc5f50e'),
    ('ab320895-a75a-491b-bd5c-796893a5092c', 'GET Sessions Valid 5', 'Fifth valid session for GET endpoint testing', '2026-07-10 11:00:00', '2026-07-10 12:00:00', '05135b86-f3b7-4a27-8736-8415fe95ddf8', 45, '8819ef47-f477-4133-9da2-d387cc7c9b1a')
on conflict (id) do nothing;

-- Session attached to a speaker and an event, but no room
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('13260746-bf49-43db-97c4-4a69b81b096a', 'GET Sessions No Room', 'Session without a room for GET endpoint testing', '2026-06-15 14:00:00', '2026-06-15 15:00:00', null, 35, 'f09aa72b-4c96-4c8b-880b-f39f0bc5f50e')
on conflict (id) do nothing;

insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('52ca3cf4-70d0-4385-bf83-a315ecfc9341', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', '13260746-bf49-43db-97c4-4a69b81b096a', '14:00:00+02', '15:00:00+02')
on conflict (id) do nothing;

-- Session attached to a room and multiple speakers
-- (event_id is NOT NULL per schema, so this session is also attached to an event)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('c6e17cef-1314-4e12-87fa-61f52bdc616f', 'GET Sessions Multiple Speakers', 'Session with multiple speakers for GET endpoint testing', '2026-05-24 00:00:00', '2026-05-24 23:59:00', '54116228-a730-42d6-a792-b31080a0142d', 50, '8819ef47-f477-4133-9da2-d387cc7c9b1a')
on conflict (id) do nothing;

insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('39a9ac90-42d0-42fb-bc43-c1f8e3ba2da2', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', 'c6e17cef-1314-4e12-87fa-61f52bdc616f', '12:00:00+02', '13:00:00+02'),
    ('871939dd-c21c-41dd-8be2-06e2d65649cd', '4f17fb62-25cc-4758-a747-8f7df562d425', 'c6e17cef-1314-4e12-87fa-61f52bdc616f', '13:00:00+02', '14:00:00+02')
on conflict (id) do nothing;

-- Session attached to a room and an event, but no speaker (no intervene records)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('37df0f32-c10a-45a0-97d0-ab5955dc3a77', 'GET Sessions No Speaker', 'Session without any speaker for GET endpoint testing', '2026-07-10 14:00:00', '2026-07-10 15:00:00', '05135b86-f3b7-4a27-8736-8415fe95ddf8', 40, '8819ef47-f477-4133-9da2-d387cc7c9b1a')
on conflict (id) do nothing;

-- Test data for PUT /sessions/{id} endpoint (self-contained, independent of other seed files)

-- Room for PUT session testing
insert into eventsync_app.rooms (id, name)
values
    ('7a3b1c2d-4e5f-6a7b-8c9d-0e1f2a3b4c5d', 'PUT Session Room')
on conflict (id) do nothing;

-- Event for PUT session testing
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('8b4c3d2e-5f6a-7b8c-9d0e-1f2a3b4c5d6e', 'PUT Session Event', 'Event for testing PUT session endpoint', '2026-06-15 09:00:00', '2026-06-15 18:00:00', 'PUT Test Location')
on conflict (id) do nothing;

-- Speakers (users with role 'speaker') for PUT session testing
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('9c5d4e3f-6a7b-8c9d-0e1f-2a3b4c5d6e7f', 'Peter', 'Put', 'peter.put@eventsync.com', 'Speaker for PUT session endpoint testing.', 'https://i.pravatar.cc/150?img=30', 'speaker'),
    ('0d6e5f4a-7b8c-9d0e-1f2a-3b4c5d6e7f8a', 'Paula', 'Put', 'paula.put@eventsync.com', 'Speaker for PUT session endpoint testing.', 'https://i.pravatar.cc/150?img=31', 'speaker')
on conflict (id) do nothing;

-- Session to update via PUT /sessions/{id} (attached to room, event, and speakers)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('1e7f6a5b-8c9d-0e1f-2a3b-4c5d6e7f8a9b', 'PUT Session to Update', 'Session for testing PUT endpoint', '2026-07-15 10:00:00', '2026-07-15 12:00:00', '7a3b1c2d-4e5f-6a7b-8c9d-0e1f2a3b4c5d', 60, '8b4c3d2e-5f6a-7b8c-9d0e-1f2a3b4c5d6e')
on conflict (id) do nothing;

-- Intervene records linking speakers to the PUT session
insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('2f8a7b6c-9d0e-1f2a-3b4c-5d6e7f8a9b0c', '9c5d4e3f-6a7b-8c9d-0e1f-2a3b4c5d6e7f', '1e7f6a5b-8c9d-0e1f-2a3b-4c5d6e7f8a9b', '10:00:00+02', '11:00:00+02'),
    ('3a9b8c7d-0e1f-2a3b-4c5d-6e7f8a9b0c1d', '0d6e5f4a-7b8c-9d0e-1f2a-3b4c5d6e7f8a', '1e7f6a5b-8c9d-0e1f-2a3b-4c5d6e7f8a9b', '11:00:00+02', '12:00:00+02')
on conflict (id) do nothing;
