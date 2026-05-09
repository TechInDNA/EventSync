# AGENTS.md

## Commands

```bash
mvn spring-boot:run          # Start app on :8080
mvn clean install            # Build
mvn test                     # JUnit tests (currently only EventSyncApplicationTests)
```

Manual API tests (require running app + curlie):

```bash
chmod +x src/test/java/requests/*.sh
./src/test/java/requests/post_auth_login.sh
./src/test/java/requests/post_events.sh
```

## Environment

- Copy `.env.example` or create `.env` from README variables (`JWT_TOKEN`, `DB_URL`, `DB_USER`, `DB_PASSWORD`)
- App loads `.env` via `spring.config.import=optional:file:.env[.properties]` in `src/main/resources/application.properties`
- Both `.env` and `application.properties` are gitignored

## Database

PostgreSQL required. Initialize in order:

```bash
psql -U eventsync_manager -d eventsync_db -f src/sql/init_db.sql
psql -U eventsync_manager -d eventsync_db -f src/sql/schema.sql
psql -U eventsync_manager -d eventsync_db -f src/sql/room_data.sql
psql -U eventsync_manager -d eventsync_db -f src/sql/event_data.sql
psql -U eventsync_manager -d eventsync_db -f src/sql/data.sql
psql -U eventsync_manager -d eventsync_db -f src/sql/SessionData.sql
```

## Architecture Notes

- **Spring JDBC, not JPA** — no `@Entity` classes; repositories use `RowMapper` and `JdbcTemplate`
- **Argon2** password hashing via Bouncy Castle (not bcrypt)
- **JWT** auth via Auth0 library; secret key in `security.jwt.token.secret-key` property
- Single Maven project, package: `com.techindna.eventsync`
- API spec: `src/main/docs/api.yaml` (OpenAPI 3.0.3)
- Conceptual schema: `src/main/docs/mcd.canvas` (Obsidian Canvas JSON)

## Packages

| Package | Role |
|---------|------|
| `config` | JWT filter, Spring Security config, TokenProvider |
| `controller` | REST endpoints (Auth, Events) |
| `dto` | Request/response payloads |
| `entity` | Domain objects + `enums/Role` |
| `repository` | JDBC query interfaces |
| `service` | Business logic |
| `validator` | Request validation |
| `exception` | Custom HTTP errors (400, 401, 409, 500) |
