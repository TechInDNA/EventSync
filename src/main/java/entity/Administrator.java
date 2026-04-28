package entity;

import entity.enums.Role;

public class Administrator extends User {
    private String password;
    private String username;

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
