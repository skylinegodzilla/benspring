package com.bencawley.benspring.dtos;

import java.time.LocalDate;

// think of this as a response object in swift but this will be converted in to json not from json

public class ToDoItemResponseDTO {
    private Long itemId;
    private String title;
    private String description;
    private boolean completed;
    private LocalDate dueDate;
    private Integer position;

    // Getters and Setters
    public Long getItemId() {return itemId;}
    public void setItemId(Long itemId) {this.itemId = itemId;}

    public String getTitle() {return title;}
    public void setTitle(String name) {this.title = name;}

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description;}

    public boolean isCompleted() {return completed;}
    public void setCompleted(boolean completed) {this.completed = completed;}

    public LocalDate getDueDate() {return dueDate;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}

    public Integer getPosition() {return position;}
    public void setPosition(Integer position) {this.position = position;}
}
