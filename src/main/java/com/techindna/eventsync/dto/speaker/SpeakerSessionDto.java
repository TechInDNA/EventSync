package com.techindna.eventsync.dto.speaker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.entity.Session;

@JsonPropertyOrder({"id", "title", "description", "startDate", "endDate", "room", "capacity", "event", "isLive"})
public class SpeakerSessionDto extends Session {
    private boolean isLive;

    @JsonProperty("isLive")
    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
