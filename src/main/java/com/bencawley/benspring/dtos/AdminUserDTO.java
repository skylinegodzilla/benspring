package com.bencawley.benspring.dtos;

import com.bencawley.benspring.utilities.UserRole;

public class AdminUserDTO {
    private String username;
    private String email;
    private UserRole role;

    public AdminUserDTO(String username, String email, UserRole role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters and setters

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public UserRole getRole() {return role;}
    public void setRole(UserRole role) {this.role = role;}
}
