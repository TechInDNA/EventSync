package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.QuestionResponseDto;
import com.techindna.eventsync.dto.SpeakerInterventionDto;
import com.techindna.eventsync.dto.events.EventSessionResponseDto;
import com.techindna.eventsync.dto.sessions.GetSessionRequestDto;
import com.techindna.eventsync.dto.sessions.SessionDetailResponseDto;
import com.techindna.eventsync.dto.sessions.SessionResponseDto;
import com.techindna.eventsync.dto.speaker.SessionRequestDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Session;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SessionMapper {

  public static void mapCommonSessionFields(ResultSet rs, Session session) throws SQLException {
    session.setId(UUID.fromString(rs.getString("id")));
    session.setTitle(rs.getString("title"));
    session.setDescription(rs.getString("description"));
    session.setStartDate(rs.getTimestamp("start_date").toInstant());
    session.setEndDate(rs.getTimestamp("end_date").toInstant());
    session.setCapacity(rs.getInt("capacity"));
  }

  public static EventSessionResponseDto mapResultSetToEventSessionDto(ResultSet rs)
      throws SQLException {
    EventSessionResponseDto session = new EventSessionResponseDto();
    session.setId(UUID.fromString(rs.getString("id")));
    session.setTitle(rs.getString("title"));
    session.setDescription(rs.getString("description"));
    session.setStartDate(rs.getTimestamp("start_date").toInstant());
    session.setEndDate(rs.getTimestamp("end_date").toInstant());
    session.setCapacity(rs.getInt("capacity"));

    if (rs.getObject("room_id") != null) {
      session.setRooms(List.of(RoomMapper.mapResultSetToRoom(rs)));
    }
    session.setLive(
        Instant.now().isAfter(session.getStartDate())
            && Instant.now().isBefore(session.getEndDate()));
    return session;
  }

  public static SessionResponseDto mapResultSetToSessionResponseDto(ResultSet rs)
      throws SQLException {
    SessionResponseDto session = new SessionResponseDto();
    session.setId(UUID.fromString(rs.getString("session_id")));
    session.setTitle(rs.getString("session_title"));
    session.setDescription(rs.getString("session_description"));
    session.setStartDate(rs.getTimestamp("session_start_date").toInstant());
    session.setEndDate(rs.getTimestamp("session_end_date").toInstant());
    session.setCapacity(rs.getInt("capacity"));
    return session;
  }

  public static void mapPreparedStatement(
      PreparedStatement ps, SessionRequestDto dto, Room room, Event event) throws SQLException {
    ps.setString(1, dto.getTitle());
    ps.setString(2, dto.getDescription());
    ps.setTimestamp(3, Timestamp.from(Instant.parse(dto.getStartDate())));
    ps.setTimestamp(4, Timestamp.from(Instant.parse(dto.getEndDate())));
    ps.setObject(5, room.getId());
    ps.setInt(6, Integer.parseInt(dto.getCapacity()));
    ps.setObject(7, event.getId());
  }

  public static SessionResponseDto mapToSessionResponseDto(
      ResultSet rs, Room room, Event event, List<SpeakerInterventionDto> speakers, Instant now)
      throws SQLException {
    SessionResponseDto session = new SessionResponseDto();
    mapCommonSessionFields(rs, session);
    session.setLive(now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate()));
    session.setRoom(room);
    session.setEvent(event);
    session.setSpeakers(speakers == null || speakers.isEmpty() ? null : speakers);
    return session;
  }

  public static SessionDetailResponseDto mapResultSetToSessionDetailResponseDto(
      ResultSet rs,
      List<SpeakerInterventionDto> speakers,
      List<QuestionResponseDto> questions,
      Instant now)
      throws SQLException {
    SessionDetailResponseDto session = new SessionDetailResponseDto();
    session.setId(UUID.fromString(rs.getString("session_id")));
    session.setTitle(rs.getString("session_title"));
    session.setDescription(rs.getString("session_description"));
    session.setStartDate(rs.getTimestamp("session_start_date").toInstant());
    session.setEndDate(rs.getTimestamp("session_end_date").toInstant());
    session.setCapacity(rs.getInt("capacity"));
    session.setRoom(rs.getObject("room_id") != null ? RoomMapper.mapResultSetToRoom(rs) : null);
    session.setEvent(EventMapper.mapResultSetToEventWithAlias(rs));
    session.setSpeakers(speakers.isEmpty() ? null : speakers);
    session.setQuestions(questions.isEmpty() ? null : questions);
    session.setLive(now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate()));
    return session;
  }

  public static GetSessionRequestDto mapToGetSessionRequestDto(
      String roomName, String speakerName, String eventTitle, boolean isLive) {
    GetSessionRequestDto request = new GetSessionRequestDto();
    request.setRoomName(roomName);
    request.setSpeakerName(speakerName);
    request.setEventTitle(eventTitle);
    request.setLive(isLive);
    return request;
  }
}
