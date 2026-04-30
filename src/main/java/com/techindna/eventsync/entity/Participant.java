package com.techindna.eventsync.entity;


import com.techindna.eventsync.entity.enums.Role;

public class Participant extends User {
    @Override
    public Role getRole() {
        return Role.PARTICIPANT;
    }
}
