-- Questions for testing GET /sessions/{id}/questions endpoint

insert into eventsync_app.question (id, title, content, created_at, session_id, user_id, anonymous)
values
    ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'Will AI replace developers?', 'I am curious about the impact of AI on software development jobs in the next 5 years.', '2026-05-10 10:05:00', '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', false),
    ('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'What is the best language for AI?', 'Which programming language is recommended for beginners in AI development?', '2026-05-10 10:12:00', '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f', '4f17fb62-25cc-4758-a747-8f7df562d425', false),
    ('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'Anonymous question about salary', 'What is the average salary for a senior developer in Europe?', '2026-05-10 10:20:00', '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f', '0a635d21-c174-4525-9031-19848bed99a4', true),
    ('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', 'Remote work trends', 'How is remote work shaping the future of tech companies?', '2026-05-10 10:30:00', '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f', 'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e', false)
on conflict (id) do nothing;

-- Questions for session: Web Development Basics (4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a)
insert into eventsync_app.question (id, title, content, created_at, session_id, user_id, anonymous)
values
    ('e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'React vs Angular in 2026?', 'Which framework is more popular for enterprise applications this year?', '2026-05-14 09:15:00', '4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a', 'c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f', false),
    ('f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8a9b0c', 'Best CSS framework?', 'What CSS framework do you recommend for rapid prototyping?', '2026-05-14 10:00:00', '4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a', 'd3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f7a', false)
on conflict (id) do nothing;

-- Questions for session: Testing Strategies (7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d)
insert into eventsync_app.question (id, title, content, created_at, session_id, user_id, anonymous)
values
    ('a7b8c9d0-e1f2-4a3b-5c6d-7e8f9a0b1c2d', 'Mocking best practices?', 'What are the best practices for mocking external services in Spring Boot tests?', '2026-05-14 10:30:00', '7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', '4f17fb62-25cc-4758-a747-8f7df562d425', false),
    ('b8c9d0e1-f2a3-4b5c-6d7e-8f9a0b1c2d3e', 'Test coverage target?', 'What is a good test coverage percentage to aim for?', '2026-05-14 11:00:00', '7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d', '0a635d21-c174-4525-9031-19848bed99a4', false)
on conflict (id) do nothing;
