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
    public ResponseEntity<ToDoListResponseDTO> createList(@RequestHeader("Authorization") String token,
                                                  @RequestBody ToDoListRequestDTO dto) {
        // session check
        Long userId = sessionService.validateSession(token);
        if (userId == null || !userId.equals(dto.getUserId())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

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
    public ResponseEntity<ToDoListResponseDTO> updateList(@RequestHeader("Authorization") String token,
                                                  @RequestBody ToDoListRequestDTO dto) {
        Long userId = sessionService.validateSession(token);
        if (userId == null || !userId.equals(dto.getUserId())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ToDoListEntity> opt = listRepo.findById(dto.getUserId());
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        ToDoListEntity entity = opt.get();
        if (!entity.getUser().getId().equals(userId)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        entity.setTitle(dto.getTitle());
        // You can also add item update logic here

        ToDoListEntity updated = listRepo.save(entity);
        return ResponseEntity.ok(ToDoListMapper.toResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<ToDoListResponseDTO>> deleteList(@RequestHeader("Authorization") String token,
                                                        @PathVariable Long id) {
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