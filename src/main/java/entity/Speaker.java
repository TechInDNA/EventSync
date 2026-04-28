package entity;

import entity.enums.Role;

public class Speaker extends User {
    private String bio;
    private String profilePicture;

    @Override
    public Role getRole() {
        return Role.SPEAKER;
    }
}
