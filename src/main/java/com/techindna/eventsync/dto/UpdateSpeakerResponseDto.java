package com.techindna.eventsync.dto;

import java.util.UUID;

public class UpdateSpeakerResponseDto extends SpeakerRequestDto {
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
