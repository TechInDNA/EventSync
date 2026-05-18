package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.events.EventSessionResponseDto;
import com.techindna.eventsync.entity.Room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SessionMapper {

    public static EventSessionResponseDto mapResultSetToEventSessionDto(ResultSet rs) throws SQLException {
        EventSessionResponseDto session = new EventSessionResponseDto();
        session.setId(UUID.fromString(rs.getString("id")));
        session.setTitle(rs.getString("title"));
        session.setDescription(rs.getString("description"));
        session.setStartTime(rs.getTimestamp("start_date").toInstant());
        session.setEndTime(rs.getTimestamp("end_date").toInstant());
        session.setCapacity(rs.getInt("capacity"));

        if (rs.getObject("room_id") != null) {
            session.setRooms(List.of(RoomMapper.mapResultSetToRoom(rs)));
        }
        session.setLive(Instant.now().isAfter(session.getStartTime()) && Instant.now().isBefore(session.getEndTime()));
        return session;
    }
}
