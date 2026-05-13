package com.techindna.eventsync.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;

@JsonPropertyOrder({"id", "speaker", "session", "startTime", "endTime"})
public class SessionSpeakerResponseDto {

    private UUID id;
    private SpeakerRefDto speaker;
    private SessionRefDetailDto session;
    private java.time.OffsetDateTime startTime;
    private java.time.OffsetDateTime endTime;




    public UUID getId() {
        return id;
    }

    public SessionSpeakerResponseDto setId(UUID id) {
        this.id = id;
        return this;
    }

    public SpeakerRefDto getSpeaker() {
        return speaker;
    }

    public SessionSpeakerResponseDto setSpeaker(SpeakerRefDto speaker) {
        this.speaker = speaker;
        return this;
    }

    public SessionRefDetailDto getSession() {
        return session;
    }

    public SessionSpeakerResponseDto setSession(SessionRefDetailDto session) {
        this.session = session;
        return this;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public SessionSpeakerResponseDto setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public SessionSpeakerResponseDto setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
    }




}
