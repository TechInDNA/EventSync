package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.dto.SessionResponseDto;
import com.techindna.eventsync.dto.SpeakerRefDto;

import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.ConflictException;
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
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;

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
                    Instant now = Instant.now();
                    session.setLive(now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate()));
                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SessionResponseDto> getAllSessions(int offset, int limit) {
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
                e.title AS event_title,
                e.description AS event_description,
                e.start_date AS event_start_date,
                e.end_date AS event_end_date,
                e.location AS event_location,
                e.created_at AS event_created_at
            FROM eventsync_app.sessions s
            LEFT JOIN eventsync_app.rooms r ON s.room_id = r.id
            LEFT JOIN eventsync_app.events e ON s.event_id = e.id
            ORDER BY s.start_date DESC
            LIMIT ? OFFSET ?
            """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<SessionResponseDto> sessions = new ArrayList<>();
                Instant now = Instant.now();
                while (rs.next()) {
                    SessionResponseDto session = new SessionResponseDto();
                    session.setId(UUID.fromString(rs.getString("id")));
                    session.setTitle(rs.getString("title"));
                    session.setDescription(rs.getString("description"));
                    session.setStartDate(rs.getTimestamp("start_date").toInstant());
                    session.setEndDate(rs.getTimestamp("end_date").toInstant());
                    session.setCapacity(rs.getInt("capacity"));

                    String roomId = rs.getString("room_id");
                    if (roomId != null) {
                        Room room = new Room();
                        room.setId(UUID.fromString(roomId));
                        room.setName(rs.getString("room_name"));
                        session.setRoom(room);
                    }

                    String eventId = rs.getString("event_id");
                    if (eventId != null) {
                        Event event = new Event();
                        event.setId(UUID.fromString(eventId));
                        event.setTitle(rs.getString("event_title"));
                        event.setDescription(rs.getString("event_description"));
                        event.setStartDate(rs.getTimestamp("event_start_date").toInstant());
                        event.setEndDate(rs.getTimestamp("event_end_date").toInstant());
                        event.setLocation(rs.getString("event_location"));
                        event.setCreatedAt(rs.getTimestamp("event_created_at").toInstant());
                        session.setEvent(event);
                    }

                    session.setLive(now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate()));

                    session.setSpeakers(getSpeakersBySessionId(session.getId()));

                    sessions.add(session);
                }
                return sessions;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private List<SpeakerRefDto> getSpeakersBySessionId(UUID sessionId) {
        final String query =
            """
            SELECT
                u.id,
                u.first_name,
                u.last_name,
                u.profile_picture,
                u.bio
            FROM eventsync_app.users u
            INNER JOIN eventsync_app.intervene i ON i.speaker_id = u.id
            WHERE i.session_id = ?
            ORDER BY u.last_name ASC
            """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, sessionId);
            List<SpeakerRefDto> speakers = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpeakerRefDto speaker = new SpeakerRefDto();
                    speaker.setId(UUID.fromString(rs.getString("id")));
                    speaker.setFirstName(rs.getString("first_name"));
                    speaker.setLastName(rs.getString("last_name"));
                    speaker.setProfilePicture(rs.getString("profile_picture"));
                    speaker.setBio(rs.getString("bio"));
                    speakers.add(speaker);
                }
            }
            return speakers;
        } catch (SQLException e) {
            return new ArrayList<>();
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
