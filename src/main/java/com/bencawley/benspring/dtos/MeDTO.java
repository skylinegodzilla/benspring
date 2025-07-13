package com.bencawley.benspring.dtos;

import com.bencawley.benspring.utilities.UserRole;

public class MeDTO {
    private String username;
    private UserRole role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
