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

        List<ToDoItemResponseDTO> items = toDoItemRepository.findByList_IdOrderByPosition(listId)
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

        /*
            TODO: think about making a service for ToDoItem and ToDoList
            A controller should mostly just be an entry point for API calls and a tool to build and return a response.
            All the calculations needed to build said response should be abstracted away in to services.
            This already has that for session services but not for the ToDoItem like this logic below where we calculate what
            the  max position value for a list for a new item being created. It is ok for now because this is not much
            logic but its still good practices so that these controllers do not become bloated.
        */
        // Get the next position for the new item in this list
        int maxPosition = toDoItemRepository.findMaxPositionByListId(listId).orElse(0);
        ToDoItemEntity newItem = ToDoItemMapper.toEntity(dtoRequest, listOpt.get());
        newItem.setPosition(maxPosition + 1);

        // Save and return response
        ToDoItemEntity saved = toDoItemRepository.save(newItem);
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

    /**
     * Reorder the items in a specific to-do list by their new order of IDs.
     *
     * @param token          Authorization header with session token
     * @param listId         ID of the to-do list
     * @param orderedItemIds List of item IDs in the new order
     * @return 200 OK if reorder is successful, or an error status
     */
// ✅ Reorder items within the list
    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderItems(
            @RequestHeader("Authorization") String token,
            @PathVariable Long listId,
            @RequestBody List<Long> orderedItemIds
    ) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ToDoListEntity> listOpt = toDoListRepository.findById(listId);
        if (listOpt.isEmpty() || !listOpt.get().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    /*
        TODO: Refactor this into a ToDoItemService if the logic expands.
        This loop does simple position updating but will grow more complex
        once reordering rules or validation expand.
    */
        for (int i = 0; i < orderedItemIds.size(); i++) {
            Long itemId = orderedItemIds.get(i);

            ToDoItemEntity item = toDoItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

            if (!item.getList().getId().equals(listId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            item.setPosition(i);
            toDoItemRepository.save(item);
        }

        return ResponseEntity.ok().build();
    }

}
