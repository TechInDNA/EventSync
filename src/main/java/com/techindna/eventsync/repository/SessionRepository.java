package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.NotFoundException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.swing.text.html.Option;
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

    public Session createSession(SessionRequestDto sessionRequestDto) {
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
            ps.setString(1, sessionRequestDto.getTitle());
            ps.setString(2, sessionRequestDto.getDescription());
            ps.setTimestamp(3, Timestamp.from(sessionRequestDto.getStartDate()));
            ps.setTimestamp(4, Timestamp.from(sessionRequestDto.getEndDate()));
            ps.setObject(5, sessionRequestDto.getRoomId());
            ps.setInt(6, sessionRequestDto.getCapacity());
            ps.setObject(7, sessionRequestDto.getEventId());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ConflictException(String.format("Session with title '%s' already exists", sessionRequestDto.getTitle()));
                }

                Session session = new Session();
                session.setId(UUID.fromString(rs.getString("id")));
                session.setTitle(sessionRequestDto.getTitle());
                session.setDescription(sessionRequestDto.getDescription());
                session.setStartDate(sessionRequestDto.getStartDate());
                session.setEndDate(sessionRequestDto.getEndDate());
                session.setCapacity(sessionRequestDto.getCapacity());

                Room room = new Room();
                room.setId(sessionRequestDto.getRoomId());
                session.setRoom(room);

                Event event = new Event();
                event.setId(sessionRequestDto.getEventId());
                session.setEvent(event);

                return session;
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
