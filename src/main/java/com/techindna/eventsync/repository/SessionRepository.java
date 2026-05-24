package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.dto.events.EventSessionResponseDto;
import com.techindna.eventsync.dto.sessions.GetSessionRequestDto;
import com.techindna.eventsync.dto.sessions.SessionResponseDto;
import com.techindna.eventsync.dto.speaker.SessionForSpeakerDto;
import com.techindna.eventsync.dto.speaker.SessionRequestDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.mapper.EventMapper;
import com.techindna.eventsync.mapper.RoomMapper;
import com.techindna.eventsync.mapper.SessionMapper;
import com.techindna.eventsync.mapper.SpeakerMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
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
    private static final String UNIQUE_VIOLATION_SQLSTATE = "23505";

    public SessionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
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
            LEFT JOIN eventsync_app.rooms r ON s.room_id = r.id
            JOIN eventsync_app.events e ON e.id = s.event_id
        """ + buildFilterWhereClause(request) + " ORDER BY s.id LIMIT ? OFFSET ?";
    }

    public int countFilteredSessions(GetSessionRequestDto request) {
        String query = """
                    SELECT COUNT(*) as total
                    FROM eventsync_app.sessions s
                    LEFT JOIN eventsync_app.rooms r ON s.room_id = r.id
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

    public List<SpeakerInterventionDto> getInterventionById(UUID sessionId, Connection connection){
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
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setObject(1,sessionId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    interventions.add(SpeakerMapper.mapResultSetToSpeakerInterventionDto(rs));
                }
                return interventions;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public List<SessionResponseDto> getAllSessions(GetSessionRequestDto request, PaginationRequestDto pagination){
        final String query = getAllSessionsQuery(request);
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try(PreparedStatement ps = connection.prepareStatement(query)){
            int paramIndex = setFilterParameters(ps, request, 1);
            ps.setInt(paramIndex++, pagination.getLimit());
            ps.setInt(paramIndex, pagination.getOffset());

            Instant now = Instant.now();
            List<SessionResponseDto> sessions = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    SessionResponseDto session = SessionMapper.mapResultSetToSessionResponseDto(rs);

                    session.setRoom(rs.getObject("room_id") != null ? RoomMapper.mapResultSetToRoom(rs) : null);

                    Event event = EventMapper.mapResultSetToEventWithAlias(rs);
                    session.setEvent(event);

                    List<SpeakerInterventionDto> speakers = getInterventionById(session.getId(), connection);
                    session.setSpeakers(speakers.isEmpty() ? null : speakers);

                    session.setLive(now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate()));

                    sessions.add(session);
                }
                return sessions;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }


    public Optional<SessionResponseDto> createSession(SessionRequestDto sessionRequestDto, Room room, Event event) {
        final String query =
                """
                INSERT INTO
                    eventsync_app.sessions(title, description, start_date, end_date, room_id, capacity, event_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (title) DO NOTHING
                RETURNING
                id as session_id,
                title as session_title,
                description as session_description,
                start_date as session_start_date,
                end_date as session_end_date,
                room_id,
                capacity,
                event_id
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            SessionMapper.mapPreparedStatement(ps, sessionRequestDto, room, event);

            Instant now = Instant.now();
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SessionResponseDto session = SessionMapper.mapResultSetToSessionResponseDto(rs);
                    session.setLive(now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate()));

                    session.setEvent(event);

                    session.setRoom(room);

                    session.setSpeakers(null);

                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<Session> findSessionById(UUID id) {
        final String query = "SELECT id, title FROM eventsync_app.sessions WHERE id = ?";
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
                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<SessionResponseDto> updateSessionById(UUID id, SessionRequestDto sessionRequestDto, Room room, Event event) {
        final String query =
            """
            UPDATE eventsync_app.sessions
            SET title = ?, description = ?, start_date = ?, end_date = ?, room_id = ?, capacity = ?, event_id = ?
            WHERE id = ?
            RETURNING id, title, description, start_date, end_date, room_id, capacity, event_id
            """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            SessionMapper.mapPreparedStatement(ps, sessionRequestDto, room, event);
            ps.setObject(8, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final List<SpeakerInterventionDto> speakers = getInterventionById(id, connection);
                    return Optional.of(SessionMapper.mapToSessionResponseDto(rs, room, event, speakers, Instant.now()));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals(UNIQUE_VIOLATION_SQLSTATE)) {
                throw new ConflictException(String.format("Session with title '%s' already exists.", sessionRequestDto.getTitle()));
            }
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<UUID> deleteSessionById(UUID id) {
        final String query = "DELETE FROM eventsync_app.sessions WHERE id = ? RETURNING id";

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(id) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public List<SessionForSpeakerDto> findSessionsBySpeakerId(Connection connection, UUID speakerId) {
        final String query = """
            select
                s.id, s.title, s.description,
                s.start_date, s.end_date,
                r.id as room_id, r.name as room_name,
                s.capacity,
                e.id as event_id, e.title as event_title,
                e.description as event_description,
                e.start_date as event_start_date,
                e.end_date as event_end_date,
                e.location, e.created_at
            from eventsync_app.sessions s
            join eventsync_app.rooms r on s.room_id = r.id
            join eventsync_app.events e on s.event_id = e.id
            join eventsync_app.intervene i on i.session_id = s.id
            where i.speaker_id = ?
            order by s.start_date
            """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, speakerId);
            List<SessionForSpeakerDto> sessions = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SessionForSpeakerDto session = new SessionForSpeakerDto();

                    SessionMapper.mapCommonSessionFields(rs, session);
                    session.setRoom(RoomMapper.mapResultSetToRoom(rs));
                    session.setEvent(EventMapper.mapResultSetToEventWithAlias(rs));
                    session.setLive(Instant.now().isAfter(session.getStartDate()) && Instant.now().isBefore(session.getEndDate()));

                    sessions.add(session);
                }
            }
            return sessions.isEmpty() ? null : sessions;
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public List<EventSessionResponseDto> findSessionsByEventId(UUID eventId) {
        final String query = """
            select s.id, s.title, s.description, s.start_date, s.end_date, s.capacity,
                   r.id as room_id, r.name as room_name
            from eventsync_app.sessions s
            left join eventsync_app.rooms r on r.id = s.room_id
            where s.event_id = ?
            """;

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                List<EventSessionResponseDto> sessions = new ArrayList<>();
                while (rs.next()) {
                    sessions.add(SessionMapper.mapResultSetToEventSessionDto(rs));
                }
            return sessions;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
