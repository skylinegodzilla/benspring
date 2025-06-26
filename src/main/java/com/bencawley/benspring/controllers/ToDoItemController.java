package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.ToDoItemRequestDTO;
import com.bencawley.benspring.dtos.ToDoItemResponseDTO;
import com.bencawley.benspring.entities.ToDoItemEntity;
import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.repositories.ToDoItemRepository;
import com.bencawley.benspring.repositories.ToDoListRepository;
import com.bencawley.benspring.mappers.ToDoItemMapper;
import com.bencawley.benspring.services.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/todolists/{listId}/items")
public class ToDoItemController {

    private final ToDoItemRepository toDoItemRepository;
    private final ToDoListRepository toDoListRepository;
    private final SessionService sessionService;

    public ToDoItemController(ToDoItemRepository toDoItemRepository,
                              ToDoListRepository toDoListRepository,
                              SessionService sessionService) {
        this.toDoItemRepository = toDoItemRepository;
        this.toDoListRepository = toDoListRepository;
        this.sessionService = sessionService;
    }

    /**
     * List all items in the specified to-do list.
     * Requires a valid authorization token.
     *
     * @param token   Authorization header with session token
     * @param listId  ID of the to-do list
     * @return List of ToDoItemResponseDTO or empty list if unauthorized/not found
     */
    // List all items in the specified list
    @GetMapping
    public ResponseEntity<List<ToDoItemResponseDTO>> getAllItemsForList(
            @RequestHeader("Authorization") String token,
            @PathVariable Long listId
    ) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(List.of());

        Optional<ToDoListEntity> listOpt = toDoListRepository.findById(listId);
        if (listOpt.isEmpty() || !listOpt.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        List<ToDoItemResponseDTO> items = toDoItemRepository.findByList_Id(listId)
                .stream()
                .map(ToDoItemMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(items);
    }

    /**
     * Create a new item in the specified to-do list.
     *
     * @param token      Authorization header with session token
     * @param listId     ID of the to-do list
     * @param dtoRequest Item data from request
     * @return Created ToDoItemResponseDTO or error status
     */
    // ✅ Create an item in the list
    @PostMapping
    public ResponseEntity<ToDoItemResponseDTO> createItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long listId,
            @RequestBody ToDoItemRequestDTO dtoRequest
    ) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ToDoListEntity> listOpt = toDoListRepository.findById(listId);
        if (listOpt.isEmpty() || !listOpt.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ToDoItemEntity saved = toDoItemRepository.save(ToDoItemMapper.toEntity(dtoRequest, listOpt.get()));
        return ResponseEntity.ok(ToDoItemMapper.toResponseDTO(saved));
    }

    /**
     * Get a single item by ID from the specified to-do list.
     *
     * @param token  Authorization header with session token
     * @param listId ID of the to-do list
     * @param itemId ID of the to-do item
     * @return ToDoItemResponseDTO or error status
     */
    // ✅ Get a single item
    @GetMapping("/{itemId}")
    public ResponseEntity<ToDoItemResponseDTO> getItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long listId,
            @PathVariable Long itemId
    ) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ToDoListEntity> listOpt = toDoListRepository.findById(listId);
        if (listOpt.isEmpty() || !listOpt.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<ToDoItemEntity> itemOpt = toDoItemRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getList().getId().equals(listId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(ToDoItemMapper.toResponseDTO(itemOpt.get()));
    }

    /**
     * Update an existing item in the specified to-do list.
     *
     * @param token  Authorization header with session token
     * @param listId ID of the to-do list
     * @param itemId ID of the to-do item
     * @param dto    Item data to update
     * @return Updated ToDoItemResponseDTO or error status
     */
    // ✅ Update an item
    @PutMapping("/{itemId}")
    public ResponseEntity<ToDoItemResponseDTO> updateItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @RequestBody ToDoItemRequestDTO dto
    ) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ToDoListEntity> listOpt = toDoListRepository.findById(listId);
        if (listOpt.isEmpty() || !listOpt.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<ToDoItemEntity> itemOpt = toDoItemRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getList().getId().equals(listId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ToDoItemEntity entity = itemOpt.get();
        entity.setTitle(dto.getTitle());
        entity.setCompleted(dto.isCompleted());
        entity.setDueDate(dto.getDueDate());

        ToDoItemEntity updated = toDoItemRepository.save(entity);
        return ResponseEntity.ok(ToDoItemMapper.toResponseDTO(updated));
    }

    /**
     * Delete an item by ID from the specified to-do list.
     *
     * @param token  Authorization header with session token
     * @param listId ID of the to-do list
     * @param itemId ID of the to-do item
     * @return Success message or error status
     */
    // ✅ Delete an item
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long listId,
            @PathVariable Long itemId
    ) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Unauthorized"));

        Optional<ToDoListEntity> listOpt = toDoListRepository.findById(listId);
        if (listOpt.isEmpty() || !listOpt.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","List not found"));
        }

        Optional<ToDoItemEntity> itemOpt = toDoItemRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getList().getId().equals(listId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Item not found"));
        }

        toDoItemRepository.delete(itemOpt.get());
        return ResponseEntity.ok(Map.of("message","Item deleted successfully"));
    }
}
