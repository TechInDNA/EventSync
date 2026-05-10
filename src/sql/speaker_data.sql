-- Test speaker for PUT /speaker/{id} and DELETE PUT /speaker/{id} endpoint (use this ID to test update)
insert into eventsync_app.users (id, first_name, last_name, email, bio, profile_picture, "role")
values
    ('27dfb67f-b1c5-4f71-b0b2-2190674eefa8', 'Test', 'Speaker', 'test.speaker@eventsync.com', 'Test speaker bio for PUT endpoint testing.', 'https://i.pravatar.cc/150?img=20', 'speaker');

-- External links for test speaker (PUT /speaker/{id} and DELETE /speaker/{id} endpoint testing)
insert into eventsync_app.external_link (name, url, user_id)
values
    ('Personal Website', 'https://testspeaker.dev', '27dfb67f-b1c5-4f71-b0b2-2190674eefa8'),
    ('GitHub', 'https://github.com/testspeaker', '27dfb67f-b1c5-4f71-b0b2-2190674eefa8');
