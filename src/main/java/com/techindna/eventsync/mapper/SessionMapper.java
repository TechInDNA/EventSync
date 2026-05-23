package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.events.EventSessionResponseDto;
import com.techindna.eventsync.dto.sessions.LinkSpeakerResponseDto;
import com.techindna.eventsync.dto.sessions.SessionRequestDto;
import com.techindna.eventsync.dto.sessions.SpeakerInterventionDto;
import com.techindna.eventsync.entity.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SessionMapper {

    private SessionMapper() {}

    public static void bindCreateParams(PreparedStatement ps, SessionRequestDto dto, UUID roomId, UUID eventId) throws SQLException {
        ps.setString(1, dto.getTitle());
        ps.setString(2, dto.getDescription());
        ps.setTimestamp(3, Timestamp.from(Instant.parse(dto.getStartDate())));
        ps.setTimestamp(4, Timestamp.from(Instant.parse(dto.getEndDate())));
        ps.setObject(5, roomId);
        ps.setInt(6, Integer.parseInt(dto.getCapacity()));
        ps.setObject(7, eventId);
    }

    public static void bindLinkSpeakerParams(PreparedStatement ps, UUID speakerId, UUID sessionId, String startTime, String endTime) throws SQLException {
        ps.setObject(1, speakerId);
        ps.setObject(2, sessionId);
        ps.setString(3, startTime);
        ps.setString(4, endTime);
    }

    private static void mapCommonFields(ResultSet rs, Session session) throws SQLException {
        session.setId(UUID.fromString(rs.getString("id")));
        session.setTitle(rs.getString("title"));
        session.setDescription(rs.getString("description"));
        session.setStartDate(rs.getTimestamp("start_date").toInstant());
        session.setEndDate(rs.getTimestamp("end_date").toInstant());
        session.setCapacity(rs.getInt("capacity"));
    }

    public static void mapCommonSessionFields(ResultSet rs, Session session) throws SQLException {
        mapCommonFields(rs, session);
    }

    public static Session mapSession(ResultSet rs) throws SQLException {
        Session session = new Session();
        mapCommonFields(rs, session);
        return session;
    }

    public static EventSessionResponseDto mapResultSetToEventSessionDto(ResultSet rs) throws SQLException {
        Session temp = new Session();
        mapCommonFields(rs, temp);

        EventSessionResponseDto session = new EventSessionResponseDto();
        session.setId(temp.getId());
        session.setTitle(temp.getTitle());
        session.setDescription(temp.getDescription());
        session.setStartDate(temp.getStartDate());
        session.setEndDate(temp.getEndDate());
        session.setCapacity(temp.getCapacity());

        if (rs.getObject("room_id") != null) {
            session.setRooms(List.of(RoomMapper.mapResultSetToRoom(rs)));
        }
        session.setLive(Instant.now().isAfter(session.getStartDate()) && Instant.now().isBefore(session.getEndDate()));
        return session;
    }

    public static LinkSpeakerResponseDto mapLinkSpeakerResponse(ResultSet rs) throws SQLException {
        LinkSpeakerResponseDto response = new LinkSpeakerResponseDto();
        response.setId(UUID.fromString(rs.getString("id")));
        response.setSessionId(UUID.fromString(rs.getString("session_id")));
        response.setSpeakerId(UUID.fromString(rs.getString("speaker_id")));
        response.setStartTime(rs.getString("start_time"));
        response.setEndTime(rs.getString("end_time"));
        return response;
    }

    public static SpeakerInterventionDto mapSpeakerIntervention(ResultSet rs) throws SQLException {
        SpeakerInterventionDto intervention = new SpeakerInterventionDto();
        intervention.setFirstName(rs.getString("first_name"));
        intervention.setLastName(rs.getString("last_name"));
        intervention.setProfilePicture(rs.getString("profile_picture"));
        intervention.setBio(rs.getString("bio"));
        return intervention;
    }
}
