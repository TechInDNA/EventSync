package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.InternalServerErrorException;
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

    private String buildFilterWhereClause(GetSessionRequestDto request) {
        StringBuilder where = new StringBuilder("WHERE 1=1");
        if (request.getEventTitle() != null) {
            where.append(" AND e.title ILIKE ?");
        }
        if (request.getRoomName() != null) {
            where.append(" AND r.name ILIKE ?");
        }
        if (request.getSpeakerName() != null) {
            where.append(" AND EXISTS (SELECT 1 FROM eventsync_app.intervene i JOIN eventsync_app.users u ON i.speaker_id = u.id WHERE i.session_id = s.id AND u.role = 'speaker' AND (u.first_name ILIKE ? OR u.last_name ILIKE ?))");
        }
        if (request.isLive()){
            where.append(" AND ? BETWEEN s.start_date AND s.end_date");
        }
        return where.toString();
    }

    private int setFilterParameters(PreparedStatement ps, GetSessionRequestDto request, int startIndex) throws SQLException {
        int idx = startIndex;
        if (request.getEventTitle() != null) {
            ps.setString(idx++, "%" + request.getEventTitle() + "%");
        }
        if (request.getRoomName() != null) {
            ps.setString(idx++, "%" + request.getRoomName() + "%");
        }
        if (request.getSpeakerName() != null) {
            ps.setString(idx++, "%" + request.getSpeakerName() + "%");
            ps.setString(idx++, "%" + request.getSpeakerName() + "%");
        }
        if (request.isLive()){
            ps.setTimestamp(idx++, Timestamp.from(Instant.now()));
        }
        return idx;
    }

    private String getAllSessionsQuery(GetSessionRequestDto request) {
        return """
            SELECT s.id as session_id, s.title as session_title,
                   s.description as session_description,
                   s.start_date as session_start_date,
                   s.end_date as session_end_date,
                   r.id as room_id, r.name as room_name,
                   s.capacity,
                   e.id as event_id, e.title as event_title,
                   e.description as event_description,
                   e.start_date as event_start_date,
                   e.end_date as event_end_date,
                   e.location, e.created_at
            FROM eventsync_app.sessions s
            JOIN eventsync_app.rooms r ON s.room_id = r.id
            JOIN eventsync_app.events e ON e.id = s.event_id
        """ + buildFilterWhereClause(request) + " ORDER BY s.id LIMIT ? OFFSET ?";
    }

    public int countFilteredSessions(GetSessionRequestDto request) {
        String query = """
                    SELECT COUNT(*) as total
                    FROM eventsync_app.sessions s
                    JOIN eventsync_app.rooms r ON s.room_id = r.id
                    JOIN eventsync_app.events e ON e.id = s.event_id
                """ + buildFilterWhereClause(request);

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            setFilterParameters(ps, request, 1);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public List<SpeakerInterventionDto> getInterventionById(UUID sessionId){
        final String query =
                """
                select
                users.first_name,
                users.last_name,
                users.profile_picture,
                users.bio
                from eventsync_app.intervene
                join eventsync_app.users
                on users.id = intervene.speaker_id
                where intervene.session_id = ?
                """;
        final List<SpeakerInterventionDto> interventions = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ){
            ps.setObject(1,sessionId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    SpeakerInterventionDto intervention = new SpeakerInterventionDto();
                    intervention.setFirstName(rs.getString("first_name"));
                    intervention.setLastName(rs.getString("last_name"));
                    intervention.setProfilePicture(rs.getString("profile_picture"));
                    intervention.setBio(rs.getString("bio"));
                    interventions.add(intervention);
                }
                return interventions;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public List<SessionResponseDto> getAllSessions(GetSessionRequestDto request, PaginationRequestDto pagination){
        final String query = getAllSessionsQuery(request);
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ){
            int paramIndex = setFilterParameters(ps, request, 1);
            ps.setInt(paramIndex++, pagination.getLimit());
            ps.setInt(paramIndex, pagination.getOffset());

            Instant now = Instant.now();
            List<SessionResponseDto> sessions = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    SessionResponseDto session = new SessionResponseDto();
                    session.setId(UUID.fromString(rs.getString("session_id")));
                    session.setTitle(rs.getString("session_title"));
                    session.setDescription(rs.getString("session_description"));
                    session.setStartDate(rs.getTimestamp("session_start_date").toInstant());
                    session.setEndDate(rs.getTimestamp("session_end_date").toInstant());
                    session.setCapacity(rs.getInt("capacity"));

                    Room room = new Room();
                    room.setId(UUID.fromString(rs.getString("room_id")));
                    room.setName(rs.getString("room_name"));
                    session.setRoom(room);

                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("event_id")));
                    event.setTitle(rs.getString("event_title"));
                    event.setDescription(rs.getString("event_description"));
                    event.setStartDate(rs.getTimestamp("event_start_date").toInstant());
                    event.setEndDate(rs.getTimestamp("event_end_date").toInstant());
                    session.setEvent(event);

                    session.setSpeakers(getInterventionById(session.getId()));

                    Instant start = session.getStartDate();
                    Instant end = session.getEndDate();
                    session.setLive(now.isAfter(start) && now.isBefore(end));

                    sessions.add(session);
                }
                return sessions;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
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

            Instant now = Instant.now();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SessionResponseDto session = new SessionResponseDto();
                    session.setId(UUID.fromString(rs.getString("id")));
                    session.setTitle(rs.getString("title"));
                    session.setDescription(rs.getString("description"));
                    session.setStartDate(rs.getTimestamp("start_date").toInstant());
                    session.setEndDate(rs.getTimestamp("end_date").toInstant());
                    session.setCapacity(rs.getInt("capacity"));
                    session.setLive(now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate()));

                    session.setEvent(event);

                    session.setRoom(room);

                    session.setSpeakers(getInterventionById(session.getId()).isEmpty() ? null : getInterventionById(session.getId()));

                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<SessionResponseDto> findSessionById(UUID id) {
        final String query =
                """
                SELECT s.id as session_id, s.title as session_title,
                       s.description as session_description,
                       s.start_date as session_start_date,
                       s.end_date as session_end_date,
                       r.id as room_id, r.name as room_name,
                       s.capacity,
                       e.id as event_id, e.title as event_title,
                       e.description as event_description,
                       e.start_date as event_start_date,
                       e.end_date as event_end_date,
                       e.location, e.created_at
                FROM eventsync_app.sessions s
                JOIN eventsync_app.rooms r ON s.room_id = r.id
                JOIN eventsync_app.events e ON e.id = s.event_id
                WHERE s.id = ?
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Instant now = Instant.now();
                    SessionResponseDto session = new SessionResponseDto();
                    session.setId(UUID.fromString(rs.getString("session_id")));
                    session.setTitle(rs.getString("session_title"));
                    session.setDescription(rs.getString("session_description"));
                    session.setStartDate(rs.getTimestamp("session_start_date").toInstant());
                    session.setEndDate(rs.getTimestamp("session_end_date").toInstant());
                    session.setCapacity(rs.getInt("capacity"));

                    Room room = new Room();
                    room.setId(UUID.fromString(rs.getString("room_id")));
                    room.setName(rs.getString("room_name"));
                    session.setRoom(room);

                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("event_id")));
                    event.setTitle(rs.getString("event_title"));
                    event.setDescription(rs.getString("event_description"));
                    event.setStartDate(rs.getTimestamp("event_start_date").toInstant());
                    event.setEndDate(rs.getTimestamp("event_end_date").toInstant());
                    session.setEvent(event);

                    session.setSpeakers(getInterventionById(session.getId()));

                    Instant start = session.getStartDate();
                    Instant end = session.getEndDate();
                    session.setLive(now.isAfter(start) && now.isBefore(end));

                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<Session> findSessionByTitleExcludingId(String title, UUID excludeId) {
        final String query = "SELECT id, title FROM eventsync_app.sessions WHERE title = ? AND id != ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setString(1, title);
            ps.setObject(2, excludeId);
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
            throw new InternalServerErrorException("Database error: " + e.getMessage());
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
                Instant now = Instant.now();
                if (rs.next()) {
                    SessionResponseDto session = new SessionResponseDto();
                    session.setId(UUID.fromString(rs.getString("id")));
                    session.setTitle(rs.getString("title"));
                    session.setDescription(rs.getString("description"));
                    session.setStartDate(rs.getTimestamp("start_date").toInstant());
                    session.setEndDate(rs.getTimestamp("end_date").toInstant());
                    session.setCapacity(rs.getInt("capacity"));
                    session.setLive(now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate()));

                    session.setRoom(room);

                    session.setEvent(event);

                    session.setSpeakers(getInterventionById(session.getId()).isEmpty() ? null : getInterventionById(session.getId()));

                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
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
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }
}
