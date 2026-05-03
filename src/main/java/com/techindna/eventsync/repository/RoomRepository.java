package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.Room;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class RoomRepository {
    private final DataSource dataSource;

    public RoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Room saveRoom(String name) {
        String query = "insert into eventsync_app.rooms(name) values(?) on conflict (name) do nothing returning id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                Room room = new Room();
                room.setName(name);
                if (rs.next()) {
                    room.setId(UUID.fromString(rs.getString("id")));
                }
                return room;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}