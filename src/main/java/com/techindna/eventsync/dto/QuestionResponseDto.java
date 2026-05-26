package com.techindna.eventsync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public class QuestionResponseDto {
  private UUID id;
  private String title;
  private String content;
  private ParticipantDto participant;
  private int upvotes;

  @JsonProperty("isAnonymous")
  private boolean isAnonymous;

  private Instant createdAt;

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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ParticipantDto getParticipant() {
    return participant;
  }

  public void setParticipant(ParticipantDto participant) {
    this.participant = participant;
  }

  public int getUpvotes() {
    return upvotes;
  }

  public void setUpvotes(int upvotes) {
    this.upvotes = upvotes;
  }

  @JsonProperty("isAnonymous")
  public boolean isAnonymous() {
    return isAnonymous;
  }

  @JsonProperty("isAnonymous")
  public void setAnonymous(boolean anonymous) {
    isAnonymous = anonymous;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
