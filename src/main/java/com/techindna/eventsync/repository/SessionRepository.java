package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.*;
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
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private static final Instant ACTUAL_DATE = Instant.now();

    public SessionRepository(DataSource dataSource, RoomRepository roomRepository, EventRepository eventRepository) {
        this.dataSource = dataSource;
        this.roomRepository = roomRepository;
        this.eventRepository = eventRepository;
    }

    private String getAllSessionsQueryHandler(GetSessionRequestDto request){
        final StringBuilder query = new StringBuilder(
                """
                select
                sessions.id as session_id,
                sessions.title as session_title,
                sessions.description as session_description,
                sessions.start_date as session_start_date,
                sessions.end_date as session_end_date,
                rooms.id as room_id,
                rooms.name as room_name,
                sessions.capacity,
                events.id as event_id,
                events.title as event_title,
                events.description as event_description,
                events.start_date as event_start_date,
                events.end_date as event_end_date,
                events.location,
                events.created_at
                from eventsync_app.intervene
                join eventsync_app.sessions
                on intervene.session_id = sessions.id
                join eventsync_app.users
                on intervene.speaker_id = users.id
                join eventsync_app.rooms
                on sessions.room_id = rooms.id
                join eventsync_app.events
                on events.id = sessions.event_id
                where users."role" = 'speaker'
                """);
        if (request.getEventTitle() != null) {
            query.append(" and events.title ilike ?");
        }
        if (request.getSpeakerName() != null) {
            query.append(" and (users.first_name ilike ? or users.last_name ilike ?)");
        }
        if (request.getRoomName() != null) {
            query.append(" and rooms.name ilike ?");
        }
        if (request.isLive()){
            query.append(" ? between sessions.start_date and sessions.end_date");
        }
        query.append(" limit ? offset ?");
        return String.valueOf(query);
    }

    public int countSession(){
        final String query = "select count(sessions.id) as total from eventsync_app.sessions";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ){
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public List<SessionResponseDto> getAllSessions(GetSessionRequestDto request, PaginationRequestDto pagination){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(getAllSessionsQueryHandler(request))
        ){
            int paramIndex = 1;
            if (request.getEventTitle() != null) {
                ps.setString(paramIndex++, String.format("%%%s%%", request.getEventTitle()));
            }
            if (request.getSpeakerName() != null) {
                ps.setString(paramIndex++, String.format("%%%s%%", request.getSpeakerName()));
                ps.setString(paramIndex++, String.format("%%%s%%", request.getSpeakerName()));
            }
            if (request.getRoomName() != null) {
                ps.setString(paramIndex++, String.format("%%%s%%", request.getRoomName()));
            }
            if (request.isLive()){
                ps.setTimestamp(paramIndex++, Timestamp.from(ACTUAL_DATE));
            }
            ps.setInt(paramIndex++, pagination.getLimit());
            ps.setInt(paramIndex, pagination.getOffset());

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

                    sessions.add(session);
                }
                return sessions;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                    session.setLive(ACTUAL_DATE.isAfter(session.getStartDate()) && ACTUAL_DATE.isBefore(session.getEndDate()));
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
