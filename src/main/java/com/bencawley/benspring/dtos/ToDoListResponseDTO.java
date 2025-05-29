package com.bencawley.benspring.dtos;

import java.util.List;

public class ToDoListResponseDTO {
    private Long listId;
    private String title;
    private String description;
    private List<ToDoItemResponseDTO> items;

    // Getters and Setters
    public Long getListId() {return listId;}
    public void setListId(Long listId) {this.listId = listId;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public List<ToDoItemResponseDTO> getItems() {return items;}
    public void setItems(List<ToDoItemResponseDTO> items) {this.items = items;}
}