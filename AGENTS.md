# AGENTS.md

## Commands

```bash
sudo ./mvnw spring-boot:run   # HTTPS on :443 (needs keystore + .env)
mvn spring-boot:run           # HTTP on :8080 (overrides SSL via local application.properties)
mvn clean install             # Full build
mvn test                      # All JUnit tests
```

## Environment

- Create `.env` in project root (gitignored) with: `JWT_TOKEN`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `HTTPS_PASS`, `KEY_STORE=file:eventsync.p12`, `CORS_ALLOWED_ORIGINS`
- `.env` loaded via `spring.config.import=optional:file:.env[.properties]`
- `src/main/resources/application.properties` is **tracked by git** (the `.gitignore` entry is ineffective because the file was committed before the ignore rule). It contains the full SSL + port 443 config. To run HTTP on :8080, override `server.port` and remove SSL properties locally.

## Database (PostgreSQL)

Run scripts in this order:

```bash
psql -f src/sql/init_db.sql     # Creates DB, role, schema (needs superuser)
psql -f src/sql/schema.sql      # Creates all tables + enums
psql -f src/sql/auth_data.sql   # Seed admin user (password: "test")
psql -f src/sql/room_data.sql   # Seed rooms
psql -f src/sql/event_data.sql  # Seed events, some rooms/sessions
psql -f src/sql/session_data.sql # Seed sessions, intervene links
psql -f src/sql/speaker_data.sql # Seed speaker users + external links
psql -f src/sql/question_data.sql # Seed questions + upvotes
```

All `*_data.sql` files use `ON CONFLICT ... DO NOTHING` — idempotent, safe to re-run.

## Tests

- Single `@SpringBootTest`: `EventSyncApplicationTests` — context-load sanity check only
- No unit tests exist
- Manual API test scripts at `src/test/java/requests/**/*.sh` (require app running + [curlie](https://github.com/rs/curlie))
- Auth-dependent scripts (events, sessions, speakers) use `cookies.txt` for JWT session — run `post_auth_login.sh` first

## Architecture

- **Spring Boot 4.0.6**, Java 17, single Maven module
- **Spring JDBC** (no JPA) — `RowMapper` + `JdbcTemplate`
- **Spring Boot 4.x** uses `spring-boot-starter-webmvc` (not `spring-boot-starter-web`)
- **Argon2** via Bouncy Castle (`bcpkix-jdk18on`)
- **JWT** via Auth0 `java-jwt`; secret key: `security.jwt.token.secret-key`
- Package root: `com.techindna.eventsync`

| Package | Contents |
|---------|----------|
| `config` | `JwtAuthenticationFilter`, `SecurityConfig`, `TokenProvider` |
| `controller` | `Auth`, `Event`, `Session`, `Room`, `Question`, `Speakers` |
| `service` | Business logic: `AuthService`, `EventService`, `SessionService`, `RoomService`, `SpeakerService`, `QuestionService` |
| `repository` | JDBC repos for all entities |
| `dto` | Request/response DTOs (flat, no nested package) |
| `mapper` | Row mappers: `EventMapper`, `SessionMapper`, `RoomMapper` |
| `entity` | Domain POJOs (`User`, `Event`, `Session`, `Room`, `Question`, `Participant`, `Administrator`, `ExternalLinks`) + `enums/Role` |
| `validator` | `DataValidator` — validates page/size, UUID, session/speaker data |
| `exception` | `BadRequestException` (400), `UnauthorizedException` (401), `NotFoundException` (404), `ConflictException` (409), `TooManyRequestException` (429), `InternalServerErrorException` (500) |

## Docs

- API spec: `src/main/docs/api.yaml` (OpenAPI 3.0.3)
- Conceptual schema: `src/main/docs/mcd.canvas` (Obsidian Canvas)
