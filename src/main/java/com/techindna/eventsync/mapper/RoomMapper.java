package com.techindna.eventsync.mapper;

import com.techindna.eventsync.entity.Room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RoomMapper {

    public static Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(UUID.fromString(rs.getString("room_id")));
        room.setName(rs.getString("room_name"));
        return room;
    }
}
