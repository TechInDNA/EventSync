# EventSync

EventSync is a Spring Boot-based event management system with JWT authentication, PostgreSQL, and RESTful APIs for managing events, sessions, rooms, speakers, and Q&A.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [HTTPS Configuration](#https-configuration)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Configuration](#configuration)

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- [curlie](https://github.com/rs/curlie) (optional, for manual API tests)

## Quick Start

```bash
# 1. Create .env (copy from example below)
# 2. Set up the database (see Database Setup)
# 3. Run on HTTP :8080
mvn spring-boot:run
```

For HTTPS on port 443, see [HTTPS Configuration](#https-configuration).

## HTTPS Configuration

### 1. Generate a self-signed certificate

```bash
keytool -genkey -alias eventsync -storetype PKCS12 -keyalg RSA -keysize 4096 -keystore eventsync.p12 -validity 3650
```

Remember the password — it becomes `HTTPS_PASS` in `.env`.

### 2. Add to `.env`

```env
HTTPS_PASS=your_keystore_password
KEY_STORE=file:eventsync.p12
CORS_ALLOWED_ORIGINS=http://localhost:4444,http://localhost:3000
```

### 3. Start with `sudo` (required for port 443)

```bash
sudo ./mvnw spring-boot:run
```

Available at `https://localhost`.

## Database Setup

Run these scripts **in order**. The first requires a PostgreSQL superuser:

```bash
psql -f src/sql/init_db.sql          # Creates DB, role, schema (needs superuser)
psql -f src/sql/schema.sql           # Creates tables + enums
psql -f src/sql/auth_data.sql        # Seed admin user (password: "test")
psql -f src/sql/room_data.sql        # Seed rooms
psql -f src/sql/event_data.sql       # Seed events
psql -f src/sql/session_data.sql     # Seed sessions + speaker links
psql -f src/sql/speaker_data.sql     # Seed speakers + external links
psql -f src/sql/question_data.sql    # Seed questions + upvotes
```

All `*_data.sql` files use `ON CONFLICT ... DO NOTHING` — safe to re-run.

## Running the Application

| Mode | Command | URL |
|------|---------|-----|
| Development (HTTP) | `mvn spring-boot:run` | `http://localhost:8080` |
| Production (HTTPS) | `sudo ./mvnw spring-boot:run` | `https://localhost` |

The HTTPS mode requires a keystore and `.env` (see [HTTPS Configuration](#https-configuration)).

## Testing

### Automated

```bash
mvn test    # Single @SpringBootTest — context-load check only
```

No unit tests currently exist.

### Manual API Tests

Shell scripts in `src/test/java/requests/` use [curlie](https://github.com/rs/curlie) to test endpoints. The app must be running.

```bash
# Auth-dependent scripts need login first (creates cookies.txt with JWT session)
./src/test/java/requests/auth/post_auth_login.sh

# Then run any endpoint test:
./src/test/java/requests/events/get_events.sh
./src/test/java/requests/sessions/post_sessions.sh
./src/test/java/requests/speakers/get_speakers.sh
# ... etc
```

For HTTPS, scripts use the `-k` flag to accept self-signed certificates.

## Project Structure

| Package | Contents |
|---------|----------|
| `config` | `JwtAuthenticationFilter`, `SecurityConfig`, `TokenProvider` |
| `controller` | REST controllers: Auth, Event, Session, Room, Question, Speakers |
| `service` | Business logic for all entities |
| `repository` | JDBC repository interfaces |
| `dto` | Request/response DTOs |
| `mapper` | Row mappers (`EventMapper`, `SessionMapper`, `RoomMapper`) |
| `entity` | Domain POJOs + `enums/Role` |
| `validator` | `DataValidator` — validates pagination, UUIDs, session/speaker data |
| `exception` | Custom exceptions: 400, 401, 404, 409, 429, 500 |

### Key Directories

- `src/main/docs/` — OpenAPI spec (`api.yaml`) and conceptual DB schema (`mcd.canvas`)
- `src/sql/` — Database initialization and seed scripts
- `src/test/java/requests/` — Manual API test scripts (curlie)

## Configuration

### Environment Variables

Create a `.env` file in the project root (gitignored):

| Variable | Description | Example |
|----------|-------------|---------|
| `JWT_TOKEN` | Secret for signing JWT tokens | `a1b2c3d4-5678-...` |
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/eventsync_db` |
| `DB_USER` | Database username | `eventsync_manager` |
| `DB_PASSWORD` | Database password | `secure_password` |
| `HTTPS_PASS` | Keystore password | `your_keystore_password` |
| `KEY_STORE` | Keystore path | `file:eventsync.p12` |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:4444` |

Loaded via `spring.config.import=optional:file:.env[.properties]`.

### Application Properties

`src/main/resources/application.properties` is tracked by git and contains the full SSL + port 443 configuration. To override locally (e.g. for HTTP dev on :8080), modify it — the `.gitignore` entry does not take effect because the file was committed before the ignore rule.

### Logging

Debug logging is enabled by default for Spring Security and JDBC. Change levels in `application.properties`:

```properties
logging.level.org.springframework.security=INFO
logging.level.org.springframework.jdbc=INFO
```

## Dependencies

- Spring Boot 4.0.6 (Java 17)
- Spring Security, WebMVC, Validation, JDBC
- PostgreSQL Driver
- Auth0 `java-jwt` 4.5.2
- Bouncy Castle `bcpkix-jdk18on` (Argon2 password hashing)
