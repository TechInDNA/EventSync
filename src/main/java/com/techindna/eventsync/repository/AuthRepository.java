package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.entity.Participant;
import com.techindna.eventsync.exception.InternalServerErrorException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AuthRepository {
    private final DataSource dataSource;

    public AuthRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Optional<Administrator> findAdminByEmail(String email) {
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
        try (
                Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {
                ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Administrator admin = new Administrator();
                    admin.setId(UUID.fromString(rs.getString("id")));
                    admin.setFirstName(rs.getString("first_name"));
                    admin.setLastName(rs.getString("last_name"));
                    admin.setPassword(rs.getString("password"));
                    admin.setEmail(rs.getString("email"));
                    return Optional.of(admin);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Participant saveParticipant(String firstName, String lastName, String email) {
        final String insertQuery = """
            INSERT INTO eventsync_app.users (first_name, last_name, email, role)
            VALUES (?, ?, ?, 'participant'::eventsync_app.role)
            ON CONFLICT (email) DO NOTHING
            RETURNING id, first_name, last_name, email
            """;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(insertQuery)
        ) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            try (ResultSet rs = ps.executeQuery()) {
                Participant p = new Participant();
                if (rs.next()) {
                    p.setId(UUID.fromString(rs.getString("id")));
                    p.setFirstName(rs.getString("first_name"));
                    p.setLastName(rs.getString("last_name"));
                    p.setEmail(rs.getString("email"));
                }
                return p;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public Optional<Participant> findParticipant(String email, String firstName, String lastName) {
        final String selectQuery = """
            SELECT id, first_name, last_name, email
            FROM eventsync_app.users
            WHERE email = ? AND first_name = ? AND last_name = ?
            """;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(selectQuery)
        ) {
            ps.setString(1, email);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Participant p = new Participant();
                    p.setId(UUID.fromString(rs.getString("id")));
                    p.setFirstName(rs.getString("first_name"));
                    p.setLastName(rs.getString("last_name"));
                    p.setEmail(rs.getString("email"));
                    return Optional.of(p);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }
}
