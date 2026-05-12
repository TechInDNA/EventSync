package com.techindna.eventsync.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.dto.SpeakerRefDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonPropertyOrder({"id", "title", "description", "startDate", "endDate", "room", "capacity", "event", "speakers", "questions", "isLive"})
public class Session {
    private UUID id;
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private Room room;
    private int capacity;
    private EventRef event;
    private List<SpeakerRefDto> speakers;
    private List<Question> questions;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }

    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public EventRef getEvent() { return event; }
    public void setEvent(EventRef event) { this.event = event; }

    public List<SpeakerRefDto> getSpeakers() { return speakers; }
    public void setSpeakers(List<SpeakerRefDto> speakers) { this.speakers = speakers; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }



    @JsonProperty("isLive")
    public boolean IsLive() {
        Instant now = Instant.now();
        if (this.startDate == null || this.endDate == null) return false;


         System.out.println("Now: " + now + " | Start: " + startDate + " | End: " + endDate);

        return !now.isBefore(this.startDate) && now.isBefore(this.endDate);
    }
}
