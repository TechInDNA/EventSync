package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.SpeakerRefDto;
import com.techindna.eventsync.entity.EventRef;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;

import com.techindna.eventsync.exception.NotFoundException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionRepository {

    private final DataSource dataSource;

    public SessionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Session mapRow(ResultSet rs) throws SQLException {
        Session session = new Session();
        session.setId(UUID.fromString(rs.getString("id")));
        session.setTitle(rs.getString("title"));
        session.setDescription(rs.getString("description"));
        session.setStartDate(rs.getTimestamp("start_date").toInstant());
        session.setEndDate(rs.getTimestamp("end_date").toInstant());
        session.setCapacity(rs.getInt("capacity"));

        Room room = new Room();
        room.setId(UUID.fromString(rs.getString("room_id")));
        room.setName(rs.getString("room_name"));
        session.setRoom(room);

        EventRef event = new EventRef();
        event.setId(UUID.fromString(rs.getString("event_id")));
        event.setTitle(rs.getString("event_title"));
        session.setEvent(event);

        return session;
    }

    private List<SpeakerRefDto> findSpeakersBySessionId(UUID sessionId) {
        final String query =
        """
        select u.id, u.first_name, u.last_name, u.profile_picture, u.bio
        from eventsync_app.intervene i
        join eventsync_app.users u on i.speaker_id = u.id
        where i.session_id = ?
        order by u.last_name
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                List<SpeakerRefDto> speakers = new ArrayList<>();
                while (rs.next()) {
                    SpeakerRefDto s = new SpeakerRefDto();
                    s.setId(UUID.fromString(rs.getString("id")));
                    s.setFirstName(rs.getString("first_name"));
                    s.setLastName(rs.getString("last_name"));
                    s.setProfilePicture(rs.getString("profile_picture"));
                    s.setBio(rs.getString("bio"));
                    speakers.add(s);
                }
                return speakers;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<UUID, List<SpeakerRefDto>> findSpeakersBySessionIds(List<UUID> sessionIds) {
        if (sessionIds.isEmpty()) return Map.of();

        StringBuilder query = new StringBuilder(
        """
        select i.session_id, u.id as speaker_id, u.first_name, u.last_name, u.profile_picture, u.bio
        from eventsync_app.intervene i
        join eventsync_app.users u on i.speaker_id = u.id
        where i.session_id in ("""
        );
        for (int i = 0; i < sessionIds.size(); i++) {
            if (i > 0) query.append(",");
            query.append("?");
        }
        query.append(") order by u.last_name");

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query.toString())
        ) {
            for (int i = 0; i < sessionIds.size(); i++) {
                ps.setObject(i + 1, sessionIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                Map<UUID, List<SpeakerRefDto>> result = new HashMap<>();
                while (rs.next()) {
                    UUID sessionId = UUID.fromString(rs.getString("session_id"));
                    SpeakerRefDto s = new SpeakerRefDto();
                    s.setId(UUID.fromString(rs.getString("speaker_id")));
                    s.setFirstName(rs.getString("first_name"));
                    s.setLastName(rs.getString("last_name"));
                    s.setProfilePicture(rs.getString("profile_picture"));
                    s.setBio(rs.getString("bio"));
                    result.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(s);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            if (p instanceof String s) {
                ps.setString(i + 1, s);
            } else if (p instanceof Integer n) {
                ps.setInt(i + 1, n);
            } else if (p instanceof UUID u) {
                ps.setObject(i + 1, u);
            } else if (p instanceof Timestamp t) {
                ps.setTimestamp(i + 1, t);
            } else if (p instanceof Boolean b) {
                ps.setBoolean(i + 1, b);
            } else if (p instanceof Time t) {
                ps.setTime(i + 1, t);
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }

    public List<Session> getAllSessions(int offset, int limit,
                                         String room, String speaker, Boolean live, String event) {
        StringBuilder q = new StringBuilder(
        """
        select
            s.id, s.title, s.description, s.start_date, s.end_date, s.capacity,
            r.id as room_id, r.name as room_name,
            e.id as event_id, e.title as event_title
        from
            eventsync_app.sessions s
            join eventsync_app.rooms r on s.room_id = r.id
            join eventsync_app.events e on s.event_id = e.id
        where 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (room != null && !room.isBlank()) {
            q.append(" and r.name ilike ?");
            params.add("%" + room + "%");
        }
        if (event != null && !event.isBlank()) {
            q.append(" and e.title ilike ?");
            params.add("%" + event + "%");
        }
        if (speaker != null && !speaker.isBlank()) {
            q.append("""
                and exists (
                    select 1 from eventsync_app.intervene i
                    join eventsync_app.users u on i.speaker_id = u.id
                    where i.session_id = s.id
                      and (u.first_name ilike ? or u.last_name ilike ?)
                )
            """);
            params.add("%" + speaker + "%");
            params.add("%" + speaker + "%");
        }
        if (Boolean.TRUE.equals(live)) {
            q.append(" and s.start_date <= now() and s.end_date > now()");
        } else if (Boolean.FALSE.equals(live)) {
            q.append(" and (s.start_date > now() or s.end_date <= now())");
        }

        q.append(" order by s.start_date desc limit ? offset ?");
        params.add(limit);
        params.add(offset);

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(q.toString())
        ) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<Session> sessions = new ArrayList<>();
                List<UUID> sessionIds = new ArrayList<>();
                while (rs.next()) {
                    Session s = mapRow(rs);
                    sessions.add(s);
                    sessionIds.add(s.getId());
                }
                Map<UUID, List<SpeakerRefDto>> speakersBySession = findSpeakersBySessionIds(sessionIds);
                for (Session s : sessions) {
                    s.setSpeakers(speakersBySession.getOrDefault(s.getId(), List.of()));
                }
                return sessions;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countSessions(String room, String speaker, Boolean live, String event) {
        StringBuilder q = new StringBuilder(
        """
        select count(s.id) as total
        from eventsync_app.sessions s
        join eventsync_app.rooms r on s.room_id = r.id
        join eventsync_app.events e on s.event_id = e.id
        where 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (room != null && !room.isBlank()) {
            q.append(" and r.name ilike ?");
            params.add("%" + room + "%");
        }
        if (event != null && !event.isBlank()) {
            q.append(" and e.title ilike ?");
            params.add("%" + event + "%");
        }
        if (speaker != null && !speaker.isBlank()) {
            q.append("""
                and exists (
                    select 1 from eventsync_app.intervene i
                    join eventsync_app.users u on i.speaker_id = u.id
                    where i.session_id = s.id
                      and (u.first_name ilike ? or u.last_name ilike ?)
                )
            """);
            params.add("%" + speaker + "%");
            params.add("%" + speaker + "%");
        }
        if (Boolean.TRUE.equals(live)) {
            q.append(" and s.start_date <= now() and s.end_date > now()");
        } else if (Boolean.FALSE.equals(live)) {
            q.append(" and (s.start_date > now() or s.end_date <= now())");
        }

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(q.toString())
        ) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public Optional<Session> findSessionById(UUID id) {
        final String query =
        """
        select
            s.id, s.title, s.description, s.start_date, s.end_date, s.capacity,
            r.id as room_id, r.name as room_name,
            e.id as event_id, e.title as event_title
        from
            eventsync_app.sessions s
            join eventsync_app.rooms r on s.room_id = r.id
            join eventsync_app.events e on s.event_id = e.id
        where s.id = ?
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Session session = mapRow(rs);
                    session.setSpeakers(findSpeakersBySessionId(id));
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
        select
            s.id, s.title, s.description, s.start_date, s.end_date, s.capacity,
            r.id as room_id, r.name as room_name,
            e.id as event_id, e.title as event_title
        from
            eventsync_app.sessions s
            join eventsync_app.rooms r on s.room_id = r.id
            join eventsync_app.events e on s.event_id = e.id
        where s.title = ?
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Session session = mapRow(rs);
                    session.setSpeakers(findSpeakersBySessionId(session.getId()));
                    return Optional.of(session);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Session updateSession(UUID id, String title, String description, Instant startDate,
                                 Instant endDate, UUID roomId, int capacity, UUID eventId) {
        final String query =
                """
                update
                    eventsync_app.sessions
                set
                    title = ?, description = ?, start_date = ?, end_date = ?,
                    room_id = ?, capacity = ?, event_id = ?
                where id = ?
                returning id
                """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
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

                    EventRef event = new EventRef();
                    event.setId(eventId);
                    session.setEvent(event);

                    session.setSpeakers(findSpeakersBySessionId(id));

                    return session;
                }
                throw new NotFoundException(String.format("Session %s not found.", id));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}











