package com.bencawley.benspring.controllers;

import com.bencawley.benspring.model.ToDoItem;
import com.bencawley.benspring.repository.ToDoItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ToDoItemController {

    private final ToDoItemRepository toDoItemRepository;

    public ToDoItemController(ToDoItemRepository toDoItemRepository) {
        this.toDoItemRepository = toDoItemRepository;
    }

    @GetMapping("/api/todos")
    public List<ToDoItem> getAllToDoItems() {
        return toDoItemRepository.findAll();
    }

    @PostMapping("/api/todos")
    public ToDoItem createToDoItem(@RequestBody ToDoItem newItem) {
        return toDoItemRepository.save(newItem);
    }

    @DeleteMapping("/api/todos/{id}")
    public ResponseEntity<Void> deleteToDoItem(@PathVariable Long id) {
        if (!toDoItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        toDoItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
