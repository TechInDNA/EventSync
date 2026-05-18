package com.techindna.eventsync.dto.events;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.entity.Room;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonPropertyOrder({"id", "title", "description", "startDate", "endDate", "rooms", "capacity", "live"})
public class EventSessionResponseDto {
    private UUID id;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private List<Room> rooms;
    private int capacity;
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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}
