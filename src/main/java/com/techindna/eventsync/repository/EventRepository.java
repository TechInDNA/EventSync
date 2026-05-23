package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.events.EventRequestDto;
import com.techindna.eventsync.dto.events.EventResponseDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.mapper.EventMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EventRepository {
    private final DataSource dataSource;

    public EventRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<EventResponseDto> saveEvent(EventRequestDto request){
        final String query =
                """
                    insert into
                        eventsync_app.events(title, description, start_date, end_date, location)
                    values(?, ?, ?, ?, ?)
                    on conflict (title) do nothing
                    returning id, title, description, start_date, end_date, location, created_at
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try(PreparedStatement ps = connection.prepareStatement(query)){
            EventMapper.mapRequestDtoToStatement(request, ps);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EventResponseDto response = new EventResponseDto();
                    response.setSessions(null);
                    return Optional.of(EventMapper.mapResultSetToResponseDto(rs, response));
                }
                return Optional.empty();
            }
        }
        catch (SQLException e){
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public List<Event> getAllEvents(int offset, int limit, String title, String location) {
        final StringBuilder queryBuilder = getAllEventsQueryBuilder(title, location);

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            int paramIndex = 1;
            if (title != null && !title.isBlank()) {
                ps.setString(paramIndex++, "%" + title + "%");
            }
            if (location != null && !location.isBlank()) {
                ps.setString(paramIndex++, "%" + location + "%");
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                List<Event> events = new ArrayList<>();
                while (rs.next()) {
                    events.add(EventMapper.mapResultSetToEvent(rs));
                }
                return events;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private static @NonNull StringBuilder getAllEventsQueryBuilder(String title, String location) {
        final StringBuilder queryBuilder = new StringBuilder(
            """
            select
                id,
                title,
                description,
                start_date,
                end_date,
                location,
                created_at
            from eventsync_app.events
            where 1=1
            """
        );

        if (title != null && !title.isBlank()) {
            queryBuilder.append(" and title ilike ?");
        }
        if (location != null && !location.isBlank()) {
            queryBuilder.append(" and location ilike ?");
        }

        queryBuilder.append(" order by created_at desc limit ? offset ?");
        return queryBuilder;
    }

    public Optional<Event> findEventByTitle(String title) {
        final String query =
            """
            select
                id,
                title,
                description,
                start_date,
                end_date,
                location,
                created_at
            from eventsync_app.events
            where title = ?
            """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Event event = EventMapper.mapResultSetToEvent(rs);
                    return Optional.of(event);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<EventResponseDto> updateEvent(EventRequestDto request) {
        final String query =
            """
            update eventsync_app.events
            set
                title = ?,
                description = ?,
                start_date = ?,
                end_date = ?,
                location = ?
            where id = ?
            returning id, title, description, start_date, end_date, location, created_at
            """;
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            EventMapper.mapRequestDtoToStatement(request, ps);
            ps.setObject(6, UUID.fromString(request.getId()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EventResponseDto response = new EventResponseDto();
                    return Optional.of(EventMapper.mapResultSetToResponseDto(rs, response));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public int countEvents(String title, String location) {
        StringBuilder queryBuilder = new StringBuilder(
            """
            select count(id) as total
            from eventsync_app.events
            where 1=1
            """
        );

        if (title != null && !title.isBlank()) {
            queryBuilder.append(" and title ilike ?");
        }
        if (location != null && !location.isBlank()) {
            queryBuilder.append(" and location ilike ?");
        }

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            int paramIndex = 1;
            if (title != null && !title.isBlank()) {
                ps.setString(paramIndex++, "%" + title + "%");
            }
            if (location != null && !location.isBlank()) {
                ps.setString(paramIndex, "%" + location + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<Event> findEventById(UUID id) {
        final String query =
            """
            select
                id,
                title,
                description,
                start_date,
                end_date,
                location,
                created_at
            from eventsync_app.events
            where id = ?
            """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(EventMapper.mapResultSetToEvent(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<UUID> deleteEventById(UUID id) {
        final String query = "delete from eventsync_app.events where id = ? returning id";
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UUID.fromString(rs.getString("id")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

}
