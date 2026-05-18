-- Test data for GET /sessions/{id}/questions endpoint
-- This endpoint retrieves paginated questions for a session, with sorting by creationDate (default) or upvotes,
-- and supports anonymous questions (participant info hidden).

-- Participant users who will ask questions (also used by upvotes)
insert into eventsync_app.users (id, first_name, last_name, email, "role")
values
    ('43005ca0-37f7-4942-86ac-695a29a6a3b1', 'Alice', 'Johnson', 'alice.participant@eventsync.com', 'participant'),
    ('ad279f61-3883-42b1-8b03-1ebbeda3e4bf', 'Bob', 'Smith', 'bob.participant@eventsync.com', 'participant'),
    ('dd307c1a-c674-4969-99e3-91ad37cc64dd', 'Charlie', 'Brown', 'charlie.participant@eventsync.com', 'participant'),
    ('97bc7f5d-1e00-4cef-a661-4133ab0e1667', 'Diana', 'Miller', 'diana.participant@eventsync.com', 'participant'),
    ('017cdf3d-fa6e-42f5-958d-91a192c90118', 'Eve', 'Davis', 'eve.participant@eventsync.com', 'participant')
on conflict (id) do nothing;

-- Room for the test session
insert into eventsync_app.rooms (id, name)
values ('26ecdf60-70e9-41ec-80f4-4e88571661ce', 'Questions Test Room')
on conflict (id) do nothing;

-- Event for the test session
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values (
    '73362766-dc21-4ced-9ec4-db0819ea442f',
    'Questions Test Event',
    'Event for testing GET /sessions/{id}/questions endpoint',
    '2026-06-10 09:00:00',
    '2026-06-10 18:00:00',
    'Question Test Center'
)
on conflict (id) do nothing;

-- Session whose questions will be fetched via GET /sessions/{id}/questions
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values (
    'a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb',
    'Questions Test Session',
    'Session for testing the questions retrieval endpoint',
    '2026-06-10 10:00:00',
    '2026-06-10 12:00:00',
    '26ecdf60-70e9-41ec-80f4-4e88571661ce',
    100,
    '73362766-dc21-4ced-9ec4-db0819ea442f'
)
on conflict (id) do nothing;

-- Questions for the session (mix of regular and anonymous)
insert into eventsync_app.question (id, title, content, created_at, session_id, user_id, anonymous)
values
    ('ddf75918-e752-4fb1-b316-cb4574da81de', 'Question about the topic',
     'Will this session cover microservices architecture patterns?',
     '2026-06-10 10:05:00',
     'a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb',
     '43005ca0-37f7-4942-86ac-695a29a6a3b1',
     false),
    ('20919e99-f2ff-429d-a6af-8cba749af27a', 'Anonymous question',
     'Is there any prerequisite knowledge required for this session?',
     '2026-06-10 10:10:00',
     'a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb',
     'ad279f61-3883-42b1-8b03-1ebbeda3e4bf',
     true),
    ('2110b88d-b648-44fb-a2c0-6a2f5a674eb7', 'Slides availability',
     'Will the presentation slides be shared after the session?',
     '2026-06-10 10:15:00',
     'a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb',
     'dd307c1a-c674-4969-99e3-91ad37cc64dd',
     false),
    ('0067a56e-84d8-443a-a686-b5b124b01342', 'Hands-on exercise question',
     'Will there be any hands-on coding exercises during this session?',
     '2026-06-10 10:20:00',
     'a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb',
     '97bc7f5d-1e00-4cef-a661-4133ab0e1667',
     false),
    ('a6631e77-eeeb-4551-aefc-11b17b681e46', 'Anonymous feedback question',
     'Is the session going to be recorded for later viewing?',
     '2026-06-10 10:25:00',
     'a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb',
     '017cdf3d-fa6e-42f5-958d-91a192c90118',
     true),
    ('403dea58-0f1b-4c6f-81dd-5d6cb7dcd790', 'Recommended reading',
     'Are there any recommended books or articles to read before this session?',
     '2026-06-10 10:30:00',
     'a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb',
     '43005ca0-37f7-4942-86ac-695a29a6a3b1',
     false)
on conflict (id) do nothing;

-- Upvotes on questions (to test upvote_count sorting)
insert into eventsync_app.upvote (id, user_id, question_id)
values
    ('69666a9b-34c8-4929-9ab2-a2c88e058a11', 'ad279f61-3883-42b1-8b03-1ebbeda3e4bf', 'ddf75918-e752-4fb1-b316-cb4574da81de'),
    ('637fb91c-fb1a-4403-9c3d-abd79e0b1c95', 'dd307c1a-c674-4969-99e3-91ad37cc64dd', 'ddf75918-e752-4fb1-b316-cb4574da81de'),
    ('21d5abd2-88a6-4172-bde7-2e07b8431a44', '97bc7f5d-1e00-4cef-a661-4133ab0e1667', 'ddf75918-e752-4fb1-b316-cb4574da81de'),
    ('cbc5d7ef-a308-4b84-8344-c704f3e0bbeb', '43005ca0-37f7-4942-86ac-695a29a6a3b1', '2110b88d-b648-44fb-a2c0-6a2f5a674eb7'),
    ('88882c47-6495-46b6-a861-e8e68e46912a', 'ad279f61-3883-42b1-8b03-1ebbeda3e4bf', '2110b88d-b648-44fb-a2c0-6a2f5a674eb7'),
    ('b97e7a6c-f02f-47b2-8332-c4ea9a2df018', 'dd307c1a-c674-4969-99e3-91ad37cc64dd', '20919e99-f2ff-429d-a6af-8cba749af27a'),
    ('c39f8185-f077-4d8d-9706-00f882cb309a', '97bc7f5d-1e00-4cef-a661-4133ab0e1667', '403dea58-0f1b-4c6f-81dd-5d6cb7dcd790')
on conflict (id) do nothing;
