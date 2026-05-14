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
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomRepository {
    private final DataSource dataSource;

    public RoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<Room> findRoomById(UUID id) {
        final String query = "select id, name from eventsync_app.rooms where id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
                ){
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()){
                Room room = new Room();
                if (rs.next()){
                   room.setId(id);
                   room.setName(rs.getString("name"));
                   return Optional.of(room);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Room saveRoom(String name) {
        final String query =
        """
        insert into
            eventsync_app.rooms(name)
        values(?) on conflict (name)
        do nothing
        returning id
        """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
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

    public List<Room> getAllRooms(int offset, int limit) {
        final String query =
        """
        SELECT id, name 
        FROM eventsync_app.rooms 
        ORDER BY name ASC 
        LIMIT ? OFFSET ?
        """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Room> rooms = new ArrayList<>();
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(UUID.fromString(rs.getString("id")));
                    room.setName(rs.getString("name"));
                    rooms.add(room);
                }
                return rooms;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countRooms() {
        final String query = "SELECT count(id) as total FROM eventsync_app.rooms";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Room> findRoomByName(String name) {
        final String query = "SELECT id, name FROM eventsync_app.rooms WHERE name = ?";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setId(UUID.fromString(rs.getString("id")));
                    room.setName(rs.getString("name"));
                    return Optional.of(room);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Room> updateRoomById(UUID id, String name) {
        final String query = "UPDATE eventsync_app.rooms SET name = ? WHERE id = ? RETURNING id, name";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setString(1, name);
            ps.setObject(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                Room room = new Room();
                if (rs.next()) {
                    room.setId(UUID.fromString(rs.getString("id")));
                    room.setName(rs.getString("name"));
                    return Optional.of(room);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID deleteRoomById(UUID id) {
        final String query = "DELETE FROM eventsync_app.rooms WHERE id = ? RETURNING id";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next() ? UUID.fromString(rs.getString("id")) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}