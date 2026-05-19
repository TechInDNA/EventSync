package com.techindna.eventsync.dto.speaker;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.dto.ExternalLinkDto;

import java.util.List;
import java.util.UUID;

@JsonPropertyOrder({"id", "firstName", "lastName", "profilePicture", "bio", "externalLinks", "sessions"})
public class SpeakerDetailResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String bio;
    private List<ExternalLinkDto> externalLinks;
    private List<SpeakerSessionDto> sessions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<ExternalLinkDto> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<ExternalLinkDto> externalLinks) {
        this.externalLinks = externalLinks;
    }

    public List<SpeakerSessionDto> getSessions() {
        return sessions;
    }

    public void setSessions(List<SpeakerSessionDto> sessions) {
        this.sessions = sessions;
    }
}
