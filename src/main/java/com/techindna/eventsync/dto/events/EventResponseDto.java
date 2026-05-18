package com.techindna.eventsync.dto.events;

import com.techindna.eventsync.entity.Event;

import java.util.List;

public class EventResponseDto extends Event{
    private List<EventSessionResponseDto> sessions;

    public List<EventSessionResponseDto> getSessions() {
        return sessions;
    }

    public void setSessions(List<EventSessionResponseDto> sessions) {
        this.sessions = sessions;
    }
}
