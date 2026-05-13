package com.techindna.eventsync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techindna.eventsync.entity.Session;

public class SessionResponseDto extends Session {
    private boolean isLive;

    @JsonProperty("isLive")
    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
