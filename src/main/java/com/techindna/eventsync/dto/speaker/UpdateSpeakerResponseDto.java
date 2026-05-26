package com.techindna.eventsync.dto.speaker;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.UUID;

@JsonPropertyOrder({"id", "firstName", "lastName", "email", "profilePicture", "bio"})
public class UpdateSpeakerResponseDto extends SpeakerRequestDto {
  private UUID id;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
}
