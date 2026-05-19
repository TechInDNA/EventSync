package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;
import com.techindna.eventsync.dto.speaker.UpdateSpeakerResponseDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpeakerMapper {

    private SpeakerMapper() {}

    public static void bindUpdateSpeakerParams(PreparedStatement ps, SpeakerRequestDto dto, UUID id) throws SQLException {
        ps.setString(1, dto.getFirstName());
        ps.setString(2, dto.getLastName());
        ps.setString(3, dto.getEmail());
        ps.setString(4, dto.getProfilePicture());
        ps.setString(5, dto.getBio());
        ps.setObject(6, id);
    }

    public static UpdateSpeakerResponseDto toUpdateSpeakerResponse(UUID id, SpeakerRequestDto dto) {
        UpdateSpeakerResponseDto response = new UpdateSpeakerResponseDto();
        response.setId(id);
        response.setFirstName(dto.getFirstName());
        response.setLastName(dto.getLastName());
        response.setEmail(dto.getEmail());
        response.setProfilePicture(dto.getProfilePicture());
        response.setBio(dto.getBio());
        return response;
    }

    public static UpdateSpeakerResponseDto mapUpdateSpeakerResponse(ResultSet rs, SpeakerRequestDto dto) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        return toUpdateSpeakerResponse(id, dto);
    }
}
