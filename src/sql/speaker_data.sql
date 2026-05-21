-- Test speaker for PUT /speaker/{id} and DELETE /speaker/{id} endpoint (use this ID to test update)
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('27dfb67f-b1c5-4f71-b0b2-2190674eefa8', 'Test', 'Speaker', 'test.speaker@eventsync.com', 'Test speaker bio for PUT endpoint testing.', 'https://i.pravatar.cc/150?img=20', 'speaker')
on conflict (id) do nothing;

-- External links for test speaker (PUT /speaker/{id} and DELETE /speaker/{id} endpoint testing)
insert into eventsync_app.external_link (name, url, user_id)
values
    ('Personal Website', 'https://testspeaker.dev', '27dfb67f-b1c5-4f71-b0b2-2190674eefa8'),
    ('GitHub', 'https://github.com/testspeaker', '27dfb67f-b1c5-4f71-b0b2-2190674eefa8')
on conflict (url) do nothing;


-- Speakers for testing GET /speakers endpoint
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e', 'John', 'Doe', 'john.speaker@eventsync.com', 'Senior software engineer specializing in backend development with 10+ years of experience.', 'https://i.pravatar.cc/150?img=1', 'speaker'),
    ('4f17fb62-25cc-4758-a747-8f7df562d425', 'Jane', 'Smith', 'jane.speaker@eventsync.com', 'UX/UI designer focused on creating intuitive user experiences for web and mobile apps.', 'https://i.pravatar.cc/150?img=2', 'speaker'),
    ('0a635d21-c174-4525-9031-19848bed99a4', 'Bob', 'Johnson', 'bob.speaker@eventsync.com', 'Full-stack developer and tech enthusiast sharing knowledge through blogs and workshops.', 'https://i.pravatar.cc/150?img=3', 'speaker'),
    ('b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e', 'Alice', 'Williams', 'alice.speaker@eventsync.com', 'Data scientist with expertise in machine learning and AI model deployment.', 'https://i.pravatar.cc/150?img=4', 'speaker'),
    ('c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f', 'Charlie', 'Brown', 'charlie.speaker@eventsync.com', 'DevOps engineer passionate about CI/CD pipelines and cloud infrastructure.', 'https://i.pravatar.cc/150?img=5', 'speaker'),
    ('d3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f7a', 'Diana', 'Miller', 'diana.speaker@eventsync.com', 'Cybersecurity expert helping organizations protect their digital assets.', 'https://i.pravatar.cc/150?img=6', 'speaker'),
    ('e4f5a6b7-c8d9-4e0f-1a2b-3c4d5e6f7a8b', 'Eve', 'Davis', 'eve.speaker@eventsync.com', 'Mobile app developer specializing in React Native and Flutter frameworks.', 'https://i.pravatar.cc/150?img=7', 'speaker'),
    ('f5a6b7c8-d9e0-4f1a-2b3c-4d5e6f7a8b9c', 'Frank', 'Garcia', 'frank.speaker@eventsync.com', 'Blockchain developer and cryptocurrency enthusiast with 5+ years of experience.', 'https://i.pravatar.cc/150?img=8', 'speaker'),
    ('a6b7c8d9-e0f1-4a2b-3c4d-5e6f7a8b9c0d', 'Grace', 'Lopez', 'grace.speaker@eventsync.com', 'Product manager with a strong technical background in agile methodologies.', 'https://i.pravatar.cc/150?img=9', 'speaker'),
    ('b7c8d9e0-f1a2-4b3c-5d6e-7f8a9b0c1d2e', 'Henry', 'Martinez', 'henry.speaker@eventsync.com', 'Cloud architect designing scalable solutions on AWS and Azure platforms.', 'https://i.pravatar.cc/150?img=10', 'speaker'),
    ('c8d9e0f1-a2b3-4c5d-6e7f-8a9b0c1d2e3f', 'Ivy', 'Anderson', 'ivy.speaker@eventsync.com', 'QA engineer focused on automated testing and ensuring software quality.', 'https://i.pravatar.cc/150?img=11', 'speaker'),
    ('d9e0f1a2-b3c4-4d5e-6f7a-8b9c0d1e2f3a', 'Jack', 'Thomas', 'jack.speaker@eventsync.com', 'IoT specialist building smart devices and connected systems.', 'https://i.pravatar.cc/150?img=12', 'speaker'),
    ('e0f1a2b3-c4d5-4e6f-7a8b-9c0d1e2f3a4b', 'Karen', 'Jackson', 'karen.speaker@eventsync.com', 'Technical writer creating clear documentation for complex software products.', 'https://i.pravatar.cc/150?img=13', 'speaker'),
    ('f1a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', 'Leo', 'White', 'leo.speaker@eventsync.com', 'Game developer with experience in Unity and Unreal Engine.', 'https://i.pravatar.cc/150?img=14', 'speaker'),
    ('a2b3c4d5-e6f7-4a8b-9c0d-1e2f3a4b5c6d', 'Mia', 'Harris', 'mia.speaker@eventsync.com', 'AI researcher exploring natural language processing and computer vision.', 'https://i.pravatar.cc/150?img=15', 'speaker')
on conflict (id) do nothing;

-- Speakers for testing POST /speakers/{id}/external-link endpoint
-- (45047c18 has no links, be8bfef5 has one link)
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('45047c18-1984-4d1e-bcbc-a7407c227292', 'Alice', 'External', 'alice.external@eventsync.com', 'Speaker without external links for testing POST external-link endpoint.', null, 'speaker'),
    ('be8bfef5-d76f-4b54-a7aa-8209b17b5a54', 'Bob', 'External', 'bob.external@eventsync.com', 'Speaker with an existing external link for testing POST external-link endpoint.', null, 'speaker')
on conflict (id) do nothing;

insert into eventsync_app.external_link (name, url, user_id)
values
    ('Website', 'https://bobexternal.dev', 'be8bfef5-d76f-4b54-a7aa-8209b17b5a54')
on conflict (url) do nothing;

-- External links for testing GET /speakers endpoint
insert into eventsync_app.external_link (name, url, user_id)
values
    ('Personal Website', 'https://johndoe.dev', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e'),
    ('GitHub', 'https://github.com/johndoe', 'af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e'),
    ('LinkedIn', 'https://linkedin.com/in/janesmith', '4f17fb62-25cc-4758-a747-8f7df562d425'),
    ('Portfolio', 'https://janesmith.design', '4f17fb62-25cc-4758-a747-8f7df562d425'),
    ('Twitter', 'https://twitter.com/bobJohnson', '0a635d21-c174-4525-9031-19848bed99a4'),
    ('GitHub', 'https://github.com/alicewilliams', 'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e'),
    ('LinkedIn', 'https://linkedin.com/in/alicewilliams', 'b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e'),
    ('Website', 'https://charliebrown.dev', 'c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f'),
    ('Twitter', 'https://twitter.com/charliebrown', 'c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f'),
    ('LinkedIn', 'https://linkedin.com/in/dianamiller', 'd3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f7a'),
    ('Blog', 'https://dianamiller.security', 'd3e4f5a6-b7c8-4d9e-0f1a-2b3c4d5e6f7a'),
    ('GitHub', 'https://github.com/evedavis', 'e4f5a6b7-c8d9-4e0f-1a2b-3c4d5e6f7a8b'),
    ('Portfolio', 'https://evedavis.mobile', 'e4f5a6b7-c8d9-4e0f-1a2b-3c4d5e6f7a8b'),
    ('LinkedIn', 'https://linkedin.com/in/frankgarcia', 'f5a6b7c8-d9e0-4f1a-2b3c-4d5e6f7a8b9c'),
    ('GitHub', 'https://github.com/gracelopez', 'a6b7c8d9-e0f1-4a2b-3c4d-5e6f7a8b9c0d'),
    ('Website', 'https://gracelopez.pm', 'a6b7c8d9-e0f1-4a2b-3c4d-5e6f7a8b9c0d'),
    ('Twitter', 'https://twitter.com/henrymartinez', 'b7c8d9e0-f1a2-4b3c-5d6e-7f8a9b0c1d2e'),
    ('LinkedIn', 'https://linkedin.com/in/henrymartinez', 'b7c8d9e0-f1a2-4b3c-5d6e-7f8a9b0c1d2e'),
    ('GitHub', 'https://github.com/ivyanderson', 'c8d9e0f1-a2b3-4c5d-6e7f-8a9b0c1d2e3f'),
    ('LinkedIn', 'https://linkedin.com/in/ivyanderson', 'c8d9e0f1-a2b3-4c5d-6e7f-8a9b0c1d2e3f'),
    ('Portfolio', 'https://jackthomas.iot', 'd9e0f1a2-b3c4-4d5e-6f7a-8b9c0d1e2f3a'),
    ('Twitter', 'https://twitter.com/jackthomas', 'd9e0f1a2-b3c4-4d5e-6f7a-8b9c0d1e2f3a'),
    ('Blog', 'https://karenjackson.tech', 'e0f1a2b3-c4d5-4e6f-7a8b-9c0d1e2f3a4b'),
    ('LinkedIn', 'https://linkedin.com/in/karenjackson', 'e0f1a2b3-c4d5-4e6f-7a8b-9c0d1e2f3a4b'),
    ('GitHub', 'https://github.com/leowhite', 'f1a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c'),
    ('Website', 'https://leowhite.games', 'f1a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c'),
    ('LinkedIn', 'https://linkedin.com/in/miaharris', 'a2b3c4d5-e6f7-4a8b-9c0d-1e2f3a4b5c6d'),
    ('Research Gate', 'https://researchgate.net/miaharris', 'a2b3c4d5-e6f7-4a8b-9c0d-1e2f3a4b5c6d')
on conflict (url) do nothing;
