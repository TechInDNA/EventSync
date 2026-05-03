package com.techindna.eventsync.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.UUID;

@JsonPropertyOrder({"id", "firstName", "lastName", "profilePicture", "bio", "externalLinks"})
public class SpeakerResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePicture;
    private List<ExternalLinkResponseDto> externalLinks;

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<ExternalLinkResponseDto> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<ExternalLinkResponseDto> externalLinks) {
        this.externalLinks = externalLinks;
    }
}
