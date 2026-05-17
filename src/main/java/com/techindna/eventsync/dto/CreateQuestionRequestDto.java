package com.techindna.eventsync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateQuestionRequestDto {
    private String title;
    private String content;
    private ParticipantDto participant;
    @JsonProperty("isAnonymous")
    private boolean isAnonymous;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ParticipantDto getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantDto participant) {
        this.participant = participant;
    }

    @JsonProperty("isAnonymous")
    public boolean isAnonymous() {
        return isAnonymous;
    }

    @JsonProperty("isAnonymous")
    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }
}
