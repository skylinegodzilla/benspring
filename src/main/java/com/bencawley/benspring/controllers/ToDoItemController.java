package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.ToDoItemDTO;
import com.bencawley.benspring.entities.ToDoItemEntity;
import com.bencawley.benspring.repositories.ToDoItemRepository;
import com.bencawley.benspring.mappers.ToDoItemMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ToDoItemController {

    private final ToDoItemRepository toDoItemRepository;

    public ToDoItemController(ToDoItemRepository toDoItemRepository) {
        this.toDoItemRepository = toDoItemRepository;
    }

    @GetMapping("/api/todos")
    public List<ToDoItemDTO> getAllToDoItems() {
        return toDoItemRepository.findAll()
                .stream()
                .map(ToDoItemMapper::toDTO)
                .toList();
    }

    @PostMapping("/api/todos")
    public ToDoItemDTO createToDoItem(@RequestBody ToDoItemDTO dto) {
        ToDoItemEntity entity = ToDoItemMapper.toEntity(dto);
        ToDoItemEntity saved = toDoItemRepository.save(entity);
        return ToDoItemMapper.toDTO(saved);
    }
}
