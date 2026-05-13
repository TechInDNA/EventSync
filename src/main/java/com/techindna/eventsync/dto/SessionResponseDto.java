package com.techindna.eventsync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.entity.Session;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"id", "title", "description", "startDate", "endDate", "room", "capacity", "event", "speakers", "isLive"})
public class SessionResponseDto extends Session {
    private boolean isLive;
    private List<SpeakerRefDto> speakers = new ArrayList<>();

    @JsonProperty("isLive")
    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public List<SpeakerRefDto> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<SpeakerRefDto> speakers) {
        this.speakers = speakers;
    }
}
