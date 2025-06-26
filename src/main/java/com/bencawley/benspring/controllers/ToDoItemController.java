package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.ToDoItemResponseDTO;
import com.bencawley.benspring.entities.ToDoItemEntity;
import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.repositories.ToDoItemRepository;
import com.bencawley.benspring.mappers.ToDoItemMapper;
import com.bencawley.benspring.repositories.ToDoListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
public class ToDoItemController {

    private final ToDoItemRepository toDoItemRepository;
    private final ToDoListRepository toDoListRepository;

    public ToDoItemController(ToDoItemRepository toDoItemRepository, ToDoListRepository toDoListRepository) {
        this.toDoItemRepository = toDoItemRepository;
        this.toDoListRepository = toDoListRepository;
    }

    // GET all to-do items // todo might need to remove this endpoint call
    @GetMapping
    public ResponseEntity<List<ToDoItemResponseDTO>> getAllToDoItems() {
        List<ToDoItemResponseDTO> items = toDoItemRepository.findAll()
                .stream()
                .map(ToDoItemMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(items);
    }

    // POST a new to-do item // todo might need to change this to  @PostMapping("/{id}")
    @PostMapping // todo replace this object with a dto
    public ResponseEntity<Object> createToDoItem(@RequestBody ToDoItemResponseDTO dto) {
        Optional<ToDoListEntity> optionalList = toDoListRepository.findById(dto.getItemId());
        if (optionalList.isEmpty()) {
            Map<String, String> error = Map.of("error", "ToDoList with ID " + dto.getItemId() + " not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        ToDoListEntity list = optionalList.get();
        ToDoItemEntity entity = ToDoItemMapper.toEntity(dto, list);
        ToDoItemEntity saved = toDoItemRepository.save(entity);
        return ResponseEntity.ok(ToDoItemMapper.toResponseDTO(saved));
    }

    // GET a single to-do item by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getToDoItemById(@PathVariable Long id) {
        //todo: Might have to check that the sessiontoken used in this matches the id if not then deny them access but test first making this change
        Optional<ToDoItemEntity> optionalItem = toDoItemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "ToDoItem with ID " + id + " not found"));
        }

        return ResponseEntity.ok(ToDoItemMapper.toResponseDTO(optionalItem.get()));
    }

    // DELETE a to-do item by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteToDoItem(@PathVariable Long id) {
        Optional<ToDoItemEntity> optionalItem = toDoItemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "ToDoItem with ID " + id + " not found"));
        }

        toDoItemRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "ToDoItem deleted successfully"));
    }

    // PUT update a to-do item
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateToDoItem(@PathVariable Long id, @RequestBody ToDoItemResponseDTO dto) {
        Optional<ToDoItemEntity> optionalItem = toDoItemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "ToDoItem with ID " + id + " not found"));
        }

        Optional<ToDoListEntity> optionalList = toDoListRepository.findById(dto.getItemId());
        if (optionalList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "ToDoList with ID " + dto.getItemId() + " not found"));
        }

        ToDoItemEntity entity = ToDoItemMapper.toEntity(dto, optionalList.get());
        entity.setId(id); // Preserve the existing ID
        ToDoItemEntity updated = toDoItemRepository.save(entity);
        return ResponseEntity.ok(ToDoItemMapper.toResponseDTO(updated));
    }
}
