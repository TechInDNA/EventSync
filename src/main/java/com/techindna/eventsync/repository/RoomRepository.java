package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.mapper.RoomMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
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

    public Optional<Room> saveRoom(String name) {
        final String query =
        """
        insert into
            eventsync_app.rooms(name)
        values(?) on conflict (name)
        do nothing
        returning id as room_id, name as room_name
        """;

        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RoomMapper.mapResultSetToRoom(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public List<Room> getAllRooms(int offset, int limit) {
        final String query =
        """
        SELECT id as room_id, name as room_name
        FROM eventsync_app.rooms
        ORDER BY name ASC
        LIMIT ? OFFSET ?
        """;

        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Room> rooms = new ArrayList<>();
                while (rs.next()) {
                    rooms.add(RoomMapper.mapResultSetToRoom(rs));
                }
                return rooms;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public int countRooms() {
        final String query = "SELECT count(id) as total FROM eventsync_app.rooms";

        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()
        ) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public Optional<Room> findRoomByName(String name) {
        final String query = "SELECT id as room_id, name as room_name FROM eventsync_app.rooms WHERE name = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RoomMapper.mapResultSetToRoom(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<Room> updateRoomById(UUID id, String name) {
        final String query = "UPDATE eventsync_app.rooms SET name = ? WHERE id = ? RETURNING id as room_id, name as room_name";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, name);
            ps.setObject(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RoomMapper.mapResultSetToRoom(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<UUID> deleteRoomById(UUID id) {
        final String query = "DELETE FROM eventsync_app.rooms WHERE id = ? RETURNING id";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(UUID.fromString(rs.getString("id")))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

}