package com.bencawley.benspring.dtos;

public class LogOutResponseDTO {
    private boolean success;
    private String message;

    public boolean isSuccess() {return success;}
    public void setSuccess(boolean success) {this.success = success;}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
}
