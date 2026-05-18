insert into eventsync_app.users (first_name, last_name, email, password, "role")
values (
    'Admin',
    'Admin',
    'admin@eventsync.com',
    '$argon2id$v=19$m=16384,t=2,p=1$MHYuT/dnMM16eVdoA3GNkQ$cwPs80AXs0wAhgEv+dvuUstWX5dvlReNAOP+Pd6fUDQ',
    'admin'
);
-- admin hashed password: test
