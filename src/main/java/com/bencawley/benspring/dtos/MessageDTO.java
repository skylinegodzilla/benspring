package com.bencawley.benspring.dtos;

public class MessageDTO {
    private String message;

    public MessageDTO(String message) {
        this.message = message;
    }

    // Getter and setter
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
}
