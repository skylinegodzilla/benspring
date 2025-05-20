package com.bencawley.benspring.dtos;

// This is a simple POJO (Plain Old Java Object) that represents incoming JSON data
public class HelloDTO {
    private String input;

    // Required: no-argument constructor (needed for deserialization)
    public HelloDTO() {}

    // Getter for 'input' (used by Spring to read the value)
    public String getInput() {
        return input;
    }

    // Setter for 'input' (used by Spring to write the value)
    public void setInput(String input) {
        this.input = input;
    }
}