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
    user_id uuid references eventsync_app.users(id)
);