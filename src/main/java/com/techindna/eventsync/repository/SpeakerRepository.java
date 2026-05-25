package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.speaker.SpeakerDetailResponseDto;
import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;
import com.techindna.eventsync.dto.speaker.SpeakerResponseDto;
import com.techindna.eventsync.dto.speaker.UpdateSpeakerResponseDto;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
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
    private static final String FOREIGN_KEY_VIOLATION_SQLSTATE = "23503";

    private final DataSource dataSource;
    private final SessionRepository sessionRepository;

    public SpeakerRepository(DataSource dataSource, SessionRepository sessionRepository){
        this.dataSource = dataSource;
        this.sessionRepository = sessionRepository;
    }

    private static String getAllSpeakerQuery(){
        return """
                with paginated_speakers as (
                    select u.id, u.first_name, u.last_name, u.email, u.profile_picture, u.bio
                    from eventsync_app.users u
                    where u."role" = 'speaker'
                      and (? is null or u.first_name ilike ? or u.last_name ilike ?)
                    order by u.last_name, u.first_name
                    limit ? offset ?
                )
                select
                    ps.id,
                    ps.first_name,
                    ps.last_name,
                    ps.email,
                    ps.profile_picture,
                    ps.bio,
                    el.name as link_name,
                    el.url as link_url
                from paginated_speakers ps
                left join eventsync_app.external_link el on el.user_id = ps.id
                order by ps.last_name, ps.first_name
                """;
    }

    public List<SpeakerResponseDto> getAllSpeakers(int offset, int limit, String search){
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(getAllSpeakerQuery())){
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
                    else {
                        speaker.setExternalLinks(null);
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

        try (PreparedStatement ps = connection.prepareStatement(insertUser)) {

            SpeakerMapper.bindUpdateSpeakerParams(ps, speakerRequestDto);

            UUID userId = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = UUID.fromString(rs.getString("id"));
                }
            }

            if (externalLinks != null && !externalLinks.isEmpty()) {
                insertExternalLinksBatch(connection, userId, externalLinks);
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

    private void insertExternalLinksBatch(Connection connection, UUID userId, List<ExternalLinkDto> externalLinks) {
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
            if (FOREIGN_KEY_VIOLATION_SQLSTATE.equals(e.getSQLState())){
                throw new NotFoundException(String.format("Speaker %s not found.", userId));
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

    private List<ExternalLinkDto> getExternalLinks(UUID id, Connection connection) {
        final String query =
                """
                   select 
                       name as link_name,
                       url as link_url
                   from
                       eventsync_app.external_link
                   where
                       user_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(query)){
            List<ExternalLinkDto> externalLinks = new ArrayList<>();
            ps.setObject(1, id);
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    externalLinks.add(ExternalLinkMapper.mapExternalLink(rs));
                }
            }
            return externalLinks;

        } catch (SQLException e){
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public List<ExternalLinkDto> addExternalLinksBySpeakerId(UUID id, ExternalLinkDto externalLink) {
        final String insertLink =
                """
                    insert into eventsync_app.external_link(name, url, user_id)
                    values (?, ?, ?)
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(insertLink)) {
            ExternalLinkMapper.bindInsertExternalLinkParams(ps, externalLink, id);
            ps.executeUpdate();
            return getExternalLinks(id, connection);
        } catch (SQLException e){
            if (UNIQUE_VIOLATION_SQLSTATE.equals(e.getSQLState())) {
                throw new ConflictException("This URL already exists.");
            }
            if (FOREIGN_KEY_VIOLATION_SQLSTATE.equals(e.getSQLState())){
                throw new NotFoundException(String.format("Speaker %s not found.", id));
            }
            throw new RuntimeException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<List<ExternalLinkDto>> updateExternalLinkBySpeakerId(UUID id, String urlName, ExternalLinkDto externalLink) {
        final String query =
                """
                    update eventsync_app.external_link
                    set name = ?, url = ?
                    where user_id = ? and name = ?
                    returning id
                """;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try {
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ExternalLinkMapper.bindInsertExternalLinkParams(ps, externalLink, id);
                ps.setString(4, urlName);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? Optional.of(getExternalLinks(id, connection))
                            : Optional.empty();
                }
            }
        } catch (SQLException e) {
            if (UNIQUE_VIOLATION_SQLSTATE.equals(e.getSQLState())) {
                throw new ConflictException("This URL already exists.");
            }
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<SpeakerDetailResponseDto> findSpeakerById(UUID id) {
        final String speakerWithLinksQuery = """
            select
                u.id, u.first_name, u.last_name, u.email,
                u.profile_picture, u.bio,
                el.name as link_name,
                el.url as link_url
            from eventsync_app.users u
            left join eventsync_app.external_link el on el.user_id = u.id
            where u.id = ? and u."role" = 'speaker'
            """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = connection.prepareStatement(speakerWithLinksQuery)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                SpeakerDetailResponseDto speaker = null;
                List<ExternalLinkDto> externalLinks = new ArrayList<>();

                while (rs.next()) {
                    if (speaker == null) {
                        speaker = SpeakerMapper.mapSpeakerDetailResponse(rs);
                    }
                    if (rs.getString("link_name") != null) {
                        externalLinks.add(ExternalLinkMapper.mapExternalLink(rs));
                    }
                }

                if (speaker == null) {
                    return Optional.empty();
                }
                speaker.setExternalLinks(externalLinks.isEmpty() ? null : externalLinks);
                speaker.setSessions(sessionRepository.findSessionsBySpeakerId(connection, id));

                return Optional.of(speaker);
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Optional<UUID> deleteExternalLinkBySpeakerId(UUID speakerId, String urlName) {
        final String query = """
                delete from eventsync_app.external_link
                where user_id = ? and name = ?
                returning id
                """;

        Connection conn = DataSourceUtils.getConnection(dataSource);

        try {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setObject(1, speakerId);
                ps.setString(2, urlName);
                try (ResultSet rs = ps.executeQuery()) {
                        return !rs.next() ? Optional.empty()
                                : Optional.of(UUID.fromString(rs.getString("id")));
                }
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public boolean existsSpeakerById(UUID id) {
        final String query = """
            SELECT 1 FROM eventsync_app.users
            WHERE id = ? AND "role" = 'speaker'
            """;
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
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

        try (PreparedStatement ps = conn.prepareStatement(query)) {
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
