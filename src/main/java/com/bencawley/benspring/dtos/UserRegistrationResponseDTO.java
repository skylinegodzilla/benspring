package com.bencawley.benspring.dtos;

public class UserRegistrationResponseDTO {
    private String token;
    private int status;
    private String message;

    public UserRegistrationResponseDTO(String token, int status, String message) {
        this.token = token;
        this.status = status;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
