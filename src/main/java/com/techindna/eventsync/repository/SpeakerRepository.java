package com.techindna.eventsync.repository;

import com.techindna.eventsync.entity.ExternalLinks;
import com.techindna.eventsync.entity.Speaker;
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
public class SpeakerRepository {
    private final DataSource dataSource;
    public SpeakerRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    private List<ExternalLinks> getExternalLinksByUserId(UUID userId){
        final String query =
                """
                select external_link.name, external_link.url from eventsync_app.external_link where user_id = ?
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ){
            List<ExternalLinks> results = new ArrayList<>();
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    ExternalLinks externalLinks = new ExternalLinks();
                    externalLinks.setName(rs.getString("name"));
                    externalLinks.setUrl(rs.getString("url"));
                    results.add(externalLinks);
                }
            }
            return results;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Speaker> getAllSpeakers(int offset, int limit){
        final String query =
                """
                select
                    users.id,
                    users.first_name,
                    users.last_name,
                    users.profile_picture,
                    users.bio,
                from
                    eventsync_app.users
                where
                    users."role" = 'speaker'
                order by users.last_name
                limit ? offset ?
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
                ){
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            List<Speaker> speakers = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Speaker speaker = new Speaker();
                    UUID userId = UUID.fromString(rs.getString("id"));
                    speaker.setId(userId);
                    speaker.setFirstName(rs.getString("first_name"));
                    speaker.setLastName(rs.getString("last_name"));
                    speaker.setProfilePicture(rs.getString("profile_picture"));
                    speaker.setBio(rs.getString("bio"));
                    List<ExternalLinks> externalLinks = getExternalLinksByUserId(userId);
                    speaker.setExternalLinks(externalLinks);
                    speakers.add(speaker);
                }
                return speakers;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public int countSpeakers() {
        String query = """
            select count(id) as total
            from eventsync_app.users where users."role" = 'speaker'
            """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
