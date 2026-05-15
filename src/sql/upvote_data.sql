-- Upvotes for testing GET /sessions/{id}/questions endpoint

insert into eventsync_app.upvote (id, user_id, question_id, created_at)
values
    ('11111111-1111-4111-8111-111111111111', '4f17fb62-25cc-4758-a747-8f7df562d425', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-05-10 10:06:00'),
    ('22222222-2222-4222-8222-222222222222', '0a635d21-c174-4525-9031-19848bed99a4', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-05-10 10:07:00'),
    ('33333333-3333-4333-8333-333333333333', 'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-05-10 10:08:00'),
    ('44444444-4444-4444-8444-444444444444', 'c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-05-10 10:09:00')
on conflict (id) do nothing;

-- Upvotes on question: What is the best language for AI? (b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e)
insert into eventsync_app.upvote (id, user_id, question_id, created_at)
values
    ('55555555-5555-4555-8555-555555555555', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', '2026-05-10 10:13:00'),
    ('66666666-6666-4666-8666-666666666666', 'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e', 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', '2026-05-10 10:14:00')
on conflict (id) do nothing;

-- Upvotes on question: Anonymous question about salary (c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f)
insert into eventsync_app.upvote (id, user_id, question_id, created_at)
values
    ('77777777-7777-4777-8777-777777777777', 'd3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f7a', 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', '2026-05-10 10:21:00'),
    ('88888888-8888-4888-8888-888888888888', 'c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f', 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', '2026-05-10 10:22:00'),
    ('99999999-9999-4999-9999-999999999999', '4f17fb62-25cc-4758-a747-8f7df562d425', 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', '2026-05-10 10:23:00')
on conflict (id) do nothing;

-- Upvotes on question: Remote work trends (d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a)
insert into eventsync_app.upvote (id, user_id, question_id, created_at)
values
    ('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', '0a635d21-c174-4525-9031-19848bed99a4', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', '2026-05-10 10:31:00')
on conflict (id) do nothing;


insert into eventsync_app.upvote (id, user_id, question_id, created_at)
values
    ('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb', 'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e', 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', '2026-05-14 09:16:00'),
    ('cccccccc-cccc-4ccc-8ccc-cccccccccccc', 'd3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f7a', 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', '2026-05-14 09:17:00')
on conflict (id) do nothing;

-- Upvote pour la question 'Remote work trends'
INSERT INTO eventsync_app.upvote (id, user_id, question_id, created_at)
VALUES ('aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa', '0a635d21-c174-4525-9031-19848bed99a4', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', '2026-05-10 10:31:00')
ON CONFLICT (id) DO NOTHING;

-- Upvotes pour la question 'React vs Angular in 2026?'
INSERT INTO eventsync_app.upvote (id, user_id, question_id, created_at)
VALUES
('bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb', 'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e', 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', '2026-05-14 09:16:00'),
('cccccccc-cccc-4ccc-8ccc-cccccccccccc', 'd3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f7a', 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', '2026-05-14 09:17:00')
ON CONFLICT (id) DO NOTHING;