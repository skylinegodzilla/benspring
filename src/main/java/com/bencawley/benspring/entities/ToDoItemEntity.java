package com.bencawley.benspring.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

/*
    So an entity is basicley an object that can be saved into a database.

    Imagination a Table as an Object Type
    and a Row as an Instance of that Type.
    An Entity would then be the Class of the Object.

    So here we are defining the Table like it is an Object Type
    with each coulomb being a member value
    and each row being an instance.

    However, keep in mind this is just a metaphor.
    For example, you have to crate the entity and populate it,
    but then you also need to SAVE it to the database before it can become a row.
    Unlike an actual object witch is automatically instantiated as soon as you create it.

    And no pointers are not part of this metaphor.

*/

@Entity
@Table(name = "todo_items")
public class ToDoItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "description", length = 1000) // not needed but is a nice to have to customise the table column
    private String description;

    private Boolean completed = false;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "position")
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    @JsonBackReference
    private ToDoListEntity list;

    // Getters and setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public Boolean isCompleted() {return completed;}
    public void setCompleted(Boolean completed) {this.completed = completed;}

    public LocalDate getDueDate() {return dueDate;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}

    public Integer getPosition() {return position;}
    public void setPosition(Integer position) {this.position = position;}

    public ToDoListEntity getList() {return list;}
    public void setList(ToDoListEntity list) {this.list = list;}
}