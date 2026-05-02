package com.techindna.eventsync.entity;


import com.techindna.eventsync.entity.enums.Role;

import java.util.List;

public class Speaker extends User {
    private String bio;
    private String profilePicture;
    private List<ExternalLinks> urls;

    @Override
    public Role getRole() {
        return Role.SPEAKER;
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

    public List<ExternalLinks> getUrls() {
        return urls;
    }

    public void setUrls(List<ExternalLinks> urls) {
        this.urls = urls;
    }
}
