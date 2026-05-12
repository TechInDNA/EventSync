-- Room for testing POST /sessions endpoint
insert into eventsync_app.rooms (id, name)
values
    ('d4e1798f-d3cf-4114-9e74-aa6b091f8ff5', 'Session POST Room')
on conflict (id) do nothing;

-- Room used for the session to delete
insert into eventsync_app.rooms (id, name)
values
    ('c1c12204-4427-4add-b755-b681719d1685', 'Session Delete Room')
on conflict (id) do nothing;

-- Event for testing POST /sessions endpoint
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('8e354819-2e18-4955-88ce-0ab61e7e8ca3', 'Session POST Event', 'Event for testing POST session endpoint', '2026-06-15 09:00:00', '2026-06-15 18:00:00', 'POST Test Location')
on conflict (id) do nothing;

-- Event used for the session to delete
insert into eventsync_app.events (id, title, description, start_date, end_date, location)
values
    ('b3b958ac-bdd7-481a-b8f7-636d43794f83', 'Session Delete Event', 'Event for testing DELETE session endpoint', '2026-12-01 09:00:00', '2026-12-01 18:00:00', 'Delete Test Location')
on conflict (id) do nothing;

-- Session to delete via DELETE /sessions/{id}
insert into eventsync_app.sessions (id, title, description, start_date, end_date, room_id, capacity, event_id)
values
    ('86ee1de3-b078-404e-9ee5-a55f2b3ff4a5', 'Session to Delete', 'Session for testing DELETE endpoint', '2026-06-15 10:00:00', '2026-06-15 12:00:00', 'c1c12204-4427-4add-b755-b681719d1685', 50, 'b3b958ac-bdd7-481a-b8f7-636d43794f83')
on conflict (id) do nothing;
