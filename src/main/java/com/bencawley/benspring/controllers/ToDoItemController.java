package com.bencawley.benspring.controllers;

import com.bencawley.benspring.model.ToDoItem;
import com.bencawley.benspring.repository.ToDoItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ToDoItemController {

    private final ToDoItemRepository toDoItemRepository;

    public ToDoItemController(ToDoItemRepository toDoItemRepository) {
        this.toDoItemRepository = toDoItemRepository;
    }

    @GetMapping("/todos")
    public List<ToDoItem> getAllToDoItems() {
        return toDoItemRepository.findAll();
    }
}
