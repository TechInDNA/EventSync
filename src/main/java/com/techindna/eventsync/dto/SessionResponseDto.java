package com.techindna.eventsync.dto;

import com.techindna.eventsync.entity.Session;

public class SessionResponseDto extends Session {
    private boolean isLive;

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
