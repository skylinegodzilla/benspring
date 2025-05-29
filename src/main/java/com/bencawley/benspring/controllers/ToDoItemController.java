package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.ToDoItemDTO;
import com.bencawley.benspring.entities.ToDoItemEntity;
import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.repositories.ToDoItemRepository;
import com.bencawley.benspring.mappers.ToDoItemMapper;
import com.bencawley.benspring.repositories.ToDoListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos") // to map all endpoints in this controller to have the prefix /api
public class ToDoItemController {

    private final ToDoItemRepository toDoItemRepository;
    private final ToDoListRepository toDoListRepository;

    public ToDoItemController(ToDoItemRepository toDoItemRepository, ToDoListRepository toDoListRepository) {
        this.toDoItemRepository = toDoItemRepository;
        this.toDoListRepository = toDoListRepository;
    }

    @GetMapping
    public List<ToDoItemDTO> getAllToDoItems() {
        return toDoItemRepository.findAll()
                .stream()
                .map(ToDoItemMapper::toDTO)
                .toList();
    }

    // Post todos
    @PostMapping
    public ToDoItemDTO createToDoItem(@RequestBody ToDoItemDTO dto) {
        ToDoItemEntity entity = ToDoItemMapper.toEntity(dto);
        ToDoItemEntity saved = toDoItemRepository.save(entity);
        return ToDoItemMapper.toDTO(saved);
    }

    //todo add a put to update and a delete to remove

    //todo I dont think these endpoints under this even exsist or work. Or why i have them. Swagger can not see them

//    // Get all todos for the list {listId} //todo still wondering if this should be moved to the other controller since its a todolist endpoint
//    @GetMapping("/todolists/{listId}/todos")
//    public ResponseEntity<List<ToDoItemDTO>> getTodosForList(@PathVariable Long listId) {
//        if (!toDoListRepository.existsById(listId)) {
//            return ResponseEntity.notFound().build();
//        }
//        List<ToDoItemDTO> items = toDoItemRepository.findByToDoListId(listId)
//                .stream()
//                .map(ToDoItemMapper::toDTO)
//                .toList();
//        return ResponseEntity.ok(items);
//    }
//
//    //Create a todo under a specific list
//    @PostMapping("/todolists/{listId}/todos")
//    public ResponseEntity<ToDoItemDTO> addItemToList(
//            @PathVariable Long listId,
//            @RequestBody ToDoItemDTO dto) {
//
//        ToDoListEntity list = toDoListRepository.findById(listId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "To-do list not found"));
//
//        ToDoItemEntity entity = ToDoItemMapper.toEntity(dto);
//        entity.setToDoList(list);
//
//        ToDoItemEntity saved = toDoItemRepository.save(entity);
//        return ResponseEntity.ok(ToDoItemMapper.toDTO(saved));
//    }
//
//    @PutMapping("/todolists/{listId}/todos/{todoId}")
//    public ResponseEntity<?> updateTodo(
//            @PathVariable Long listId,
//            @PathVariable Long todoId,
//            @RequestBody ToDoItemDTO dto
//    ) {
//        // Validate that the list exists
//        if (!toDoListRepository.existsById(listId)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "status", 404,
//                    "message", "List not found"
//            ));
//        }
//
//        // Find the todo item
//        Optional<ToDoItemEntity> optionalItem = toDoItemRepository.findById(todoId);
//        if (optionalItem.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                    "status", 404,
//                    "message", "To-do item not found"
//            ));
//        }
//
//        ToDoItemEntity item = optionalItem.get();
//
//        // Make sure the item belongs to the specified list
//        if (!item.getToDoList().getId().equals(listId)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
//                    "status", 400,
//                    "message", "To-do item does not belong to the specified list"
//            ));
//        }
//
//        // Update the fields
//        item.setTitle(dto.getTitle());
//        item.setCompleted(dto.isCompleted());
//        item.setDueDate(dto.getDueDate());
//
//        // Save the updated item
//        toDoItemRepository.save(item);
//
//        return ResponseEntity.ok(ToDoItemMapper.toDTO(item));
//    }
}
