package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.SessionRefDetailDto;
import com.techindna.eventsync.dto.SessionRequestDto;
import com.techindna.eventsync.dto.SessionResponseDto;
import com.techindna.eventsync.dto.SessionSpeakerInputDto;
import com.techindna.eventsync.dto.SessionSpeakerResponseDto;
import com.techindna.eventsync.dto.SpeakerRefDto;

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
import java.time.OffsetDateTime;
import java.time.OffsetTime;
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

    // ---------------------------------------------------------------
    // FIND BY TITLE
    // ---------------------------------------------------------------
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

    public Optional<SessionResponseDto> updateSession(UUID id, SessionRequestDto dto) {
        Room room = roomRepository.findRoomByName(dto.getRoomName())
                .orElseThrow(() -> new NotFoundException("Room not found."));
        Event event = eventRepository.findEventByTitle(dto.getEventTitle())
                .orElseThrow(() -> new NotFoundException("Event not found."));

        final String query = """
            UPDATE eventsync_app.sessions
            SET title = ?, description = ?, start_date = ?, end_date = ?, room_id = ?, capacity = ?, event_id = ?
            WHERE id = ?
            RETURNING id, title, description, start_date, end_date, room_id, capacity, event_id
            """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getDescription());
            ps.setTimestamp(3, Timestamp.from(Instant.parse(dto.getStartDate())));
            ps.setTimestamp(4, Timestamp.from(Instant.parse(dto.getEndDate())));
            ps.setObject(5, room.getId());
            ps.setInt(6, Integer.parseInt(dto.getCapacity()));
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

                    Room sessionRoom = new Room();
                    sessionRoom.setId(room.getId());
                    sessionRoom.setName(room.getName());
                    session.setRoom(sessionRoom);

                    Event sessionEvent = new Event();
                    sessionEvent.setId(event.getId());
                    sessionEvent.setTitle(event.getTitle());
                    session.setEvent(sessionEvent);

                    Instant now = Instant.now();
                    session.setLive(!now.isBefore(session.getStartDate()) && !now.isAfter(session.getEndDate()));

                    session.setSpeakers(getSpeakersBySessionId(session.getId()));

                    return Optional.of(session);
                }
                return Optional.empty();
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

    public void removeSpeakerFromSession(UUID sessionId, UUID speakerId) {
        final String query =
            """
            DELETE FROM eventsync_app.intervene
            WHERE session_id = ? AND speaker_id = ?
            """;
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, sessionId);
            ps.setObject(2, speakerId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new NotFoundException(
                    String.format("Speaker %s is not linked to session %s.", speakerId, sessionId)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SessionSpeakerResponseDto addSpeakerToSession(UUID sessionId, UUID speakerId, SessionSpeakerInputDto input) {
        final String findSessionQuery =
                """
                SELECT s.id, s.title, s.description, s.start_date, s.end_date, s.capacity,
                       r.id AS room_id, r.name AS room_name
                FROM eventsync_app.sessions s
                LEFT JOIN eventsync_app.rooms r ON s.room_id = r.id
                WHERE s.id = ?
                """;
        final String findSpeakerQuery =
                """
                SELECT id, first_name, last_name, profile_picture, bio
                FROM eventsync_app.users
                WHERE id = ? AND "role" = 'speaker'
                """;
        final String insertQuery =
                """
                INSERT INTO eventsync_app.intervene(speaker_id, session_id, start_time, end_time)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;
        try (Connection conn = dataSource.getConnection()) {

            SessionRefDetailDto sessionDetail;
            try (PreparedStatement ps = conn.prepareStatement(findSessionQuery)) {
                ps.setObject(1, sessionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new NotFoundException(String.format("Session %s not found.", sessionId));
                    }
                    sessionDetail = new SessionRefDetailDto();
                    sessionDetail.setId(UUID.fromString(rs.getString("id")));
                    sessionDetail.setTitle(rs.getString("title"));
                    sessionDetail.setDescription(rs.getString("description"));
                    sessionDetail.setStartDate(rs.getTimestamp("start_date").toInstant());
                    sessionDetail.setEndDate(rs.getTimestamp("end_date").toInstant());
                    sessionDetail.setCapacity(rs.getInt("capacity"));
                    SessionRefDetailDto.RoomRef roomRef = new SessionRefDetailDto.RoomRef();
                    String roomId = rs.getString("room_id");
                    if (roomId != null) {
                        roomRef.setId(UUID.fromString(roomId));
                        roomRef.setName(rs.getString("room_name"));
                    }
                    sessionDetail.setRoom(roomRef);
                }
            }

            SpeakerRefDto speakerRef;
            try (PreparedStatement ps = conn.prepareStatement(findSpeakerQuery)) {
                ps.setObject(1, speakerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new NotFoundException(String.format("Speaker %s not found.", speakerId));
                    }
                    speakerRef = new SpeakerRefDto();
                    speakerRef.setId(UUID.fromString(rs.getString("id")));
                    speakerRef.setFirstName(rs.getString("first_name"));
                    speakerRef.setLastName(rs.getString("last_name"));
                    speakerRef.setProfilePicture(rs.getString("profile_picture"));
                    speakerRef.setBio(rs.getString("bio"));
                }
            }

            OffsetDateTime startTime = input.getStartTime();
            OffsetDateTime endTime = input.getEndTime();

            UUID interveneId;
            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setObject(1, speakerId);
                ps.setObject(2, sessionId);

                ps.setObject(3, startTime);
                ps.setObject(4, endTime);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException("Failed to link speaker to session.");
                    }
                    interveneId = UUID.fromString(rs.getString("id"));
                }
            }

            SessionSpeakerResponseDto response = new SessionSpeakerResponseDto();
            response.setId(interveneId);
            response.setSpeaker(speakerRef);
            response.setSession(sessionDetail);
            response.setStartTime(startTime);
            response.setEndTime(endTime);
            return response;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
