# AGENTS.md

## Commands

```bash
mvn spring-boot:run          # Start app on :8080
mvn clean install            # Full build
mvn test                     # All JUnit tests
mvn test -Dtest=SessionControllerTest  # Single test class
```

## Environment

- Create `.env` in project root with `JWT_TOKEN`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGINS`
- Loaded via `spring.config.import=optional:file:.env[.properties]` in `application.properties`
- Both `.env` and `application.properties` are gitignored

## Database (PostgreSQL)

Run scripts in this exact order:

```bash
for f in init_db.sql schema.sql room_data.sql event_data.sql data.sql session_data.sql speaker_data.sql; do
  psql -U eventsync_manager -d eventsync_db -f src/sql/"$f"
done
```

## Manual API tests (curlie, require running app)

```bash
# Auth
./src/test/java/requests/auth/post_auth_login.sh

# Events (CRUD)
for s in post get put delete; do ./src/test/java/requests/events/${s}_events.sh; done

# Sessions (CRUD)
for s in post get put delete; do ./src/test/java/requests/sessions/${s}_sessions.sh; done

# Rooms (CRUD)
for s in post get put delete; do ./src/test/java/requests/room/${s}_rooms.sh; done

# Speakers (CRUD)
for s in post get put delete; do ./src/test/java/requests/speaker/${s}_speakers.sh; done
```

## Architecture

- **Spring JDBC** (no JPA) — repositories use `RowMapper` + `JdbcTemplate`
- **Argon2** password hashing via Bouncy Castle (`bcpkix-jdk18on`)
- **JWT auth** via Auth0 `java-jwt`; secret key in `security.jwt.token.secret-key`
- Single Maven module, Java 17, Spring Boot 4.0.6
- Package root: `com.techindna.eventsync`

| Package | Contents |
|---------|----------|
| `config` | `JwtAuthenticationFilter`, `SecurityConfig`, `TokenProvider` |
| `controller` | `Auth`, `Event`, `Session`, `Room`, `Speakers` |
| `repository` | JDBC repos (`Auth`, `Event`, `Session`, `Room`, `Speaker`) |
| `entity` | Domain POJOs + `enums/Role` |
| `validator` | `DataValidator` — validates page/size, UUID, session data |
| `exception` | `BadRequestException` (400), `UnauthorizedException` (401), `ConflictException` (409), `NotFoundException` (404) |

## Tests

- One `@WebMvcTest(SessionController.class)` — mocks `SessionService` + `DataValidator`, uses `MockMvc`
- One `@SpringBootTest` context-load sanity check
- All unit tests (no integration tests requiring DB)

## Docs

- API spec: `src/main/docs/api.yaml` (OpenAPI 3.0.3)
- Conceptual schema: `src/main/docs/mcd.canvas` (Obsidian Canvas)
