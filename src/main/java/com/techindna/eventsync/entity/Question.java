package com.techindna.eventsync.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;
import java.util.UUID;

@JsonPropertyOrder({"id", "title", "content", "participant", "upvotes", "isAnonymous", "createdAt"})
public class Question {
    private UUID id;
    private String title;
    private String content;
    private ParticipantRef participant;
    private int upvotes;
    private boolean isAnonymous;
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public ParticipantRef getParticipant() { return participant; }
    public void setParticipant(ParticipantRef participant) { this.participant = participant; }

    public int getUpvotes() { return upvotes; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }

    public boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
