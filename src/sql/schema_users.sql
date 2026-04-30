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