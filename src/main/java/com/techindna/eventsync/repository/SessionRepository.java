package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.NotFoundException;
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
public class SessionRepository {
    private final DataSource dataSource;

    public SessionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Session> getAllSessions(int offset, int limit) {
        final String query =
                """
                SELECT
                    s.id,
                    s.title,
                    s.description,
                    s.start_date,
                    s.end_date,
                    s.capacity,
                    r.id AS room_id,
                    r.name AS room_name,
                    e.id AS event_id,
                    e.title AS event_title
                FROM
                    eventsync_app.sessions s
                    JOIN eventsync_app.rooms r ON s.room_id = r.id
                    JOIN eventsync_app.events e ON s.event_id = e.id
                ORDER BY
                    s.start_date DESC
                LIMIT ? OFFSET ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Session> sessions = new ArrayList<>();
                while (rs.next()) {
                    Session session = new Session();
                    session.setId(UUID.fromString(rs.getString("id")));
                    session.setTitle(rs.getString("title"));
                    session.setDescription(rs.getString("description"));

                    Timestamp startTs = rs.getTimestamp("start_date");
                    Timestamp endTs = rs.getTimestamp("end_date");
                    Instant startDate = startTs.toInstant();
                    Instant endDate = endTs.toInstant();
                    session.setStartDate(startDate);
                    session.setEndDate(endDate);

                    session.setCapacity(rs.getInt("capacity"));

                    Room room = new Room();
                    room.setId(UUID.fromString(rs.getString("room_id")));
                    room.setName(rs.getString("room_name"));
                    session.setRoom(room);

                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("event_id")));
                    event.setTitle(rs.getString("event_title"));
                    session.setEvent(event);

                    sessions.add(session);
                }
                return sessions;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countSessions() {
        final String query = "SELECT count(id) AS total FROM eventsync_app.sessions";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Session saveSession(String title, String description, Instant startDate, Instant endDate, UUID roomId, int capacity, UUID eventId) {
        final String query =
                """
                INSERT INTO eventsync_app.sessions(title, description, start_date, end_date, room_id, capacity, event_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (title) DO NOTHING
                RETURNING id
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setTimestamp(3, Timestamp.from(startDate));
            ps.setTimestamp(4, Timestamp.from(endDate));
            ps.setObject(5, roomId);
            ps.setInt(6, capacity);
            ps.setObject(7, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                Session session = new Session();
                session.setTitle(title);
                session.setDescription(description);
                session.setStartDate(startDate);
                session.setEndDate(endDate);
                session.setCapacity(capacity);

                Room room = new Room();
                room.setId(roomId);
                session.setRoom(room);

                Event event = new Event();
                event.setId(eventId);
                session.setEvent(event);

                if (rs.next()) {
                    session.setId(UUID.fromString(rs.getString("id")));
                }
                return session;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Session> findSessionById(UUID id) {
        final String query =
                """
                SELECT
                    s.id,
                    s.title,
                    s.description,
                    s.start_date,
                    s.end_date,
                    s.capacity,
                    r.id AS room_id,
                    r.name AS room_name,
                    e.id AS event_id,
                    e.title AS event_title
                FROM
                    eventsync_app.sessions s
                    JOIN eventsync_app.rooms r ON s.room_id = r.id
                    JOIN eventsync_app.events e ON s.event_id = e.id
                WHERE s.id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Session session = new Session();
                    session.setId(UUID.fromString(rs.getString("id")));
                    session.setTitle(rs.getString("title"));
                    session.setDescription(rs.getString("description"));

                    Timestamp startTs = rs.getTimestamp("start_date");
                    Timestamp endTs = rs.getTimestamp("end_date");
                    session.setStartDate(startTs.toInstant());
                    session.setEndDate(endTs.toInstant());

                    session.setCapacity(rs.getInt("capacity"));

                    Room room = new Room();
                    room.setId(UUID.fromString(rs.getString("room_id")));
                    room.setName(rs.getString("room_name"));
                    session.setRoom(room);

                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("event_id")));
                    event.setTitle(rs.getString("event_title"));
                    session.setEvent(event);

                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Session> findSessionByTitle(String title) {
        final String query =
                """
                SELECT
                    s.id,
                    s.title,
                    s.description,
                    s.start_date,
                    s.end_date,
                    s.capacity,
                    r.id AS room_id,
                    r.name AS room_name,
                    e.id AS event_id,
                    e.title AS event_title
                FROM
                    eventsync_app.sessions s
                    JOIN eventsync_app.rooms r ON s.room_id = r.id
                    JOIN eventsync_app.events e ON s.event_id = e.id
                WHERE s.title = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Session session = new Session();
                    session.setId(UUID.fromString(rs.getString("id")));
                    session.setTitle(rs.getString("title"));
                    session.setDescription(rs.getString("description"));

                    Timestamp startTs = rs.getTimestamp("start_date");
                    Timestamp endTs = rs.getTimestamp("end_date");
                    session.setStartDate(startTs.toInstant());
                    session.setEndDate(endTs.toInstant());

                    session.setCapacity(rs.getInt("capacity"));

                    Room room = new Room();
                    room.setId(UUID.fromString(rs.getString("room_id")));
                    room.setName(rs.getString("room_name"));
                    session.setRoom(room);

                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("event_id")));
                    event.setTitle(rs.getString("event_title"));
                    session.setEvent(event);

                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Session updateSession(UUID id, String title, String description, Instant startDate, Instant endDate, UUID roomId, int capacity, UUID eventId) {
        final String query =
                """
                UPDATE eventsync_app.sessions
                SET title = ?, description = ?, start_date = ?, end_date = ?, room_id = ?, capacity = ?, event_id = ?
                WHERE id = ?
                RETURNING id
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setTimestamp(3, Timestamp.from(startDate));
            ps.setTimestamp(4, Timestamp.from(endDate));
            ps.setObject(5, roomId);
            ps.setInt(6, capacity);
            ps.setObject(7, eventId);
            ps.setObject(8, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Session session = new Session();
                    session.setId(id);
                    session.setTitle(title);
                    session.setDescription(description);
                    session.setStartDate(startDate);
                    session.setEndDate(endDate);
                    session.setCapacity(capacity);

                    Room room = new Room();
                    room.setId(roomId);
                    session.setRoom(room);

                    Event event = new Event();
                    event.setId(eventId);
                    session.setEvent(event);

                    return session;
                }
                throw new NotFoundException(String.format("Session %s not found.", id));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID deleteSessionById(UUID id) {
        final String query = "DELETE FROM eventsync_app.sessions WHERE id = ? RETURNING id";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("id"));
                }
                throw new NotFoundException(String.format("Session %s not found.", id));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeSpeakerFromSession(UUID sessionId, UUID speakerId) {
        final String delete = "DELETE FROM eventsync_app.intervene WHERE session_id = ? AND speaker_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(delete)) {
            ps.setObject(1, sessionId);
            ps.setObject(2, speakerId);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new NotFoundException(String.format("Intervention not found for speaker %s in session %s.", speakerId, sessionId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
