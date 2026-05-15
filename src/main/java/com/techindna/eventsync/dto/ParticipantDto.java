package com.techindna.eventsync.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techindna.eventsync.entity.User;
import com.techindna.eventsync.entity.enums.Role;

public class ParticipantDto extends User {
    @Override
    @JsonIgnore
    public Role getRole() {
        return null;
    }
}
