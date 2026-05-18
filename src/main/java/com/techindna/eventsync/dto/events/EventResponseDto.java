package com.techindna.eventsync.dto.events;

import com.techindna.eventsync.entity.Event;

import java.util.List;

public class EventResponseDto {
    private final Event event;
    private final List<EventSessionResponseDto> sessions;

    public EventResponseDto(Event event, List<EventSessionResponseDto> sessions) {
        this.event = event;
        this.sessions = sessions;
    }

    public Event getEvent() {
        return event;
    }

    public List<EventSessionResponseDto> getSessions() {
        return sessions;
    }
}
