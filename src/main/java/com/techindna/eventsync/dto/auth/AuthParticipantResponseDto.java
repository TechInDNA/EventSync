package com.techindna.eventsync.dto.auth;

import com.techindna.eventsync.dto.ParticipantDto;

public class AuthParticipantResponseDto {
    private String token;
    private ParticipantDto participant;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ParticipantDto getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantDto participant) {
        this.participant = participant;
    }
}
