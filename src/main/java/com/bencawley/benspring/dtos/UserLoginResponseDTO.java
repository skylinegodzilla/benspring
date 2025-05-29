package com.bencawley.benspring.dtos;

// At the moment this is the same as the UserRegistrationResponseDTO, but I want to keep them separate in case of changes.

public class UserLoginResponseDTO {
    private String token;
    private Long userId;
    private int status;
    private String message;

    // Constructor
    public UserLoginResponseDTO(String token, Long userId, int status, String message) {
        this.token = token;
        this.userId = userId;
        this.status = status;
        this.message = message;
    }

    // getters and setters

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getUserId() { return userId; }
    public void setUserId(int status) { this.userId = userId; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
