package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.exception.UnauthorizedException;
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

    public Administrator getAdminByEmail(String email) {
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
            and role = 'ADMIN'
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
                    return admin;
                }
                throw new UnauthorizedException("Invalid credentials");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
