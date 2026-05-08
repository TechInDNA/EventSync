package com.techindna.eventsync.dto;

import com.techindna.eventsync.entity.User;

public class AuthLoginResponseDto {
    private UserResponseDto user;
    private String token;

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
