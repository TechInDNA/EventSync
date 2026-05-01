# EventSync Configuration Guide

This document explains how to configure the `.env` file for the EventSync application.

## Important Notice

The `.env` file is included in `.gitignore` and should not be committed to version control. This prevents sensitive information such as database credentials and JWT secrets from being exposed in the repository.

## Configuration Template

Create a file at the project root named `.env` with the following configuration:

```env
# JWT Secret Key
# Generate a secure UUID for production use
JWT_TOKEN=your-secret-key-here

# Database Configuration
# Update these values according to your PostgreSQL setup
DB_URL=jdbc:postgresql://localhost:5432/db_name
DB_USER=db_user
DB_PASSWORD=db_password
```

## Configuration Details

### JWT Token Configuration
- **Variable**: `JWT_TOKEN`
- **Description**: Secret key used for signing JWT tokens
- **Recommendation**: Use a strong, randomly generated UUID or secure string
- **Generation**: You can generate a secure key using: `uuidgen` (Linux/Mac) or online UUID generators

### Database Configuration

#### Database URL
- **Variable**: `DB_URL`
- **Format**: `jdbc:postgresql://host:port/database_name`
- **Default**: `jdbc:postgresql://localhost:5432/eventsync_db`
- **Note**: Ensure the database exists before running the application

#### Database Credentials
- **Variable**: `DB_USER`
- **Variable**: `DB_PASSWORD`
- **Description**: PostgreSQL username and password with access to the database
- **Default username**: `eventsync_manager`

### Logging Configuration

The `application.properties` file includes debug logging for Spring Security and JDBC by default. To change logging levels, create or modify `src/main/resources/application.properties` (also gitignored) with:

```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.jdbc=DEBUG
```

Change `DEBUG` to `INFO`, `WARN`, or `ERROR` as needed.

## Setup Instructions

1. Create the file `.env` in the project root directory
2. Copy the template above
3. Replace the placeholder values with your actual configuration:
   - Generate a secure JWT secret key
   - Update database URL with your database name
   - Set your PostgreSQL username and password
4. Ensure PostgreSQL is running and the database exists
5. Run the application

## Security Reminders

- Never commit `.env` to version control
- Use strong, unique secrets for JWT tokens in production
- Restrict database user permissions to minimum required privileges
- Consider using environment variables or Spring Cloud Config for production deployments
