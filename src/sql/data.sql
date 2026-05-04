insert into eventsync_app.users (first_name, last_name, email, password, "role")
values (
    'Admin',
    'Admin',
    'admin@eventsync.com',
    '$argon2id$v=19$m=16384,t=2,p=1$MHYuT/dnMM16eVdoA3GNkQ$cwPs80AXs0wAhgEv+dvuUstWX5dvlReNAOP+Pd6fUDQ',
    'admin'
);

-- admin hashed password: test

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
    ('Python Coding Bootcamp', 'Intensive Python programming bootcamp', '2026-09-05 10:00:00', '2026-09-07 16:00:00', 'Code Academy');

-- Test event for PUT /events endpoint (use this ID to test update)
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('c1b957ac-bdd7-481a-b8f7-636d43794f82','Event to Update', 'Original description for testing PUT endpoint', '2026-12-01 09:00:00', '2026-12-01 18:00:00', 'Test Location');

-- Test event for DELETE /events endpoint (use this ID to test deletion)
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('a7cc7aac-50db-44cd-b8ae-cac7737a4052','Event to Delete', 'Test event for DELETE endpoint', '2026-12-15 10:00:00', '2026-12-15 17:00:00', 'Delete Test Location');

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
    ('a2b3c4d5-e6f7-4a8b-9c0d-1e2f3a4b5c6d', 'Mia', 'Harris', 'mia.speaker@eventsync.com', 'AI researcher exploring natural language processing and computer vision.', 'https://i.pravatar.cc/150?img=15', 'speaker');

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
    ('Research Gate', 'https://researchgate.net/miaharris', 'a2b3c4d5-e6f7-4a8b-9c0d-1e2f3a4b5c6d');

-- Rooms for testing GET /rooms endpoint (at least 5 rooms)
insert into eventsync_app.rooms (name)
values
    ('Grand Ballroom'),
    ('Conference Room A'),
    ('Conference Room B'),
    ('Workshop Hall'),
    ('Seminar Room 1'),
    ('Seminar Room 2'),
    ('Exhibition Hall');

-- Test room for DELETE /rooms/{id} endpoint (use this ID to test deletion)
insert into eventsync_app.rooms (id, name)
values
    ('48549c21-6dc0-4e9c-9f57-c0ae88f6544b', 'Room to Delete');

-- Test room for PUT /rooms/{id} endpoint (use this ID to test update)
insert into eventsync_app.rooms (id, name)
values
    ('c1c12204-4427-4add-b755-b681719d1684', 'Room to Update');
