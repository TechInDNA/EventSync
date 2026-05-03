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
    ('d2c068bd-cee8-592b-c9g8-747e54815g93','Event to Delete', 'Test event for DELETE endpoint', '2026-12-15 10:00:00', '2026-12-15 17:00:00', 'Delete Test Location');
