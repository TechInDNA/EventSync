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

    public List<SpeakerResponseDto> getAllSpeakers(int offset, int limit, String search){
        final String query =
                """
                select
                    u.id,
                    u.first_name,
                    u.last_name,
                    u.email,
                    u.profile_picture,
                    u.bio,
                    el.name as link_name,
                    el.url as link_url
                from
                    eventsync_app.users u
                left join eventsync_app.external_link el on el.user_id = u.id
                where
                    u."role" = 'speaker'
                    and (? is null or u.first_name ilike ? or u.last_name ilike ?)
                order by u.last_name, u.first_name
                limit ? offset ?
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(query)){
            SpeakerMapper.bindSearchParams(ps, search, 1);
            ps.setInt(4, limit);
            ps.setInt(5, offset);

            try(ResultSet rs = ps.executeQuery()){
                List<SpeakerResponseDto> speakers = new ArrayList<>();
                SpeakerResponseDto speaker = null;
                UUID currentId = null;

                while (rs.next()){
                    UUID speakerId = UUID.fromString(rs.getString("id"));
                    if (currentId == null || !currentId.equals(speakerId)) {
                        speaker = SpeakerMapper.mapSpeakerResponse(rs);
                        speaker.setExternalLinks(new ArrayList<>());
                        speakers.add(speaker);
                        currentId = speakerId;
                    }

                    String linkName = rs.getString("link_name");
                    if (linkName != null) {
                        ExternalLinkDto link = ExternalLinkMapper.mapExternalLink(rs);
                        speaker.getExternalLinks().add(link);
                    }
                }
                return speakers;
            }

        }catch (SQLException e){
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally{
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public int countSpeakers(String search) {
        String query =
            """
            select count(id) as total
            from eventsync_app.users u
            where u."role" = 'speaker'
            and (? is null or u.first_name ilike ? or u.last_name ilike ?)
            """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            SpeakerMapper.bindSearchParams(ps, search, 1);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally{
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public SpeakerResponseDto createSpeaker(SpeakerRequestDto speakerRequestDto, List<ExternalLinkDto> externalLinks){
        final String insertUser =
                """
                insert into eventsync_app.users(first_name, last_name, email, profile_picture, bio, "role")
                values(?, ?, ?, ?, ?, 'speaker')
                returning id
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (
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
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
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

        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = connection.prepareStatement(sql)){
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
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<UUID> deleteSpeakerById(UUID id) {
        final String query = """
                                DELETE FROM
                                           eventsync_app.users
                                WHERE id = ? AND "role" = 'speaker'
                                RETURNING id
                            """;
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
                ps.setObject(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(UUID.fromString(rs.getString("id"))) : Optional.empty();
                }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
