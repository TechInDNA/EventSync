package com.techindna.eventsync.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techindna.eventsync.entity.enums.Role;

public class Administrator extends User {
  private String password;

  @Override
  public Role getRole() {
    return Role.ADMIN;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @JsonIgnore
  public String getPassword() {
    return password;
  }
}
