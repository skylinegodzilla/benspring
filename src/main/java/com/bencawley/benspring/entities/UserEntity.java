package com.bencawley.benspring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
// jpa means Java persistence API its used for mapping java objects to relational databases
/*
JPA handles:
-Mapping classes to tables: @Entity, @Table
-Mapping fields to columns: @Column, @Id
-Relationships: @OneToMany, @ManyToOne, etc.
- Database operations: via JpaRepository, so you can save(), findAll(), etc.
 */
@Entity // marks a class as a JPA entity — meaning it's a class that maps to a table in your database. in this case the class is User
public class UserEntity {

    @Id // this makes this class member the primary key of the entity. Each user will have its own uniqe id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tells JPA to auto-generate the ID using the database's identity column (auto-incrementing integer in PostgreSQL).
    private Long id;

    //The members in the class become the columns in the table.
    @Column(unique = true, nullable = false) // unique means each username has to be unique, nullable = false means this has to have a value it cant be null
    private String username;

    //The members in the class become the columns in the table.
    @JsonIgnore //  is a Jackson annotation (not JPA) that prevents this field from being included in API responses
    @Column(nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference //  marks the forward part of the reference (parent → child) to prevent the list becoming recurse infinitely when serializing.
    private List<ToDoListEntity> todoLists = new ArrayList<>();

    public UserEntity() {}

    public UserEntity(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Getters and setters

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<ToDoListEntity> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(List<ToDoListEntity> todoLists) {
        this.todoLists = todoLists;
    }
}
