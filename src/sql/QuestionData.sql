-- Test data for questions
-- Run after SessionData.sql has been executed and speakers exist

-- Question with specific ID for DELETE and upvote endpoint testing
insert into eventsync_app.questions (id, title, content, session_id, user_id, anonymous)
select
    '22222222-2222-2222-2222-222222222222',
    'Endpoint Test Question',
    'This question is used for testing DELETE and upvote endpoints.',
    s.id,
    u.id,
    false
from eventsync_app.sessions s
cross join eventsync_app.users u
where s.title = 'Keynote: Future of Tech'
  and u.email = 'admin@eventsync.com'
  and not exists (select 1 from eventsync_app.questions where id = '22222222-2222-2222-2222-222222222222');

-- Vote on the test question (by admin)
insert into eventsync_app.votes (user_id, question_id)
select u.id, '22222222-2222-2222-2222-222222222222'
from eventsync_app.users u
where u.email = 'admin@eventsync.com'
  and not exists (
      select 1 from eventsync_app.votes
      where question_id = '22222222-2222-2222-2222-222222222222'
        and user_id = u.id
  );

-- Question by admin on 'Keynote: Future of Tech'
insert into eventsync_app.questions (title, content, session_id, user_id, anonymous)
select
    'AI and Quantum Computing',
    'How will quantum computing impact AI development in the coming years?',
    s.id,
    u.id,
    false
from eventsync_app.sessions s
cross join eventsync_app.users u
where s.title = 'Keynote: Future of Tech'
  and u.email = 'admin@eventsync.com';

-- Anonymous question on 'Keynote: Future of Tech'
insert into eventsync_app.questions (title, content, session_id, anonymous)
select
    'AGI Timeline',
    'Is AGI achievable in the next decade? What are the main obstacles?',
    s.id,
    true
from eventsync_app.sessions s
where s.title = 'Keynote: Future of Tech';

-- Question by speaker John Doe on 'AI Workshop: ML Basics'
insert into eventsync_app.questions (title, content, session_id, user_id, anonymous)
select
    'Beginner ML Resources',
    'What Python libraries and resources do you recommend for beginners in ML?',
    s.id,
    u.id,
    false
from eventsync_app.sessions s
cross join eventsync_app.users u
where s.title = 'AI Workshop: ML Basics'
  and u.email = 'john.speaker@eventsync.com';

-- Anonymous question on 'AI Workshop: ML Basics' (7 votes)
insert into eventsync_app.questions (title, content, session_id, anonymous)
select
    'Framework Debate',
    'TensorFlow vs PyTorch: which one is better for research in 2026?',
    s.id,
    true
from eventsync_app.sessions s
where s.title = 'AI Workshop: ML Basics';

-- Question by speaker Jane Smith on 'Cloud Computing Panel'
insert into eventsync_app.questions (content, session_id, user_id, anonymous)
select
    'What are the best practices for designing a multi-cloud architecture?',
    s.id,
    u.id,
    false
from eventsync_app.sessions s
cross join eventsync_app.users u
where s.title = 'Cloud Computing Panel'
  and u.email = 'jane.speaker@eventsync.com';

-- Anonymous question on 'Cloud Computing Panel'
insert into eventsync_app.questions (title, content, session_id, anonymous)
select
    'Cost Management',
    'How do you effectively manage and optimize cloud costs across multiple providers?',
    s.id,
    true
from eventsync_app.sessions s
where s.title = 'Cloud Computing Panel';

-- Question by admin on 'Session to Update' (fixed UUID session)
insert into eventsync_app.questions (title, content, session_id, user_id, anonymous)
select
    'Session Update Query',
    'What specific topics will be covered in the updated version of this session?',
    s.id,
    u.id,
    false
from eventsync_app.sessions s
cross join eventsync_app.users u
where s.id = 'f47ac10b-58cc-4372-a567-0e02b2c3d479'
  and u.email = 'admin@eventsync.com';
