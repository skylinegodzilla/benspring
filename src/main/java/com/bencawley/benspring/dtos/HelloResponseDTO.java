package com.bencawley.benspring.dtos;

// This represents what the server sends back in the HTTP response
public class HelloResponseDTO {
    private String result;

    public HelloResponseDTO(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
