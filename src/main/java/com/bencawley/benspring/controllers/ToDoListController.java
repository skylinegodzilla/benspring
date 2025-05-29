package com.bencawley.benspring.controllers;

import com.bencawley.benspring.dtos.ToDoListDTO;
import com.bencawley.benspring.entities.ToDoListEntity;
import com.bencawley.benspring.mappers.ToDoListMapper;
import com.bencawley.benspring.repositories.ToDoListRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todolists")
public class ToDoListController {

    private final ToDoListRepository listRepo;

    public ToDoListController(ToDoListRepository listRepo) {
        this.listRepo = listRepo;
    }

    @GetMapping
    public List<ToDoListDTO> getAllLists() {
        return listRepo.findAll().stream()
                .map(ToDoListMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ToDoListDTO createList(@RequestBody ToDoListDTO dto) {
        ToDoListEntity entity = ToDoListMapper.toEntity(dto);
        ToDoListEntity saved = listRepo.save(entity);
        return ToDoListMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public void deleteList(@PathVariable Long id) {
        listRepo.deleteById(id);
    }

    // endpoint to fetch lists by userId // todo this is wrong because its nested inside the /api/todolists endpoint :P it should just be rethought about
    @GetMapping("/user/{userId}")
    public List<ToDoListDTO> getListsByUserId(@PathVariable Long userId) {
        List<ToDoListEntity> lists = listRepo.findByUserId(userId);
        return lists.stream()
                .map(ToDoListMapper::toDto)
                .collect(Collectors.toList());
    }
}
