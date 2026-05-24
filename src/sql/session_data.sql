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

-- Users needed for cascade delete tests (must precede intervene/question FK references)
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', 'John', 'Doe', 'john.getsession@eventsync.com', 'Speaker for cascade and GET sessions testing.', 'https://i.pravatar.cc/150?img=1', 'speaker'),
    ('4f17fb62-25cc-4758-a747-8f7df562d425', 'Jane', 'Smith', 'jane.getsession@eventsync.com', 'Speaker for cascade and GET sessions testing.', 'https://i.pravatar.cc/150?img=2', 'speaker')
on conflict (id) do nothing;

-- Participant for cascade delete question
insert into eventsync_app.users (id, first_name, last_name, email, "role")
values
    ('43005ca0-37f7-4942-86ac-695a29a6a3b1', 'Charlie', 'Cascade', 'charlie.cascade@eventsync.com', 'participant')
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

-- Test data for GET /sessions/{id} endpoint

-- Room for session detail testing
insert into eventsync_app.rooms (id, name)
values
    ('7bb327b5-00d9-4d22-a974-f1f5468a2d1c', 'Detail Room'),
    ('eceb8b27-9286-41e8-8f06-76e42b49f511', 'No Speaker Room')
on conflict (id) do nothing;

-- Events for session detail testing
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('2d2d807b-b072-4317-8f2f-db3d6ba32ecf', 'Detail Event', 'Event for session detail testing', '2026-05-24 09:00:00', '2026-05-24 18:00:00', 'Detail Location'),
    ('ca9c5bbd-dc2f-4c89-885f-b0818a6684d0', 'No Speaker Event', 'Event for session with no speaker testing', '2026-05-25 09:00:00', '2026-05-25 18:00:00', 'No Speaker Location'),
    ('3cc83249-05a7-4b44-ab75-fdfe1629d284', 'No Room Event', 'Event for session with no room testing', '2026-05-26 09:00:00', '2026-05-26 18:00:00', 'No Room Location')
on conflict (id) do nothing;

-- Speakers for session detail testing
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('ba7bd06f-9891-4c54-8684-7b51ff294374', 'Alice', 'Detail', 'alice.detail@eventsync.com', 'Speaker for session detail testing.', 'https://i.pravatar.cc/150?img=10', 'speaker'),
    ('a62eabcb-d660-45f3-8de7-23cbee9cc864', 'Bob', 'Detail', 'bob.detail@eventsync.com', 'Speaker for session detail testing.', 'https://i.pravatar.cc/150?img=11', 'speaker'),
    ('547ac520-cdcc-4e78-b647-ef101d6e5ba8', 'Diana', 'NoRoom', 'diana.noroom@eventsync.com', 'Speaker for no-room session testing.', 'https://i.pravatar.cc/150?img=12', 'speaker')
on conflict (id) do nothing;

-- Participant for session detail questions
insert into eventsync_app.users (id, first_name, last_name, email, "role")
values
    ('9c3d7ef6-da2d-4570-85bc-f0de05376312', 'Charlie', 'Participant', 'charlie.participant@eventsync.com', 'participant'),
    ('4fde4c33-3509-452d-848f-9af9f6f13089', 'Eve', 'NoRoomParticipant', 'eve.noroom@eventsync.com', 'participant')
on conflict (id) do nothing;

-- Session 1: full session with room, event, 2 speakers, 3 questions (1 anonymous)
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('7d1b2c1f-2887-4b33-94f9-c3d204c4ab56', 'Detail Session', 'Session with speakers and questions for GET /sessions/{id} testing', '2026-05-24 10:00:00', '2026-05-24 12:00:00', '7bb327b5-00d9-4d22-a974-f1f5468a2d1c', 50, '2d2d807b-b072-4317-8f2f-db3d6ba32ecf')
on conflict (id) do nothing;

-- Intervene records for session 1 (2 speakers)
insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('ac5dcc25-1e7e-402c-bca9-3cf58b4c9a62', 'ba7bd06f-9891-4c54-8684-7b51ff294374', '7d1b2c1f-2887-4b33-94f9-c3d204c4ab56', '10:00:00+02', '11:00:00+02'),
    ('f14c3d15-cf61-4b7f-ae5e-1e635df67eae', 'a62eabcb-d660-45f3-8de7-23cbee9cc864', '7d1b2c1f-2887-4b33-94f9-c3d204c4ab56', '11:00:00+02', '12:00:00+02')
on conflict (id) do nothing;

-- Questions for session 1 (3 questions, 1 anonymous)
insert into eventsync_app.question (id, title, content, created_at, session_id, user_id, anonymous)
values
    ('3eee3ddd-0456-4f74-b61c-dd23dc9f29aa', 'First question', 'What topics will be covered?', '2026-05-24 10:05:00', '7d1b2c1f-2887-4b33-94f9-c3d204c4ab56', '9c3d7ef6-da2d-4570-85bc-f0de05376312', false),
    ('4af1cde5-ce37-4386-8bb1-748c89b60ce7', 'Second question', 'Will there be a Q&A session?', '2026-05-24 10:10:00', '7d1b2c1f-2887-4b33-94f9-c3d204c4ab56', '9c3d7ef6-da2d-4570-85bc-f0de05376312', false),
    ('7ed4a7ee-893e-4efe-9203-acec224e1345', 'Anonymous question', 'Is this session being recorded?', '2026-05-24 10:15:00', '7d1b2c1f-2887-4b33-94f9-c3d204c4ab56', '9c3d7ef6-da2d-4570-85bc-f0de05376312', true)
on conflict (id) do nothing;

-- Session 2: session with room and event, no speaker, no questions
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('afb87ca5-1265-44b8-8ae3-dc59bec528a8', 'No Speaker Session', 'Session without any speaker or question for testing', '2026-05-25 14:00:00', '2026-05-25 16:00:00', 'eceb8b27-9286-41e8-8f06-76e42b49f511', 30, 'ca9c5bbd-dc2f-4c89-885f-b0818a6684d0')
on conflict (id) do nothing;

-- Session 3: session with no room, has event, speaker, and 1 question
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('e1fbfb54-3e09-4a00-bc64-9a6dc2112b73', 'No Room Session', 'Session without a room for testing', '2026-05-26 09:00:00', '2026-05-26 10:00:00', null, 20, '3cc83249-05a7-4b44-ab75-fdfe1629d284')
on conflict (id) do nothing;

-- Intervene for session 3 (1 speaker)
insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
values
    ('1732d798-c42a-4aa6-aacb-e02f5962c919', '547ac520-cdcc-4e78-b647-ef101d6e5ba8', 'e1fbfb54-3e09-4a00-bc64-9a6dc2112b73', '09:00:00+02', '10:00:00+02')
on conflict (id) do nothing;

-- Question for session 3 (1 question)
insert into eventsync_app.question (id, title, content, created_at, session_id, user_id, anonymous)
values
    ('bad08341-8ba1-4c69-aa11-d41cd3c64c1c', 'Solo question', 'Is this the only question?', '2026-05-26 09:05:00', 'e1fbfb54-3e09-4a00-bc64-9a6dc2112b73', '4fde4c33-3509-452d-848f-9af9f6f13089', false)
on conflict (id) do nothing;
