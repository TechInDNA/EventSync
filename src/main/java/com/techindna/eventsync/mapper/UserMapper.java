package com.techindna.eventsync.mapper;

import com.techindna.eventsync.dto.ParticipantDto;
import com.techindna.eventsync.dto.UserResponseDto;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.entity.Participant;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserMapper {

  public static Administrator mapResultSetToAdministrator(ResultSet rs) throws SQLException {
    Administrator admin = new Administrator();
    admin.setId(UUID.fromString(rs.getString("id")));
    admin.setFirstName(rs.getString("first_name"));
    admin.setLastName(rs.getString("last_name"));
    admin.setPassword(rs.getString("password"));
    admin.setEmail(rs.getString("email"));
    return admin;
  }

  public static void mapParticipantQuery(
      PreparedStatement ps, String email, String firstName, String lastName) throws SQLException {
    ps.setString(1, email);
    ps.setString(2, firstName);
    ps.setString(3, lastName);
  }

  public static Participant mapResultSetToParticipant(ResultSet rs) throws SQLException {
    Participant p = new Participant();
    p.setId(UUID.fromString(rs.getString("id")));
    p.setFirstName(rs.getString("first_name"));
    p.setLastName(rs.getString("last_name"));
    p.setEmail(rs.getString("email"));
    return p;
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

  public static ParticipantDto mapResultSetToParticipantDto(ResultSet rs) throws SQLException {
    ParticipantDto dto = new ParticipantDto();
    dto.setId(UUID.fromString(rs.getString("user_id")));
    dto.setFirstName(rs.getString("first_name"));
    dto.setLastName(rs.getString("last_name"));
    dto.setEmail(rs.getString("email"));
    return dto;
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
