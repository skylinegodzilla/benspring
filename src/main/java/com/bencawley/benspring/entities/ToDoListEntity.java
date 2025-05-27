package com.bencawley.benspring.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todo_lists")
public class ToDoListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // Linking to items
    @OneToMany(
            mappedBy = "toDoList",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference //  marks the forward part of the reference (parent → child) to prevent the list becoming recurse infinitely when serializing.
    private List<ToDoItemEntity> items = new ArrayList<>();

    // Linking to user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference //  marks the back part of the reference (child → parent) to prevent the list becoming recurse infinitely when serializing.
    private UserEntity user;

    // Getters and setters

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


    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<ToDoItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ToDoItemEntity> items) {
        this.items = items;
    }
}
