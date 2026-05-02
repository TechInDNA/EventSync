# EventSync

EventSync is a Spring Boot-based event management system with JWT authentication, PostgreSQL database integration, and RESTful APIs for user authentication and event management.

## Table of Contents
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
  - [Environment Variables (.env)](#environment-variables-env)
  - [Logging Configuration](#logging-configuration)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [Curlie Installation](#curlie-installation)
- [Dependencies](#dependencies)

## Project Structure

The project follows standard Maven directory structure. The main source code is located under `src/main/java/com/techindna/eventsync/`, with the following packages:

| Package | Role |
|---------|------|
| `config` | Configuration classes for JWT authentication and Spring Security. |
| `controller` | REST API controllers for authentication and event management endpoints. |
| `dto` | Data Transfer Objects for request/response payloads. |
| `entity` | Domain objects (POJOs) representing database tables (project uses Spring JDBC, no JPA). The `enums` subpackage contains user role definitions. |
| `exception` | Custom exception classes for HTTP error responses (400, 401, 409, 500). |
| `repository` | Database access layer interfaces for authentication and event queries. |
| `service` | Business logic layer for authentication and event management. |
| `validator` | Request validation classes for authentication and event requests. |

### Other Key Directories
- `src/main/docs`: Documentation files:
  - `api.yaml`: OpenAPI 3.0.3 specification in YAML format describing all REST endpoints (Auth, Events, Sessions, Questions, Rooms, Speakers).
  - `mcd.canvas`: Obsidian Canvas file (JSON format) containing the MCD (Modèle Conceptuel de Données) - the conceptual database schema with entities (USERS, EVENTS, SESSIONS, ROOMS, QUESTION) and their relationships (CONTAIN, MANAGE, PARTICIPATE, INTERVENE, ASKS).
- `src/main/resources`: Application properties and static resources.
- `src/sql`: SQL scripts for database initialization (`init_db.sql`, `schema.sql`) and sample data (`data.sql`).
- `src/test/java/requests`: Shell scripts for manual API testing using `curlie`.

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- [Curlie](https://github.com/rs/curlie) (for running API tests)

## Configuration

### Environment Variables (.env)
Create a `.env` file in the project root directory. This file is included in `.gitignore` and should never be committed to version control to prevent exposing sensitive credentials.

The application loads the following variables:

| Variable | Description | Example |
|----------|-------------|---------|
| `JWT_TOKEN` | Secret key for signing JWT tokens. Use a strong, random value (generate with `uuidgen` on Linux/macOS) for production. | `a1b2c3d4-5678-90ab-cdef-1234567890ab` |
| `DB_URL` | JDBC URL for PostgreSQL connection. Format: `jdbc:postgresql://host:port/database_name` | `jdbc:postgresql://localhost:5432/eventsync_db` |
| `DB_USER` | PostgreSQL username with access to the database. | `eventsync_manager` |
| `DB_PASSWORD` | PostgreSQL password for the above user. | `secure_password_here` |

Example `.env` file:
```env
JWT_TOKEN=a1b2c3d4-5678-90ab-cdef-1234567890ab
DB_URL=jdbc:postgresql://localhost:5432/eventsync_db
DB_USER=eventsync_manager
DB_PASSWORD=secure_password_here
```

### Logging Configuration
The application enables debug logging for Spring Security and JDBC by default. To modify logging levels:
1. Create or edit `src/main/resources/application.properties` (also gitignored):
   ```properties
   logging.level.org.springframework.security=DEBUG
   logging.level.org.springframework.jdbc=DEBUG
   ```
2. Change `DEBUG` to `INFO`, `WARN`, or `ERROR` as needed.

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/EventSync.git
   cd EventSync
   ```
2. Create and configure the `.env` file as described above.
3. Set up the PostgreSQL database:
   - Create the database: `createdb eventsync_db`
   - Run the SQL scripts in order:
     ```bash
     psql -U eventsync_manager -d eventsync_db -f src/sql/init_db.sql
     psql -U eventsync_manager -d eventsync_db -f src/sql/schema.sql
     psql -U eventsync_manager -d eventsync_db -f src/sql/data.sql
     ```
4. Build the project:
   ```bash
   mvn clean install
   ```

## Running the Application
Start the Spring Boot application:
```bash
mvn spring-boot:run
```
The application will run on `http://localhost:8080`.

## Running Tests
The project includes manual API test scripts in `src/test/java/requests/` that use `curlie` to test endpoints. Prerequisites:
- The application is running on `http://localhost:8080`
- `curlie` is installed (see [Curlie Installation](#curlie-installation))

### Test Scripts
1. **Login Tests**: `post_auth_login.sh` tests the `POST /auth/login` endpoint with valid/invalid credentials and malformed requests.
   ```bash
   chmod +x src/test/java/requests/post_auth_login.sh
   ./src/test/java/requests/post_auth_login.sh
   ```

2. **Event Tests**: `post_events.sh` tests the `POST /events` endpoint (requires authentication via login first). It uses a `cookies.txt` file to store the JWT cookie, which is automatically cleaned up after execution.
   ```bash
   chmod +x src/test/java/requests/post_events.sh
   ./src/test/java/requests/post_events.sh
   ```

## Curlie Installation
Curlie is a user-friendly wrapper for `curl` (combines `curl` power with `httpie` ease of use). Install it using one of the following methods (from the [official GitHub repo](https://github.com/rs/curlie)):

### Using Homebrew (macOS/Linux)
```bash
brew install curlie
```

### Using Go
```bash
go install github.com/rs/curlie@latest
```

### Using Webi (macOS/Linux/Windows)
```bash
# macOS/Linux
curl -sS https://webinstall.dev/curlie | bash

# Windows
curl.exe -A "MS" https://webinstall.dev/curlie | powershell
```

### Download Binary
Download the latest release binary from [GitHub Releases](https://github.com/rs/curlie/releases/latest) and add it to your system PATH.

## Dependencies
- Spring Boot 4.0.6
- Spring Security
- Spring WebMVC
- Spring Validation
- Spring JDBC
- PostgreSQL Driver
- Auth0 Java JWT 4.5.2
- Bouncy Castle (Argon2 password hashing)
- Maven (build tool)
