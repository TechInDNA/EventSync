package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.ExternalLinkDto;
import com.techindna.eventsync.dto.speaker.SpeakerRequestDto;
import com.techindna.eventsync.dto.speaker.SpeakerResponseDto;
import com.techindna.eventsync.dto.speaker.UpdateSpeakerResponseDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class SpeakerMapper {

    private SpeakerMapper() {}

    public static void bindUpdateSpeakerParams(PreparedStatement ps, SpeakerRequestDto dto) throws SQLException {
        ps.setString(1, dto.getFirstName());
        ps.setString(2, dto.getLastName());
        ps.setString(3, dto.getEmail());
        ps.setString(4, dto.getProfilePicture());
        ps.setString(5, dto.getBio());
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

    public static SpeakerResponseDto toSpeakerResponse(UUID id, SpeakerRequestDto dto, List<ExternalLinkDto> externalLinks) {
        SpeakerResponseDto speaker = new SpeakerResponseDto();
        speaker.setId(id);
        speaker.setFirstName(dto.getFirstName());
        speaker.setLastName(dto.getLastName());
        speaker.setEmail(dto.getEmail());
        speaker.setProfilePicture(dto.getProfilePicture());
        speaker.setBio(dto.getBio());
        speaker.setExternalLinks(externalLinks);
        return speaker;
    }
}
