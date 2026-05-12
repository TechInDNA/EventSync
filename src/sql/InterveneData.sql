-- Test data for intervene (speaker-session assignments)
-- Run after SessionData.sql and speaker_data.sql have been executed

-- Intervene record with specific ID for DELETE/PUT endpoint testing
insert into eventsync_app.intervene (id, speaker_id, session_id, start_time, end_time)
select
    '33333333-3333-3333-3333-333333333333',
    u.id,
    s.id,
    '09:00:00+00',
    '10:30:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'test.speaker@eventsync.com'
  and s.title = 'Keynote: Future of Tech'
  and not exists (select 1 from eventsync_app.intervene where id = '33333333-3333-3333-3333-333333333333');

-- John Doe intervenes on 'Keynote: Future of Tech'
insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '09:00:00+00', '10:30:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'john.speaker@eventsync.com'
  and s.title = 'Keynote: Future of Tech';

-- Jane Smith intervenes on 'AI Workshop: ML Basics'
insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '11:00:00+00', '13:00:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'jane.speaker@eventsync.com'
  and s.title = 'AI Workshop: ML Basics';

-- Bob Johnson intervenes on 'Cloud Computing Panel'
insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '14:00:00+00', '15:30:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'bob.speaker@eventsync.com'
  and s.title = 'Cloud Computing Panel';

-- Alice Williams intervenes on 'Cybersecurity Trends 2026'
insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '10:00:00+00', '12:00:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'alice.speaker@eventsync.com'
  and s.title = 'Cybersecurity Trends 2026';

-- Charlie Brown intervenes on 'React Native Workshop'
insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '14:00:00+00', '17:00:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'charlie.speaker@eventsync.com'
  and s.title = 'React Native Workshop';

-- Diana Miller intervenes on 'Data Science with Python'
insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '09:30:00+00', '11:30:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'diana.speaker@eventsync.com'
  and s.title = 'Data Science with Python';

-- Multiple speakers on same session: Eve Davis and Frank Garcia on 'Cloud Computing Panel'
insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '14:00:00+00', '15:30:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'eve.speaker@eventsync.com'
  and s.title = 'Cloud Computing Panel';

insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '14:00:00+00', '15:30:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'frank.speaker@eventsync.com'
  and s.title = 'Cloud Computing Panel';

-- Grace Lopez intervenes on 'Session to Update' (fixed UUID session)
insert into eventsync_app.intervene (speaker_id, session_id, start_time, end_time)
select u.id, s.id, '10:00:00+00', '12:00:00+00'
from eventsync_app.users u
cross join eventsync_app.sessions s
where u.email = 'grace.speaker@eventsync.com'
  and s.id = 'f47ac10b-58cc-4372-a567-0e02b2c3d479';
