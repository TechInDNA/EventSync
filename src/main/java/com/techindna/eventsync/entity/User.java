package com.techindna.eventsync.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.techindna.eventsync.entity.enums.Role;

import java.util.UUID;

@JsonPropertyOrder({"id", "firstName", "lastName", "email", "role"})
public abstract class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public abstract Role getRole();

}
