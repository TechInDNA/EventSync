DO $$ BEGIN
    CREATE TYPE eventsync_app."role" AS ENUM ('admin', 'speaker', 'participant');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS eventsync_app.users(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    email varchar(50) UNIQUE NOT NULL,
    password varchar(100),
    bio text,
    created_at timestamp DEFAULT now(),
    profile_picture varchar(255),
    "role" eventsync_app."role" NOT NULL
);

CREATE TABLE IF NOT EXISTS eventsync_app.events(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    title varchar(50) UNIQUE NOT NULL,
    description text NOT NULL,
    start_date timestamp NOT NULL,
    end_date timestamp NOT NULL,
    location varchar(50) NOT NULL,
    created_at timestamp DEFAULT now() NOT NULL
);

CREATE TABLE IF NOT EXISTS eventsync_app.external_link(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    name varchar(50),
    url varchar(50) UNIQUE,
    user_id uuid REFERENCES eventsync_app.users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS eventsync_app.rooms(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    name varchar(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS eventsync_app.sessions(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    title varchar(50) UNIQUE NOT NULL,
    description text NOT NULL,
    start_date timestamp NOT NULL,
    end_date timestamp NOT NULL,
    room_id uuid REFERENCES eventsync_app.rooms(id) ON DELETE SET NULL,
    capacity int NOT NULL DEFAULT 0,
    event_id uuid NOT NULL REFERENCES eventsync_app.events(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS eventsync_app.intervene(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    speaker_id uuid NOT NULL REFERENCES eventsync_app.users(id) ON DELETE CASCADE,
    session_id uuid NOT NULL REFERENCES eventsync_app.sessions(id) ON DELETE CASCADE,
    start_time timetz NOT NULL,
    end_time timetz NOT NULL
);

CREATE TABLE IF NOT EXISTS eventsync_app.question(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    title varchar NOT NULL,
    content text NOT NULL,
    created_at timestamp DEFAULT now(),
    session_id uuid NOT NULL REFERENCES eventsync_app.sessions(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES eventsync_app.users(id) ON DELETE SET NULL,
    anonymous boolean DEFAULT false
);

CREATE TABLE IF NOT EXISTS eventsync_app.upvote(
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES eventsync_app.users(id) ON DELETE SET NULL,
    question_id uuid NOT NULL REFERENCES eventsync_app.question(id) ON DELETE CASCADE,
    created_at timestamp DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_intervene_session_id ON
    eventsync_app.intervene(session_id);

CREATE INDEX IF NOT EXISTS idx_question_session_id ON
    eventsync_app.question(session_id);

CREATE INDEX IF NOT EXISTS idx_intervene_speaker_id ON
    eventsync_app.intervene(speaker_id);

CREATE INDEX idx_external_link_user_id ON eventsync_app.external_link(user_id);

CREATE INDEX idx_users_role ON eventsync_app.users("role");

CREATE INDEX idx_users_role_name ON eventsync_app.users("role", last_name, first_name);

