package com.bencawley.benspring.dtos;

public class ToDoListRequestDTO {
    private Long listId;
    private String title;
    private String description;

    // Getters and Setters
    public Long getListId() {return listId;}
    public void setListId(Long listId) {this.listId = listId;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
}
