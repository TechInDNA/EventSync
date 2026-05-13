package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.dto.SessionResponseDto;
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
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionRepository {
    private final DataSource dataSource;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private static final Instant ACTUAL_DATE = Instant.now();

    public SessionRepository(DataSource dataSource, RoomRepository roomRepository, EventRepository eventRepository) {
        this.dataSource = dataSource;
        this.roomRepository = roomRepository;
        this.eventRepository = eventRepository;
    }

    public Optional<SessionResponseDto> createSession(SessionRequestDto sessionRequestDto) {
        final String query =
                """
                INSERT INTO eventsync_app.sessions(title, description, start_date, end_date, room_id, capacity, event_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (title) DO NOTHING
                RETURNING id, title, description, start_date, end_date, room_id, capacity, event_id
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {

            Room room = roomRepository.findRoomByName(sessionRequestDto.getRoomName())
                    .orElseThrow(() -> new NotFoundException("Room not found."));
            Event event = eventRepository.findEventByTitle(sessionRequestDto.getEventTitle())
                    .orElseThrow(() -> new NotFoundException("Event not found."));

            ps.setString(1, sessionRequestDto.getTitle());
            ps.setString(2, sessionRequestDto.getDescription());
            ps.setTimestamp(3, Timestamp.from(Instant.parse(sessionRequestDto.getStartDate())));
            ps.setTimestamp(4, Timestamp.from(Instant.parse(sessionRequestDto.getEndDate())));
            ps.setObject(5, room.getId());
            ps.setInt(6, Integer.parseInt(sessionRequestDto.getCapacity()));
            ps.setObject(7, event.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SessionResponseDto session = new SessionResponseDto();
                    session.setId(UUID.fromString(rs.getString("id")));
                    session.setTitle(rs.getString("title"));
                    session.setDescription(rs.getString("description"));
                    session.setStartDate(rs.getTimestamp("start_date").toInstant());
                    session.setEndDate(rs.getTimestamp("end_date").toInstant());
                    session.setCapacity(rs.getInt("capacity"));
                    session.setEvent(event);
                    session.setRoom(room);
                    session.setLive(ACTUAL_DATE.isAfter(session.getStartDate()) && ACTUAL_DATE.isBefore(session.getEndDate()));
                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Session> findSessionByTitle(String title) {
        final String query = "SELECT id, title FROM eventsync_app.sessions WHERE title = ?";
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
                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<SessionResponseDto> updateSessionById(UUID id, SessionRequestDto sessionRequestDto) {
        final String query =
            """
            UPDATE eventsync_app.sessions
            SET title = ?, description = ?, start_date = ?, end_date = ?, room_id = ?, capacity = ?, event_id = ?
            WHERE id = ?
            RETURNING id, title, description, start_date, end_date, room_id, capacity, event_id
            """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            Room room = roomRepository.findRoomByName(sessionRequestDto.getRoomName())
                    .orElseThrow(() -> new NotFoundException("Room not found."));
            Event event = eventRepository.findEventByTitle(sessionRequestDto.getEventTitle())
                    .orElseThrow(() -> new NotFoundException("Event not found."));

            ps.setString(1, sessionRequestDto.getTitle());
            ps.setString(2, sessionRequestDto.getDescription());
            ps.setTimestamp(3, Timestamp.from(Instant.parse(sessionRequestDto.getStartDate())));
            ps.setTimestamp(4, Timestamp.from(Instant.parse(sessionRequestDto.getEndDate())));
            ps.setObject(5, room.getId());
            ps.setInt(6, Integer.parseInt(sessionRequestDto.getCapacity()));
            ps.setObject(7, event.getId());
            ps.setObject(8, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SessionResponseDto session = new SessionResponseDto();
                    session.setId(UUID.fromString(rs.getString("id")));
                    session.setTitle(rs.getString("title"));
                    session.setDescription(rs.getString("description"));
                    session.setStartDate(rs.getTimestamp("start_date").toInstant());
                    session.setEndDate(rs.getTimestamp("end_date").toInstant());
                    session.setCapacity(rs.getInt("capacity"));
                    session.setRoom(room);
                    session.setEvent(event);
                    session.setLive(ACTUAL_DATE.isBefore(session.getStartDate()) && ACTUAL_DATE.isAfter(session.getEndDate()));
                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UUID> deleteSessionById(UUID id) {
        final String query = "DELETE FROM eventsync_app.sessions WHERE id = ? RETURNING id";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ?
                        Optional.of(UUID.fromString(rs.getString("id"))) :
                        Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
