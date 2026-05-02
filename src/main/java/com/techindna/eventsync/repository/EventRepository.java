package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.Event;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Repository
public class EventRepository {
    private final DataSource dataSource;
    public EventRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public Event saveEvent(String title, String description, Instant startDate, Instant endDate, String location){
        String query =
                """
                    insert into eventsync_app.events(title, description, start_date, end_date, location)
                    values(?, ?, ?, ?, ?)
                    on conflict (title) do nothing
                    returning id, created_at
                """;
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ){
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setTimestamp(3, Timestamp.from(startDate));
            ps.setTimestamp(4, Timestamp.from(endDate));
            ps.setString(5, location);
            try (ResultSet rs = ps.executeQuery()) {
                Event event = new Event();
                event.setTitle(title);
                event.setDescription(description);
                event.setStartDate(startDate);
                event.setEndDate(endDate);
                event.setLocation(location);

                while (rs.next()) {
                    event.setId(UUID.fromString(rs.getString("id")));
                    event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                }
                return event;
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

}
