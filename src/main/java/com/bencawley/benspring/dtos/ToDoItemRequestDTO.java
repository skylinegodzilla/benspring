package com.bencawley.benspring.dtos;

import java.time.LocalDate;

public class ToDoItemRequestDTO {
    private String title;
    private String description;
    private boolean completed;
    private Long listId;
    private LocalDate dueDate;
    private Integer position;

    // Getters and Setters
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public boolean isCompleted() {return completed;}
    public void setCompleted(boolean completed) {this.completed = completed;}

    public Long getListId() {return listId;}
    public void setListId(Long listId) {this.listId = listId;}

    public LocalDate getDueDate() {return dueDate;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}

    public Integer getPosition() {return position;}
    public void setPosition(Integer position) {this.position = position;}
}
