package com.techindna.eventsync.dto.auth;

import com.techindna.eventsync.dto.UserResponseDto;

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
