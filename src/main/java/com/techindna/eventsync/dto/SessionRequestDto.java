package com.techindna.eventsync.dto;

public class SessionRequestDto {
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private RoomRef room;
    private int capacity;
    private EventRef event;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public RoomRef getRoom() { return room; }
    public void setRoom(RoomRef room) { this.room = room; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public EventRef getEvent() { return event; }
    public void setEvent(EventRef event) { this.event = event; }

    public String getRoomId() { return room != null ? room.getId() : null; }
    public String getEventId() { return event != null ? event.getId() : null; }

    public static class RoomRef {
        private String id;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    public static class EventRef {
        private String id;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
}
