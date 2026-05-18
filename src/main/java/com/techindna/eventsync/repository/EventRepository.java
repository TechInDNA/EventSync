package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.events.EventRequestDto;
import com.techindna.eventsync.dto.events.EventResponseDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.mapper.EventMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EventRepository {
    private final DataSource dataSource;
    public EventRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }


    public Optional<Event> findEventByIdById(UUID id){
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

        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                ){
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("id")));
                    event.setTitle(rs.getString("title"));
                    event.setDescription(rs.getString("description"));
                    event.setStartDate(rs.getTimestamp("start_date").toInstant());
                    event.setEndDate(rs.getTimestamp("end_date").toInstant());
                    event.setLocation(rs.getString("location"));
                    event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    return Optional.of(event);
                }
                return Optional.empty();
            }

        } catch (SQLException e){
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }


    public Optional<EventResponseDto> saveEvent(EventRequestDto request, Connection connection){
        final String query =
                """
                    insert into
                        eventsync_app.events(title, description, start_date, end_date, location)
                    values(?, ?, ?, ?, ?)
                    on conflict (title) do nothing
                    returning id, title, description, start_date, end_date, location, created_at
                """;
        try(
                PreparedStatement ps = connection.prepareStatement(query)
        ){
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
        }
    }

    public List<Event> getAllEvents(int offset, int limit) {
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
            order by created_at desc
            limit ? offset ?
            """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Event> events = new ArrayList<>();
                while (rs.next()) {
                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("id")));
                    event.setTitle(rs.getString("title"));
                    event.setDescription(rs.getString("description"));
                    event.setStartDate(rs.getTimestamp("start_date").toInstant());
                    event.setEndDate(rs.getTimestamp("end_date").toInstant());
                    event.setLocation(rs.getString("location"));
                    event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    events.add(event);
                }
                return events;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<Event> findEventByTitle(String title) {
        try (Connection connection = dataSource.getConnection()) {
            return findEventByTitle(connection, title);
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<Event> findEventByTitle(Connection connection, String title) {
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
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("id")));
                    event.setTitle(rs.getString("title"));
                    event.setDescription(rs.getString("description"));
                    event.setStartDate(rs.getTimestamp("start_date").toInstant());
                    event.setEndDate(rs.getTimestamp("end_date").toInstant());
                    event.setLocation(rs.getString("location"));
                    event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    return Optional.of(event);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<EventResponseDto> updateEvent(Connection connection, EventRequestDto request) {
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
        }
    }

    public int countEvents() {
        String query = """
            select count(id) as total
            from eventsync_app.events
            """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public UUID deleteEventById(UUID id) {
        final String query = "delete from eventsync_app.events where id = ? returning id";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("id"));
                }
                throw new NotFoundException(String.format("Event %s not found.", id));
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

}
