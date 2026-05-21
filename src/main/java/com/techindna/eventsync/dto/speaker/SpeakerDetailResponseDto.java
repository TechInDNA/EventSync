package com.techindna.eventsync.dto.speaker;

import java.util.List;

public class SpeakerDetailResponseDto extends SpeakerResponseDto {
    private List<SessionForSpeakerDto> sessions;

    public List<SessionForSpeakerDto> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionForSpeakerDto> sessions) {
        this.sessions = sessions;
    }
}
