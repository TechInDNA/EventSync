package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.ParticipantDto;
import com.techindna.eventsync.dto.UserResponseDto;
import com.techindna.eventsync.dto.auth.AuthLoginResponseDto;
import com.techindna.eventsync.dto.AuthParticipantResponseDto;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.entity.Participant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthMapper {

    public static Administrator mapResultSetToAdministrator(ResultSet rs) throws SQLException {
        Administrator admin = new Administrator();
        admin.setId(UUID.fromString(rs.getString("id")));
        admin.setFirstName(rs.getString("first_name"));
        admin.setLastName(rs.getString("last_name"));
        admin.setPassword(rs.getString("password"));
        admin.setEmail(rs.getString("email"));
        return admin;
    }

    public static AuthLoginResponseDto toAuthLoginResponse(Administrator admin, String token) {
        AuthLoginResponseDto response = new AuthLoginResponseDto();
        response.setUser(toUserResponseDto(admin));
        response.setToken(token);
        return response;
    }

    public static UserResponseDto toUserResponseDto(Administrator admin) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(admin.getId());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setEmail(admin.getEmail());
        dto.setRole(admin.getRole());
        return dto;
    }

    public static AuthParticipantResponseDto toAuthParticipantResponse(Participant participant, String token) {
        AuthParticipantResponseDto response = new AuthParticipantResponseDto();
        response.setToken(token);
        response.setParticipant(toParticipantDto(participant));
        return response;
    }

    public static ParticipantDto toParticipantDto(Participant participant) {
        ParticipantDto dto = new ParticipantDto();
        dto.setId(participant.getId());
        dto.setFirstName(participant.getFirstName());
        dto.setLastName(participant.getLastName());
        dto.setEmail(participant.getEmail());
        return dto;
    }
}
