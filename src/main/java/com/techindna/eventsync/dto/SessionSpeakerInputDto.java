package com.techindna.eventsync.dto;

import java.time.OffsetDateTime;

public class SessionSpeakerInputDto {

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public SessionSpeakerInputDto setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public SessionSpeakerInputDto setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

}
