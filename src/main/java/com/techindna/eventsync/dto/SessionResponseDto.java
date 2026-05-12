package com.techindna.eventsync.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.entity.Event;
import com.techindna.eventsync.entity.Room;
import com.techindna.eventsync.entity.Speaker;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonPropertyOrder({"id", "title", "description", "startDate", "endDate", "room", "capacity", "event", "speakers", "isLive"})
public class SessionResponseDto {
    private UUID id;
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private Room room;
    private Event event;
    private List<Speaker> speakers;
    private boolean isLive;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
