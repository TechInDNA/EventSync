create type eventsync_app."role" as enum ('admin', 'speaker', 'participant');

create table eventsync_app.users(
    id uuid default gen_random_uuid() primary key,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    email varchar(50) unique not null,
    password varchar(100),
    bio text,
    created_at timestamp default now(),
    profile_picture varchar(255),
    "role" eventsync_app."role" not null
);

create table eventsync_app.events(
    id uuid default gen_random_uuid() primary key,
    title varchar(50) unique not null,
    description text not null,
    start_date timestamp not null,
    end_date timestamp not null,
    location varchar(50) not null,
    created_at timestamp default now() not null
);

create table eventsync_app.external_link(
    id uuid default gen_random_uuid() primary key,
    name varchar(50),
    url varchar(50) unique,
    user_id uuid references eventsync_app.users(id) on delete cascade
);

create table eventsync_app.rooms(
    id uuid default gen_random_uuid() primary key,
    name varchar(50) unique not null
);

create table eventsync_app.sessions(
    id uuid default gen_random_uuid() primary key,
    title varchar(50) unique not null,
    description text not null,
    start_date timestamp not null,
    end_date timestamp not null,
    room_id uuid not null references eventsync_app.rooms(id),
    capacity int not null default 0,
    event_id uuid not null references eventsync_app.events(id)
);

create table eventsync_app.intervene(
    id uuid default gen_random_uuid() primary key,
    speaker_id uuid not null references eventsync_app.users(id) on delete cascade,
    session_id uuid not null references eventsync_app.sessions(id) on delete cascade,
    start_time time with time zone not null,
    end_time time with time zone not null
);

create table eventsync_app.questions(
    id uuid default gen_random_uuid() primary key,
    title varchar(50),
    content text not null,
    created_at timestamp default now() not null,
    session_id uuid not null references eventsync_app.sessions(id) on delete cascade,
    user_id uuid references eventsync_app.users(id) on delete set null,
    anonymous boolean default false
);



create table eventsync_app.upvotes(
    id uuid default gen_random_uuid() primary key,
    user_id uuid references eventsync_app.users(id) on delete cascade,
    question_id uuid not null references eventsync_app.questions(id) on delete cascade,
    created_at timestamp default now() not null,
    unique(user_id, question_id)
);

create index idx_votes_question_id on eventsync_app.upvotes(question_id);
create index idx_questions_session_id on eventsync_app.questions(session_id);
