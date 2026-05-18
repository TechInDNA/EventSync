# AGENTS.md

## Commands

```bash
sudo ./mvnw spring-boot:run   # HTTPS on :443 (needs keystore + .env, see README)
mvn spring-boot:run           # HTTP on :8080 (if application.properties overrides SSL)
mvn clean install             # Full build
mvn test                      # All JUnit tests
```

## Environment

- Create `.env` in project root (gitignored) with: `JWT_TOKEN`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `HTTPS_PASS`, `KEY_STORE=file:eventsync.p12`, `CORS_ALLOWED_ORIGINS`
- `application.properties` is also gitignored — the local copy may differ from the repo template. HTTPS SSL config + `server.port=443` live there.
- `.env` loaded via `spring.config.import=optional:file:.env[.properties]`

## Database (PostgreSQL)

Run scripts in this order:


## Tests

- Single `@SpringBootTest`: `EventSyncApplicationTests` — context-load sanity check only
- No unit tests currently exist
- Manual API test scripts at `src/test/java/requests/**/*.sh` (require app running + [curlie](https://github.com/rs/curlie))

## Architecture

- **Spring JDBC** (no JPA) — `RowMapper` + `JdbcTemplate`
- **Argon2** via Bouncy Castle (`bcpkix-jdk18on`)
- **JWT** via Auth0 `java-jwt`; secret key: `security.jwt.token.secret-key`
- Single Maven module, Java 17, Spring Boot 4.0.6
- Package root: `com.techindna.eventsync`

| Package | Contents |
|---------|----------|
| `config` | `JwtAuthenticationFilter`, `SecurityConfig`, `TokenProvider` |
| `controller` | `Auth`, `Event`, `Session`, `Room`, `Speakers` |
| `repository` | JDBC repos (`Auth`, `Event`, `Session`, `Room`, `Speaker`) |
| `entity` | Domain POJOs + `enums/Role` |
| `validator` | `DataValidator` — validates page/size, UUID, session/speaker data |
| `exception` | `BadRequestException` (400), `UnauthorizedException` (401), `NotFoundException` (404), `ConflictException` (409), `TooManyRequestException` (429), `InternalServerErrorException` (500) |

## Docs

- API spec: `src/main/docs/api.yaml` (OpenAPI 3.0.3)
- Conceptual schema: `src/main/docs/mcd.canvas` (Obsidian Canvas)
