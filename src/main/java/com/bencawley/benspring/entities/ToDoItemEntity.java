package com.bencawley.benspring.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ToDoItemEntity {

    @Id // table id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // genarte the id
    private Long id; // the id
    private String title;
    private boolean completed;
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "list_id", nullable = false) // to link the item to the list that it is apart of
    @JsonBackReference //  marks the back part of the reference (child → parent) to prevent the list becoming recurse infinitely when serializing.
    private ToDoListEntity toDoList; // this holds the id of the list this todo item is apart of

    // Constructors
    public ToDoItemEntity() {}

    public ToDoItemEntity(
            String title,
            boolean completed,
            LocalDate dueDate
    ) {
        this.title = title;
        this.completed = completed;
        this.dueDate = dueDate;
    }

    public ToDoItemEntity(String title, boolean completed) {
        this(title, completed, null);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public ToDoListEntity getToDoList() {
        return toDoList;
    }

    public void setToDoList(ToDoListEntity toDoList) {
        this.toDoList = toDoList;
    }
}
