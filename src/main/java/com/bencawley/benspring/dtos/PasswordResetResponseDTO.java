package com.bencawley.benspring.dtos;

public class PasswordResetResponseDTO {
    private final String message;
    private final String newPassword;

    public PasswordResetResponseDTO(String message, String newPassword) {
        this.message = message;
        this.newPassword = newPassword;
    }

    public String getMessage() {
        return message;
    }

    public String getNewPassword() {
        return newPassword;
    }
}