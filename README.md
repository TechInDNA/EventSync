# EventSync Configuration Guide

This document explains how to configure the `application.properties` file for the EventSync application.

## Important Notice

The `application.properties` file is included in `.gitignore` and should not be committed to version control. This prevents sensitive information such as database credentials and JWT secrets from being exposed in the repository.

Each developer should create their own local `application.properties` file based on the template below.

## Configuration Template

Create a file at `src/main/resources/application.properties` with the following configuration:

```properties
# Application Name
spring.application.name=EventSync

# JWT Secret Key
# Generate a secure UUID for production use
security.jwt.token.secret-key=your-secret-key-here

# Database Configuration
# Update these values according to your PostgreSQL setup
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Logging Configuration (Optional)
# Uncomment and adjust as needed for debugging
# logging.level.org.springframework.security=DEBUG
# logging.level.org.springframework.jdbc=DEBUG
```

## Configuration Details

### Application Name
- **Property**: `spring.application.name`
- **Description**: The name of the Spring Boot application
- **Default**: `EventSync`

### JWT Token Configuration
- **Property**: `security.jwt.token.secret-key`
- **Description**: Secret key used for signing JWT tokens
- **Recommendation**: Use a strong, randomly generated UUID or secure string
- **Generation**: You can generate a secure key using: `uuidgen` (Linux/Mac) or online UUID generators

### Database Configuration

#### Database URL
- **Property**: `spring.datasource.url`
- **Format**: `jdbc:postgresql://host:port/database_name`
- **Default**: `jdbc:postgresql://localhost:5432/eventsync_db`
- **Note**: Ensure the database exists before running the application

#### Database Credentials
- **Property**: `spring.datasource.username`
- **Property**: `spring.datasource.password`
- **Description**: PostgreSQL username and password with access to the database
- **Default username**: `eventsync_manager`

#### Database Driver
- **Property**: `spring.datasource.driver-class-name`
- **Value**: `org.postgresql.Driver`
- **Note**: This is automatically configured by Spring Boot when using PostgreSQL starter

### Logging Configuration

The following logging levels can be configured for debugging:

- **`logging.level.org.springframework.security`**: Controls Spring Security logging (DEBUG, INFO, WARN, ERROR)
- **`logging.level.org.springframework.jdbc`**: Controls JDBC/SQL logging (DEBUG, INFO, WARN, ERROR)

Uncomment these lines only when debugging is needed, as debug logging may expose sensitive information.

## Setup Instructions

1. Create the file `src/main/resources/application.properties`
2. Copy the template above
3. Replace the placeholder values with your actual configuration:
   - Generate a secure JWT secret key
   - Update database URL with your database name
   - Set your PostgreSQL username and password
4. Ensure PostgreSQL is running and the database exists
5. Run the application

## Security Reminders

- Never commit `application.properties` to version control
- Use strong, unique secrets for JWT tokens in production
- Restrict database user permissions to minimum required privileges
- Consider using environment variables or Spring Cloud Config for production deployments
