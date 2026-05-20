package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;
import com.techindna.eventsync.dto.speaker.SpeakerResponseDto;
import com.techindna.eventsync.dto.speaker.UpdateSpeakerResponseDto;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.mapper.ExternalLinkMapper;
import com.techindna.eventsync.mapper.SpeakerMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            throw new InternalServerErrorException("Database error: " + e.getMessage());
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
            throw new InternalServerErrorException("Database error: " + e.getMessage());
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
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public SpeakerResponseDto createSpeaker(SpeakerRequestDto speakerRequestDto, List<ExternalLinkDto> externalLinks){
        final String insertUser =
                """
                insert into eventsync_app.users(first_name, last_name, email, profile_picture, bio, "role")
                values(?, ?, ?, ?, ?, 'speaker')
                returning id
                """;
        try (
                Connection connection = DataSourceUtils.getConnection(dataSource);
                PreparedStatement ps = connection.prepareStatement(insertUser)
        ) {

            SpeakerMapper.bindUpdateSpeakerParams(ps, speakerRequestDto);

            UUID userId = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = UUID.fromString(rs.getString("id"));
                }
            }

            if (externalLinks != null && !externalLinks.isEmpty()) {
                insertExternalLinks(connection, userId, externalLinks);
            }

            return SpeakerMapper.toSpeakerResponse(userId, speakerRequestDto, externalLinks);

        } catch (SQLException e) {
            if (UNIQUE_VIOLATION_SQLSTATE.equals(e.getSQLState())) {
                throw new ConflictException(
                        String.format("Speaker with email %s already exists", speakerRequestDto.getEmail())
                    );
            }
            throw new InternalServerErrorException("Database error: " + e.getMessage());
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
                ExternalLinkMapper.bindInsertExternalLinkParams(ps, link, userId);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e){
            if (UNIQUE_VIOLATION_SQLSTATE.equals(e.getSQLState())) {
                throw new ConflictException("One or more external links URL already exist.");
            }
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }


    public Optional<UpdateSpeakerResponseDto> updateSpeakerById(UUID id, SpeakerRequestDto speakerRequestDto) {
        final String sql =
        """
        UPDATE
            eventsync_app.users
        SET
            first_name = ?, last_name = ?, email = ?, profile_picture = ?, bio = ?
        WHERE
            id = ?
        AND
            "role" = 'speaker'
        RETURNING id
        """;

        try (
                Connection connection = DataSourceUtils.getConnection(dataSource);
                PreparedStatement ps = connection.prepareStatement(sql)
                ){

            SpeakerMapper.bindUpdateSpeakerParams(ps, speakerRequestDto);
            ps.setObject(6, id);

            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next() ? Optional.empty()
                        : Optional.of(SpeakerMapper.mapUpdateSpeakerResponse(rs, speakerRequestDto));
            }
        } catch (SQLException e) {
            if (UNIQUE_VIOLATION_SQLSTATE.equals(e.getSQLState())) {
                throw new ConflictException(
                    String.format("Speaker with email %s already exists.", speakerRequestDto.getEmail())
                );
            }
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }

    }

    public Optional<UUID> deleteSpeakerById(UUID id) {
        final String query = """
                                DELETE FROM
                                           eventsync_app.users
                                WHERE id = ? AND "role" = 'speaker'
                                RETURNING id
                            """;

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
                ps.setObject(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(UUID.fromString(rs.getString("id"))) : Optional.empty();
                }

        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }
}
