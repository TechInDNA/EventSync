package com.techindna.eventsync.repository;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;
import com.techindna.eventsync.dto.SpeakerResponseDto;
import com.techindna.eventsync.dto.speaker.SpeakerSessionDto;
import com.techindna.eventsync.dto.speaker.UpdateSpeakerResponseDto;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.exception.ConflictException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.NotFoundException;
import com.techindna.eventsync.mapper.SpeakerMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
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

                if (externalLinks != null && !externalLinks.isEmpty()) {
                    insertExternalLinks(connection, userId, externalLinks);
                }

                connection.commit();

                SpeakerResponseDto speaker = new SpeakerResponseDto();
                speaker.setId(userId);
                speaker.setFirstName(speakerRequestDto.getFirstName());
                speaker.setLastName(speakerRequestDto.getLastName());
                speaker.setEmail(speakerRequestDto.getEmail());
                speaker.setProfilePicture(speakerRequestDto.getProfilePicture());
                speaker.setBio(speakerRequestDto.getBio());
                speaker.setExternalLinks(externalLinks);
                return speaker;

            } catch (SQLException e) {
                connection.rollback();
                if (UNIQUE_VIOLATION_SQLSTATE.equals(e.getSQLState())) {
                    throw new ConflictException(
                        String.format("Speaker with email %s already exists", speakerRequestDto.getEmail())
                    );
                }
                throw new InternalServerErrorException("Database error: " + e.getMessage());
            }
        } catch (SQLException e) {
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
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }


    public UpdateSpeakerResponseDto updateSpeakerById(UUID id, SpeakerRequestDto speakerRequestDto) {
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
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
                ){

            SpeakerMapper.bindUpdateSpeakerParams(ps, speakerRequestDto, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFoundException(String.format("Speaker ID %s does not exist.", id));
                }
                return SpeakerMapper.mapUpdateSpeakerResponse(rs, speakerRequestDto);
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

    public Optional<SpeakerResponseDto> findSpeakerById(UUID id) {
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
                    users.id = ? and users."role" = 'speaker'
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                SpeakerResponseDto speaker = new SpeakerResponseDto();
                speaker.setId(UUID.fromString(rs.getString("id")));
                speaker.setFirstName(rs.getString("first_name"));
                speaker.setLastName(rs.getString("last_name"));
                speaker.setProfilePicture(rs.getString("profile_picture"));
                speaker.setBio(rs.getString("bio"));
                speaker.setExternalLinks(getExternalLinksByUserId(id));
                return Optional.of(speaker);
            }
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }

    public List<SpeakerSessionDto> findSessionsBySpeakerId(UUID speakerId) {
        final String query =
                """
                select
                    s.id as session_id,
                    s.title as session_title,
                    s.description as session_description,
                    s.start_date as session_start_date,
                    s.end_date as session_end_date,
                    s.capacity,
                    r.id as room_id,
                    r.name as room_name,
                    e.id as event_id,
                    e.title as event_title,
                    e.description as event_description,
                    e.start_date as event_start_date,
                    e.end_date as event_end_date,
                    e.location,
                    e.created_at
                from
                    eventsync_app.sessions s
                    join eventsync_app.rooms r on r.id = s.room_id
                    join eventsync_app.events e on e.id = s.event_id
                    join eventsync_app.intervene i on i.session_id = s.id
                where
                    i.speaker_id = ?
                order by
                    s.start_date
                """;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setObject(1, speakerId);
            List<SpeakerSessionDto> sessions = new ArrayList<>();
            Instant now = Instant.now();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpeakerSessionDto session = new SpeakerSessionDto();
                    session.setId(UUID.fromString(rs.getString("session_id")));
                    session.setTitle(rs.getString("session_title"));
                    session.setDescription(rs.getString("session_description"));
                    session.setStartDate(rs.getTimestamp("session_start_date").toInstant());
                    session.setEndDate(rs.getTimestamp("session_end_date").toInstant());
                    session.setCapacity(rs.getInt("capacity"));

                    Room room = new Room();
                    room.setId(UUID.fromString(rs.getString("room_id")));
                    room.setName(rs.getString("room_name"));
                    session.setRoom(room);

                    Event event = new Event();
                    event.setId(UUID.fromString(rs.getString("event_id")));
                    event.setTitle(rs.getString("event_title"));
                    event.setDescription(rs.getString("event_description"));
                    event.setStartDate(rs.getTimestamp("event_start_date").toInstant());
                    event.setEndDate(rs.getTimestamp("event_end_date").toInstant());
                    event.setLocation(rs.getString("location"));
                    event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    session.setEvent(event);

                    Instant start = session.getStartDate();
                    Instant end = session.getEndDate();
                    session.setLive(now.isAfter(start) && now.isBefore(end));

                    sessions.add(session);
                }
            }
            return sessions;
        } catch (SQLException e) {
            throw new InternalServerErrorException("Database error: " + e.getMessage());
        }
    }
}
