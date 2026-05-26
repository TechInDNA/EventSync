package com.techindna.eventsync.dto.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.entity.Room;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonPropertyOrder({
  "id",
  "title",
  "description",
  "startDate",
  "endDate",
  "rooms",
  "capacity",
  "isLive"
})
public class EventSessionResponseDto {
  private UUID id;
  private String title;
  private String description;
  private Instant startDate;
  private Instant endDate;
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

  @JsonProperty("isLive")
  public boolean isLive() {
    return isLive;
  }

  public void setLive(boolean live) {
    isLive = live;
  }
}
