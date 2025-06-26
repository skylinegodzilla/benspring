package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.ToDoListRequestDTO;
import com.bencawley.benspring.dtos.ToDoListResponseDTO;
import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.entities.UserEntity;
import com.bencawley.benspring.mappers.ToDoListMapper;
import com.bencawley.benspring.repositories.ToDoListRepository;
import com.bencawley.benspring.repositories.UserRepository;
import com.bencawley.benspring.services.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//TODO: Note to self its bad to send userID's through requests as this allows a bad actor to modify some one else's data even if they are just using there own session token
//TODO: to remedy this pare a users session token with there userID  that way the server can look up the all the data it needs with just the session token and no need to send a user id back and throw.

@RestController
@RequestMapping("/api/todolists")
public class ToDoListController {

    private final ToDoListRepository listRepo;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    public ToDoListController(ToDoListRepository listRepo,
                              SessionService sessionService,
                              UserRepository userRepository) {
        this.listRepo = listRepo;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<ToDoListResponseDTO>> getAllLists(@RequestHeader("Authorization") String token) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<ToDoListResponseDTO> lists = listRepo.findByUser_Id(userId).stream()
                .map(ToDoListMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }

    @PostMapping
    public ResponseEntity<ToDoListResponseDTO> createList(
            @RequestHeader("Authorization") String token,
            @RequestBody ToDoListRequestDTO dto
    ) {
        // session check
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // fetch user from database
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // map entity and assign the user
        ToDoListEntity entity = ToDoListMapper.toEntity(dto);
        entity.setUser(user);

        // save to db
        ToDoListEntity saved = listRepo.save(entity);

        return ResponseEntity.ok(ToDoListMapper.toResponseDTO(saved));
    }

    @PutMapping
    public ResponseEntity<ToDoListResponseDTO> updateList(
            @RequestHeader("Authorization") String token,
            @RequestBody ToDoListRequestDTO dto
    ) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ToDoListEntity> opt = listRepo.findById(dto.getListId());
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        ToDoListEntity entity = opt.get();
        if (!entity.getUser().getId().equals(userId)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // checking to see if the entity (the list) users id is the same as the actual user id to authorise if they can edit it

        entity.setTitle(dto.getTitle());
        // You can also add item update logic here

        ToDoListEntity updated = listRepo.save(entity);
        return ResponseEntity.ok(ToDoListMapper.toResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<ToDoListResponseDTO>> deleteList(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        Long userId = sessionService.validateSession(token);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ToDoListEntity> opt = listRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        ToDoListEntity entity = opt.get();
        if (!entity.getUser().getId().equals(userId)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        listRepo.deleteById(id);

        List<ToDoListResponseDTO> lists = listRepo.findByUser_Id(userId).stream()
                .map(ToDoListMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lists);
    }
}