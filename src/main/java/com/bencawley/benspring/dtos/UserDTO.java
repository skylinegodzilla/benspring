package com.bencawley.benspring.dtos;

import java.util.List;

public class UserDTO {

    private Long id;
    private String username;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
