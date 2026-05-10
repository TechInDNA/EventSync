package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.SpeakerRequestDto;
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
    private static final String UNIQUE_VIOLATION_SQLSTATE = "23505";
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

    public SpeakerResponseDto createSpeaker(SpeakerRequestDto speakerRequestDto){
        final String insertUser =
                """
                insert into eventsync_app.users(first_name, last_name, email, profile_picture, bio, "role")
                values(?, ?, ?, ?, ?, 'speaker')
                on conflict (email) do nothing
                returning id
                """;
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(insertUser)) {
                ps.setString(1, speakerRequestDto.getFirstName());
                ps.setString(2, speakerRequestDto.getLastName());
                ps.setString(3, speakerRequestDto.getEmail());
                ps.setString(4, speakerRequestDto.getProfilePicture());
                ps.setString(5, speakerRequestDto.getBio());

                UUID userId = null;
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userId = UUID.fromString(rs.getString("id"));
                    }
                }

                if (userId == null) {
                    connection.rollback();
                    return null;
                }

                if (speakerRequestDto.getExternalLinks() != null && !speakerRequestDto.getExternalLinks().isEmpty()) {
                    insertExternalLinks(connection, userId, speakerRequestDto.getExternalLinks());
                }

                connection.commit();

                SpeakerResponseDto speaker = new SpeakerResponseDto();
                speaker.setId(userId);
                speaker.setFirstName(speakerRequestDto.getFirstName());
                speaker.setLastName(speakerRequestDto.getLastName());
                speaker.setEmail(speakerRequestDto.getEmail());
                speaker.setProfilePicture(speakerRequestDto.getProfilePicture());
                speaker.setBio(speakerRequestDto.getBio());
                speaker.setExternalLinks(speakerRequestDto.getExternalLinks());
                return speaker;

            } catch (ConflictException e) {
                connection.rollback();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertExternalLinks(Connection connection, UUID userId, List<ExternalLinkDto> externalLinks) {
        final String insertLink =
                """
                insert into eventsync_app.external_link(name, url, user_id)
                values(?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insertLink)) {
            for (ExternalLinkDto link : externalLinks) {
                ps.setString(1, link.getName());
                ps.setString(2, link.getUrl());
                ps.setObject(3, userId);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e){
            if (UNIQUE_VIOLATION_SQLSTATE.equals(e.getSQLState())) {
                throw new ConflictException("One or more external links URL already exist.");
            }
            throw new RuntimeException("Database error while inserting external links", e);
        }
    }


    public SpeakerResponseDto updateSpeaker(UUID id, SpeakerRequestDto speakerRequestDto) {
        final String sql =
        """
        UPDATE eventsync_app.users
        SET
        first_name = ?,
        last_name = ?,
        email = ?,
        profile_picture = ?,
        bio = ?
        WHERE id = ?
        AND "role" = 'speaker'
        on conflict (email) do nothing
        returning email
        """;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, speakerRequestDto.getFirstName());
                ps.setString(2, speakerRequestDto.getLastName());
                ps.setString(3, speakerRequestDto.getEmail());
                ps.setString(4, speakerRequestDto.getProfilePicture());
                ps.setString(5, speakerRequestDto.getBio());
                ps.setObject(6, id);

                int rows = ps.executeUpdate();

                if (rows > 0) {

                    try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM eventsync_app.external_link WHERE user_id = ?")) {
                        psDel.setObject(1, id);
                        psDel.executeUpdate();
                    }
                    if (speakerRequestDto.getExternalLinks() != null && !speakerRequestDto.getExternalLinks().isEmpty()) {
                        insertExternalLinks(conn, id, speakerRequestDto.getExternalLinks());
                    }
                    conn.commit();

                    SpeakerResponseDto speaker = new SpeakerResponseDto();
                    speaker.setId(id);
                    speaker.setFirstName(speakerRequestDto.getFirstName());
                    speaker.setLastName(speakerRequestDto.getLastName());
                    speaker.setEmail(speakerRequestDto.getEmail());
                    speaker.setProfilePicture(speakerRequestDto.getProfilePicture());
                    speaker.setBio(speakerRequestDto.getBio());
                    speaker.setExternalLinks(getExternalLinksByUserId(id));
                    return speaker;
                }
                conn.rollback();
                return null;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteSpeaker(UUID id) {

        final String deleteLinks = "DELETE FROM eventsync_app.external_link WHERE user_id = ?";

        final String deleteUser = """
                            DELETE
                            FROM
                                eventsync_app.users
                            WHERE id = ?
                              AND
                                "role" = 'speaker'
                            """;

        try (
                Connection conn = dataSource.getConnection()
        ) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement psLinks = conn.prepareStatement(deleteLinks);
                    PreparedStatement psUser = conn.prepareStatement(deleteUser)
            ) {
                psLinks.setObject(1, id);
                psLinks.executeUpdate();

                psUser.setObject(1, id);
                int rowsAffected = psUser.executeUpdate();

                conn.commit();
                return rowsAffected > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
