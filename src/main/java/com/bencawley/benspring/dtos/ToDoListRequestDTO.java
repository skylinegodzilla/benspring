package com.bencawley.benspring.dtos;

public class ToDoListRequestDTO {
    private String title;
    private String description;
    private Long userId;

    // Getters and Setters
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public long getUserId() {return userId;}
    public void setUserId(long userId) {this.userId = userId;}
}
