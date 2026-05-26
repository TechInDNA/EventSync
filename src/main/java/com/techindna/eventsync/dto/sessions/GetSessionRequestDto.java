package com.techindna.eventsync.dto.sessions;

public class GetSessionRequestDto {
  private String roomName;
  private String speakerName;
  private String eventTitle;
  private boolean isLive;

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public String getSpeakerName() {
    return speakerName;
  }

  public void setSpeakerName(String speakerName) {
    this.speakerName = speakerName;
  }

  public String getEventTitle() {
    return eventTitle;
  }

  public void setEventTitle(String eventTitle) {
    this.eventTitle = eventTitle;
  }

  public boolean isLive() {
    return isLive;
  }

  public void setLive(boolean live) {
    isLive = live;
  }
}
