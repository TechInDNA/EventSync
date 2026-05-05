package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.SpeakerResponseDto;
import com.techindna.eventsync.exception.ConflictException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SpeakerRepository {
    private final DataSource dataSource;
    public SpeakerRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    private List<ExternalLinkDto> getExternalLinksByUserId(UUID userId){
        final String query =
                """
                select external_link.name, external_link.url from eventsync_app.external_link where user_id = ?
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ){
            ps.setObject(1, userId);
            List<ExternalLinkDto> results = new ArrayList<>();
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    ExternalLinkDto externalLink = new ExternalLinkDto();
                    externalLink.setName(rs.getString("name"));
                    externalLink.setUrl(rs.getString("url"));
                    results.add(externalLink);
                }
            }
            return results;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<SpeakerResponseDto> getAllSpeakers(int offset, int limit){
        final String query =
                """
                select
                    users.id,
                    users.first_name,
                    users.last_name,
                    users.profile_picture,
                    users.bio
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
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            List<SpeakerResponseDto> speakers = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    SpeakerResponseDto speaker = new SpeakerResponseDto();
                    UUID userId = UUID.fromString(rs.getString("id"));
                    speaker.setId(userId);
                    speaker.setFirstName(rs.getString("first_name"));
                    speaker.setLastName(rs.getString("last_name"));
                    speaker.setProfilePicture(rs.getString("profile_picture"));
                    speaker.setBio(rs.getString("bio"));
                    List<ExternalLinkDto> externalLinks = getExternalLinksByUserId(userId);
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
        String query =
            """
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

    public SpeakerResponseDto createSpeaker(String firstName, String lastName, String email,
                                            String profilePicture, String bio,
                                            List<ExternalLinkDto> externalLinks){
        final String insertUser =
                """
                insert into eventsync_app.users(first_name, last_name, email, profile_picture, bio, "role")
                values(?, ?, ?, ?, ?, 'speaker')
                on conflict (email) do nothing
                returning id
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(insertUser)
        ){
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, profilePicture);
            ps.setString(5, bio);

            UUID userId = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = UUID.fromString(rs.getString("id"));
                }
            }

            if (userId == null) {
                return null;
            }

            if (externalLinks != null && !externalLinks.isEmpty()) {
                insertExternalLinks(connection, userId, externalLinks);
            }

            SpeakerResponseDto speaker = new SpeakerResponseDto();
            speaker.setId(userId);
            speaker.setFirstName(firstName);
            speaker.setLastName(lastName);
            speaker.setProfilePicture(profilePicture);
            speaker.setBio(bio);
            speaker.setExternalLinks(getExternalLinksByUserId(userId));
            return speaker;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertExternalLinks(Connection connection, UUID userId, List<ExternalLinkDto> externalLinks) throws SQLException {
        final String insertLink =
                """
                insert into eventsync_app.external_link(name, url, user_id)
                values(?, ?, ?)
                on conflict (url) do nothing
                """;
        try (PreparedStatement ps = connection.prepareStatement(insertLink)) {
            for (ExternalLinkDto link : externalLinks) {
                ps.setString(1, link.getName());
                ps.setString(2, link.getUrl());
                ps.setObject(3, userId);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            for (int count : results) {
                if (count == 0) {
                    throw new ConflictException("One or more external links URL already exist.");
                }
            }
        }
    }


    public boolean updateSpeaker(UUID id, String firstName, String lastName, String profilePicture, String bio, List<ExternalLinkDto> externalLinks) {
        String sql =
        """
        UPDATE eventsync_app.users 
        SET 
        first_name = ?, 
        last_name = ?, 
        profile_picture = ?, 
        bio = ?
        WHERE id = ? 
        AND "role" = 'speaker'
        RETURNING id
        """;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, profilePicture);
                ps.setString(4, bio);
                ps.setObject(5, id);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM eventsync_app.external_link WHERE user_id = ?")) {
                        psDel.setObject(1, id);
                        psDel.executeUpdate();
                    }
                    if (externalLinks != null && !externalLinks.isEmpty()) {
                        insertExternalLinks(conn, id, externalLinks);
                    }
                    conn.commit();
                    return true;
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update", e);
        }
    }
}
