package com.bencawley.benspring.dtos;

// At the moment this is the same as the UserRegistrationResponseDTO, but I want to keep them separate in case of changes.

import com.bencawley.benspring.utilities.UserRole;

public class UserLoginResponseDTO {
    private String token;
    private int status;
    private String message;
    private UserRole role;

    // Constructor
    public UserLoginResponseDTO(
            String token,
            Long userId,
            int status,
            String message,
            UserRole role
    ) {
        this.token = token;
        this.status = status;
        this.message = message;
    }

    // getters and setters

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public  UserRole getRole() {return role; }
    public void setRole(UserRole role) { this.role = role;}
}
