package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.entity.Participant;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.mapper.AuthMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class AuthRepository {
    private final DataSource dataSource;
    private static final int MAX_ATTEMPT_LIMIT = 5;

    public AuthRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Optional<Administrator> findAdminDataByEmail(String email) {
        String sql = """
            select
            id,
            first_name,
            last_name,
            email,
            password,
            role
            from
            eventsync_app.users
            where
            email = ?
            and role = 'admin'
            """;
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(AuthMapper.mapResultSetToAdministrator(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public Participant saveParticipant(String firstName, String lastName, String email) {
        final String insertQuery = """
            INSERT INTO eventsync_app.users (first_name, last_name, email, role)
            VALUES (?, ?, ?, 'participant'::eventsync_app.role)
            ON CONFLICT (email) DO NOTHING
            RETURNING id, first_name, last_name, email
            """;
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            AuthMapper.mapParticipantQuery(ps, firstName, lastName, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? AuthMapper.mapResultSetToParticipant(rs)
                        : new Participant();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public Optional<Participant> findParticipant(String email, String firstName, String lastName) {
        final String selectQuery = """
            SELECT id, first_name, last_name, email
            FROM eventsync_app.users
            WHERE email = ? AND first_name = ? AND last_name = ?
            """;
        Connection conn =  DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            AuthMapper.mapParticipantQuery(ps, email, firstName, lastName);

            try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(AuthMapper.mapResultSetToParticipant(rs))
                            : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public Optional<Integer> findBlacklistedIp(String ipAddress) {
        final String query = """
            SELECT failed_attempt
            FROM eventsync_app.blacklisted_ip
            WHERE ip_address = ?
            """;
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ipAddress);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getInt("failed_attempt"))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public int incrementFailedAttempt(String ipAddress) {
        final String query = """
            INSERT INTO eventsync_app.blacklisted_ip (ip_address, failed_attempt)
            VALUES (?, 1)
            ON CONFLICT (ip_address)
            DO UPDATE SET failed_attempt = blacklisted_ip.failed_attempt + 1
            WHERE blacklisted_ip.failed_attempt < ?
            RETURNING failed_attempt
            """;
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ipAddress);
            ps.setInt(2, MAX_ATTEMPT_LIMIT);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("failed_attempt")
                        : MAX_ATTEMPT_LIMIT;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public void deleteBlacklistedIp(String ipAddress) {
        final String query = """
            DELETE FROM eventsync_app.blacklisted_ip
            WHERE ip_address = ?
            """;
        Connection conn =  DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ipAddress);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
