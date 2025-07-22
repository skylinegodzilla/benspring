package com.bencawley.benspring.dtos;

import com.bencawley.benspring.utilities.UserRole;

public class UserLoginResponseDTO {
    private String token;
    private int status;
    private String message;
    private UserRole role;

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
