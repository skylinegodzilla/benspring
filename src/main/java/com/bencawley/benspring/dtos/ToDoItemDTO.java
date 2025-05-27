package com.bencawley.benspring.dtos;

import java.time.LocalDate;

// think of this as a responce object in swift but this will be converted in to json not from json

public class ToDoItemDTO {
    private Long id;
    private String title;
    private boolean completed;
    private LocalDate dueDate;

    // Getters, setters, constructors

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
