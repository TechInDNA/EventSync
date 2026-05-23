package com.techindna.eventsync.dto.sessions;

import java.util.UUID;

public class LinkSpeakerResponseDto {
    private UUID id;
    private UUID sessionId;
    private UUID speakerId;
    private String startTime;
    private String endTime;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public UUID getSpeakerId() { return speakerId; }
    public void setSpeakerId(UUID speakerId) { this.speakerId = speakerId; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
