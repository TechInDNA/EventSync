package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.events.EventRequestDto;
import com.techindna.eventsync.dto.events.EventResponseDto;
import com.techindna.eventsync.dto.events.PutEventRequestDto;
import com.techindna.eventsync.entity.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class EventMapper {

    public static EventResponseDto mapResultSetToResponseDto(ResultSet rs, EventResponseDto dto) throws SQLException {
        dto.setId(UUID.fromString(rs.getString("id")));
        dto.setTitle(rs.getString("title"));
        dto.setDescription(rs.getString("description"));
        dto.setStartDate(rs.getTimestamp("start_date").toInstant());
        dto.setEndDate(rs.getTimestamp("end_date").toInstant());
        dto.setLocation(rs.getString("location"));
        dto.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return dto;
    }

    public static void mapRequestDtoToStatement(EventRequestDto request, java.sql.PreparedStatement ps) throws SQLException {
        ps.setString(1, request.getTitle());
        ps.setString(2, request.getDescription());
        ps.setTimestamp(3, Timestamp.from(Instant.parse(request.getStartDate())));
        ps.setTimestamp(4, Timestamp.from(Instant.parse(request.getEndDate())));
        ps.setString(5, request.getLocation());
    }

    public static Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(UUID.fromString(rs.getString("id")));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setStartDate(rs.getTimestamp("start_date").toInstant());
        event.setEndDate(rs.getTimestamp("end_date").toInstant());
        event.setLocation(rs.getString("location"));
        event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return event;
    }

    public static EventResponseDto mapEventToResponseDto(Event event) {
        EventResponseDto dto = new EventResponseDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setLocation(event.getLocation());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }

    public static Event mapResultSetToEventWithAlias(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(UUID.fromString(rs.getString("event_id")));
        event.setTitle(rs.getString("event_title"));
        event.setDescription(rs.getString("event_description"));
        event.setStartDate(rs.getTimestamp("event_start_date").toInstant());
        event.setEndDate(rs.getTimestamp("event_end_date").toInstant());
        event.setLocation(rs.getString("location"));
        event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return event;
    }

    public static EventRequestDto mapPutRequestToRequestDto(PutEventRequestDto request, String id) {
        EventRequestDto event = new EventRequestDto();
        event.setId(id);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        return event;
    }
}
