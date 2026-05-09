package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;
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
}
